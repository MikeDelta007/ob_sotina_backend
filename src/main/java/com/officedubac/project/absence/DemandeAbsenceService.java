package com.officedubac.project.absence;

import com.officedubac.project.models.Role;
import com.officedubac.project.models.User;
import com.officedubac.project.personnel.Division;
import com.officedubac.project.personnel.DivisionRepository;
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

        if (req.getDateFin().isBefore(req.getDateDebut())) {
            throw new RuntimeException("La date de fin ne peut pas précéder la date de début");
        }
        int nombreJours = (int) (ChronoUnit.DAYS.between(req.getDateDebut(), req.getDateFin()) + 1);

        // Si un solde de congés est configuré (typePersonnel défini), la demande ne peut pas
        // dépasser le solde restant. Si le solde n'est pas encore configuré, on ne bloque pas.
        if (demandeur.getSoldeConges() != null && nombreJours > demandeur.getSoldeConges()) {
            throw new RuntimeException("La période demandée (" + nombreJours + " jour(s)) dépasse votre solde de congés restant ("
                    + demandeur.getSoldeConges() + " jour(s))");
        }

        DemandeAbsence demande = DemandeAbsence.builder()
                .demandeurId(demandeur.getId())
                .demandeurNom(demandeur.getFirstname() + " " + demandeur.getLastname())
                .divisionId(demandeur.getDivision() != null ? demandeur.getDivision().getId() : null)
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
        Division division = demandeur.getDivision() != null
                ? divisionRepo.findById(demandeur.getDivision().getId()).orElse(null)
                : null;
        if (division == null || division.getChefServiceId() == null || division.getChefServiceId().isBlank()) {
            return StatutAbsence.EN_ATTENTE_CSA;
        }
        if (division.getChefServiceId().equals(demandeur.getId())) {
            return StatutAbsence.EN_ATTENTE_CSA;
        }
        return StatutAbsence.EN_ATTENTE_CHEF;
    }

    public List<DemandeAbsence> mesDemandes() {
        User user = currentUser();
        return demandeRepo.findByDemandeurIdOrderByDateCreationDesc(user.getId());
    }

    public List<DemandeAbsence> aValider() {
        User user = currentUser();
        Role role = user.getProfil() != null ? user.getProfil().getName() : null;

        if (role == Role.CSA) {
            return demandeRepo.findByStatutOrderByDateCreationDesc(StatutAbsence.EN_ATTENTE_CSA);
        }
        if (role == Role.DIRECTEUR) {
            return demandeRepo.findByStatutOrderByDateCreationDesc(StatutAbsence.EN_ATTENTE_DIRECTEUR);
        }
        if (role == Role.ADMIN) {
            return demandeRepo.findAll().stream()
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
        return demandeRepo.findByStatutAndDivisionIdInOrderByDateCreationDesc(StatutAbsence.EN_ATTENTE_CHEF, divisionsDontJeSuisChef);
    }

    public DemandeAbsence valider(String id) {
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
                demande.setValidationChef(true);
                demande.setValidateurChef(caller.getLogin());
                demande.setDateValidationChef(now);
                demande.setStatut(StatutAbsence.EN_ATTENTE_CSA);
            }
            case EN_ATTENTE_CSA -> {
                if (!hasAuthority("CSA")) {
                    throw new RuntimeException("Seul le CSA peut valider cette demande à cette étape");
                }
                demande.setValidationCsa(true);
                demande.setValidateurCsa(caller.getLogin());
                demande.setDateValidationCsa(now);
                demande.setStatut(StatutAbsence.EN_ATTENTE_DIRECTEUR);
            }
            case EN_ATTENTE_DIRECTEUR -> {
                if (!hasAuthority("DIRECTEUR")) {
                    throw new RuntimeException("Seul le Directeur peut valider cette demande à cette étape");
                }
                demande.setValidationDirecteur(true);
                demande.setValidateurDirecteur(caller.getLogin());
                demande.setDateValidationDirecteur(now);
                demande.setStatut(StatutAbsence.VALIDEE);
                decompterConges(demande);
            }
            default -> throw new RuntimeException("Cette demande n'est plus en attente de validation");
        }

        return demandeRepo.save(demande);
    }

    // Déduit les jours de la demande du solde de congés du demandeur, uniquement à la
    // validation finale (une demande rejetée en cours de route ne coûte aucun jour).
    private void decompterConges(DemandeAbsence demande) {
        User demandeur = userRepository.findById(demande.getDemandeurId()).orElse(null);
        if (demandeur == null || demandeur.getSoldeConges() == null) {
            return;
        }
        demandeur.setSoldeConges(Math.max(0, demandeur.getSoldeConges() - demande.getNombreJours()));
        userRepository.save(demandeur);
    }

    public DemandeAbsence rejeter(String id, String motif) {
        DemandeAbsence demande = demandeRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Demande d'absence introuvable"));
        if (demande.getStatut() == StatutAbsence.VALIDEE || demande.getStatut() == StatutAbsence.REJETEE) {
            throw new RuntimeException("Cette demande ne peut plus être rejetée");
        }
        demande.setStatut(StatutAbsence.REJETEE);
        demande.setMotifRejet(motif);
        demande.setRejetePar(currentUser().getLogin());
        demande.setDateRejet(LocalDateTime.now());
        return demandeRepo.save(demande);
    }

    private User currentUser() {
        String login = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByLogin(login).orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
    }

    private boolean hasAuthority(String authority) {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(authority));
    }
}
