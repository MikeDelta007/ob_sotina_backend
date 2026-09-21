package com.officedubac.project.absence;

import com.officedubac.project.models.Role;
import com.officedubac.project.models.User;
import com.officedubac.project.personnel.Division;
import com.officedubac.project.personnel.DivisionRepository;
import com.officedubac.project.personnel.Personnel;
import com.officedubac.project.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DemandeAbsenceService {

    private final DemandeAbsenceRepository demandeRepo;
    private final DivisionRepository divisionRepo;
    private final UserRepository userRepository;

    public DemandeAbsence creer(DemandeAbsenceRequest req) {
        User demandeur = currentUser();

        if (req.getDateDebut().isBefore(java.time.LocalDate.now())) {
            throw new RuntimeException("La date de début ne peut pas être antérieure à aujourd'hui");
        }
        if (req.getDateFin().isBefore(req.getDateDebut())) {
            throw new RuntimeException("La date de fin ne peut pas précéder la date de début");
        }
        int nombreJours = (int) (ChronoUnit.DAYS.between(req.getDateDebut(), req.getDateFin()) + 1);

        // Le motif n'est obligatoire que pour une AUTORISATION ponctuelle — un CONGE n'a pas
        // besoin de justification.
        if (req.getType() == TypeAbsence.AUTORISATION && (req.getMotif() == null || req.getMotif().isBlank())) {
            throw new RuntimeException("Le motif est obligatoire pour une autorisation d'absence");
        }

        // Le solde disponible pour un CONGE tient compte des AUTORISATION déjà prises et pas
        // encore régularisées (cf. Personnel.getSoldeDisponible()) — elles ne décomptent rien
        // dans l'immédiat, mais réduisent d'autant ce qui reste posable en congé.
        if (req.getType() == TypeAbsence.CONGE) {
            Integer soldeDisponible = demandeur.getPersonnel().getSoldeDisponible();
            if (soldeDisponible != null && nombreJours > soldeDisponible) {
                throw new RuntimeException("La période demandée (" + nombreJours + " jour(s)) dépasse votre solde de congés disponible ("
                        + soldeDisponible + " jour(s))");
            }
        }

        DemandeAbsence demande = DemandeAbsence.builder()
                .demandeurId(demandeur.getId())
                .demandeurNom(demandeur.getPersonnel().getFirstname() + " " + demandeur.getPersonnel().getLastname())
                .divisionId(demandeur.getPersonnel().getDivision() != null ? demandeur.getPersonnel().getDivision().getId() : null)
                .type(req.getType())
                .dateDebut(req.getDateDebut())
                .dateFin(req.getDateFin())
                .nombreJours(nombreJours)
                .motif(req.getMotif())
                .statut(statutDepart(demandeur))
                .creePar(demandeur.getLogin())
                .dateCreation(LocalDateTime.now())
                .build();

        return demandeRepo.save(demande);
    }

    private StatutAbsence statutDepart(User demandeur) {
        Role role = demandeur.getProfil() != null ? demandeur.getProfil().getName() : null;
        if (role == Role.CSA || role == Role.DIRECTEUR || role == Role.ADMIN) {
            return StatutAbsence.EN_ATTENTE_CSA;
        }
        Division division = demandeur.getPersonnel().getDivision() != null
                ? divisionRepo.findById(demandeur.getPersonnel().getDivision().getId()).orElse(null)
                : null;
        if (division == null || division.getChefServiceId() == null || division.getChefServiceId().isBlank()) {
            return StatutAbsence.EN_ATTENTE_CSA;
        }
        if (division.getChefServiceId().equals(demandeur.getId())) {
            return StatutAbsence.EN_ATTENTE_CSA;
        }
        return StatutAbsence.EN_ATTENTE_CHEF;
    }

    public List<DemandeAbsence> mesDemandes(TypeAbsence type) {
        User user = currentUser();
        return demandeRepo.findByDemandeurIdAndTypeOrderByDateCreationDesc(user.getId(), type);
    }

    public List<DemandeAbsence> aValider(TypeAbsence type) {
        User user = currentUser();
        Role role = user.getProfil() != null ? user.getProfil().getName() : null;

        if (role == Role.CSA) {
            return demandeRepo.findByStatutAndTypeOrderByDateCreationDesc(StatutAbsence.EN_ATTENTE_CSA, type);
        }
        if (role == Role.DIRECTEUR) {
            return demandeRepo.findByStatutAndTypeOrderByDateCreationDesc(StatutAbsence.EN_ATTENTE_DIRECTEUR, type);
        }
        if (role == Role.ADMIN) {
            return demandeRepo.findByTypeOrderByDateCreationDesc(type).stream()
                    .filter(d -> d.getStatut() == StatutAbsence.EN_ATTENTE_CHEF
                            || d.getStatut() == StatutAbsence.EN_ATTENTE_CSA
                            || d.getStatut() == StatutAbsence.EN_ATTENTE_DIRECTEUR)
                    .collect(Collectors.toList());
        }

        List<String> divisionsDontJeSuisChef = divisionRepo.findByActifTrue().stream()
                .filter(d -> user.getId().equals(d.getChefServiceId()))
                .map(Division::getId)
                .collect(Collectors.toList());

        if (divisionsDontJeSuisChef.isEmpty()) {
            return List.of();
        }
        return demandeRepo.findByStatutAndTypeAndDivisionIdInOrderByDateCreationDesc(StatutAbsence.EN_ATTENTE_CHEF, type, divisionsDontJeSuisChef);
    }

    // Historique complet (tous statuts) des demandes des agents des divisions dont l'utilisateur
    // est chef — contrairement à aValider(), pas limité aux demandes encore actionnables par lui.
    public List<DemandeAbsence> demandesDeMesAgents(TypeAbsence type) {
        User user = currentUser();

        List<String> divisionsDontJeSuisChef = divisionRepo.findByActifTrue().stream()
                .filter(d -> user.getId().equals(d.getChefServiceId()))
                .map(Division::getId)
                .collect(Collectors.toList());

        if (divisionsDontJeSuisChef.isEmpty()) {
            return List.of();
        }
        return demandeRepo.findByTypeAndDivisionIdInOrderByDateCreationDesc(type, divisionsDontJeSuisChef);
    }

    // Demandes déjà traitées PAR MOI — onglet "Déjà validées". Pour le chef et le CSA (étapes
    // intermédiaires), "déjà traitée" veut dire "j'ai déjà donné mon avis à mon étape", que la
    // chaîne soit terminée ou non plus loin. Pour le Directeur (étape finale) et l'Admin
    // (supervision), ça correspond naturellement aux demandes closes (VALIDEE/REJETEE).
    public List<DemandeAbsence> demandesTraitees(TypeAbsence type) {
        User user = currentUser();
        Role role = user.getProfil() != null ? user.getProfil().getName() : null;

        if (role == Role.CSA) {
            return demandeRepo.findByTypeOrderByDateCreationDesc(type).stream()
                    .filter(d -> d.isValidationCsa() || d.isRejetCsa())
                    .collect(Collectors.toList());
        }
        if (role == Role.DIRECTEUR || role == Role.ADMIN) {
            return demandeRepo.findByTypeOrderByDateCreationDesc(type).stream()
                    .filter(d -> d.getStatut() == StatutAbsence.VALIDEE || d.getStatut() == StatutAbsence.REJETEE)
                    .collect(Collectors.toList());
        }

        List<String> divisionsDontJeSuisChef = divisionRepo.findByActifTrue().stream()
                .filter(d -> user.getId().equals(d.getChefServiceId()))
                .map(Division::getId)
                .collect(Collectors.toList());
        if (divisionsDontJeSuisChef.isEmpty()) {
            return List.of();
        }
        return demandeRepo.findByTypeAndDivisionIdInOrderByDateCreationDesc(type, divisionsDontJeSuisChef).stream()
                .filter(d -> d.isValidationChef() || d.isRejetChef())
                .collect(Collectors.toList());
    }

    public DemandeAbsence valider(String id) {
        return traiterEtape(id, true, null);
    }

    public DemandeAbsence rejeter(String id, String motif) {
        return traiterEtape(id, false, motif);
    }

    // Un rejet du chef ou du CSA n'est qu'un avis enregistré sur son étape — il n'arrête pas la
    // chaîne, qui continue toujours jusqu'au Directeur. Seule la décision du Directeur est
    // finale (VALIDEE ou REJETEE).
    private DemandeAbsence traiterEtape(String id, boolean valide, String motif) {
        DemandeAbsence demande = demandeRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Demande d'absence introuvable"));
        User caller = currentUser();
        LocalDateTime now = LocalDateTime.now();

        switch (demande.getStatut()) {
            case EN_ATTENTE_CHEF -> {
                Division division = demande.getDivisionId() != null
                        ? divisionRepo.findById(demande.getDivisionId()).orElse(null)
                        : null;
                if (division == null || !caller.getId().equals(division.getChefServiceId())) {
                    throw new RuntimeException("Vous n'êtes pas le chef de la division de ce demandeur");
                }
                demande.setValidationChef(valide);
                demande.setRejetChef(!valide);
                demande.setValidateurChef(caller.getLogin());
                demande.setValidateurChefNom(nomComplet(caller));
                demande.setMotifRejetChef(valide ? null : motif);
                demande.setDateTraitementChef(now);
                demande.setStatut(StatutAbsence.EN_ATTENTE_CSA);
            }
            case EN_ATTENTE_CSA -> {
                if (!hasAuthority("CSA")) {
                    throw new RuntimeException("Seul le CSA peut traiter cette demande à cette étape");
                }
                demande.setValidationCsa(valide);
                demande.setRejetCsa(!valide);
                demande.setValidateurCsa(caller.getLogin());
                demande.setValidateurCsaNom(nomComplet(caller));
                demande.setMotifRejetCsa(valide ? null : motif);
                demande.setDateTraitementCsa(now);
                demande.setStatut(StatutAbsence.EN_ATTENTE_DIRECTEUR);
            }
            case EN_ATTENTE_DIRECTEUR -> {
                if (!hasAuthority("DIRECTEUR")) {
                    throw new RuntimeException("Seul le Directeur peut valider cette demande à cette étape");
                }
                demande.setValidationDirecteur(valide);
                demande.setValidateurDirecteur(caller.getLogin());
                demande.setValidateurDirecteurNom(nomComplet(caller));
                demande.setDateValidationDirecteur(now);
                if (valide) {
                    demande.setStatut(StatutAbsence.VALIDEE);
                    if (demande.getType() == TypeAbsence.CONGE) {
                        decompterConges(demande);
                    } else {
                        cumulerAutorisation(demande);
                    }
                } else {
                    demande.setStatut(StatutAbsence.REJETEE);
                    demande.setMotifRejet(motif);
                    demande.setRejetePar(caller.getLogin());
                    demande.setRejeteParNom(nomComplet(caller));
                    demande.setDateRejet(now);
                }
            }
            default -> throw new RuntimeException("Cette demande n'est plus en attente de validation");
        }

        return demandeRepo.save(demande);
    }

    // Déduit les jours de la demande du solde de congés du demandeur, uniquement à la
    // validation finale (une demande rejetée en cours de route ne coûte aucun jour). Les
    // jours d'AUTORISATION cumulés depuis le dernier congé sont réglés en même temps
    // (soustraits du solde puis remis à zéro) — le congé "absorbe" les autorisations prises
    // entretemps.
    private void decompterConges(DemandeAbsence demande) {
        User demandeur = userRepository.findById(demande.getDemandeurId()).orElse(null);
        if (demandeur == null || demandeur.getPersonnel() == null || demandeur.getPersonnel().getSoldeConges() == null) {
            return;
        }
        Personnel personnel = demandeur.getPersonnel();
        int cumulAutorisation = personnel.getJoursAutorisationCumules() != null ? personnel.getJoursAutorisationCumules() : 0;
        personnel.setSoldeConges(Math.max(0, personnel.getSoldeConges() - demande.getNombreJours() - cumulAutorisation));
        personnel.setJoursAutorisationCumules(0);
        userRepository.save(demandeur);
    }

    // Une AUTORISATION validée ne décompte rien dans l'immédiat, mais s'accumule pour être
    // régularisée (déduite) au prochain congé validé.
    private void cumulerAutorisation(DemandeAbsence demande) {
        User demandeur = userRepository.findById(demande.getDemandeurId()).orElse(null);
        if (demandeur == null || demandeur.getPersonnel() == null) {
            return;
        }
        Personnel personnel = demandeur.getPersonnel();
        int cumulActuel = personnel.getJoursAutorisationCumules() != null ? personnel.getJoursAutorisationCumules() : 0;
        personnel.setJoursAutorisationCumules(cumulActuel + demande.getNombreJours());
        userRepository.save(demandeur);
    }

    private User currentUser() {
        String login = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByLogin(login).orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
    }

    private String nomComplet(User u) {
        if (u.getPersonnel() == null) return u.getLogin();
        String nom = (u.getPersonnel().getFirstname() + " " + u.getPersonnel().getLastname()).trim();
        return nom.isEmpty() ? u.getLogin() : nom;
    }

    private boolean hasAuthority(String authority) {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(authority));
    }
}
