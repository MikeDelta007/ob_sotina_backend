package com.officedubac.project.absence;

import com.officedubac.project.models.Role;
import com.officedubac.project.models.User;
import com.officedubac.project.personnel.Division;
import com.officedubac.project.personnel.DivisionRepository;
import com.officedubac.project.personnel.Personnel;
import com.officedubac.project.personnel.PersonnelCompteService;
import com.officedubac.project.personnel.PersonnelRepository;
import com.officedubac.project.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DemandeAbsenceService {

    private final DemandeAbsenceRepository demandeRepo;
    private final DivisionRepository divisionRepo;
    private final UserRepository userRepository;
    private final PersonnelRepository personnelRepository;
    private final PersonnelCompteService personnelCompteService;

    public DemandeAbsence creer(DemandeAbsenceRequest req) {
        User createur = currentUser();
        Personnel demandeur = resoudreDemandeur(createur, req.getBeneficiaireId());

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
            Integer soldeDisponible = demandeur.getSoldeDisponible();
            if (soldeDisponible != null && nombreJours > soldeDisponible) {
                throw new RuntimeException("La période demandée (" + nombreJours + " jour(s)) dépasse votre solde de congés disponible ("
                        + soldeDisponible + " jour(s))");
            }
        }

        DemandeAbsence demande = DemandeAbsence.builder()
                .demandeurId(demandeur.getId())
                .demandeurNom(demandeur.getFirstname() + " " + demandeur.getLastname())
                .divisionId(demandeur.getDivision() != null ? demandeur.getDivision().getId() : null)
                .type(req.getType())
                .dateDebut(req.getDateDebut())
                .dateFin(req.getDateFin())
                .nombreJours(nombreJours)
                .motif(req.getMotif())
                .statut(statutDepart(demandeur))
                .creePar(createur.getLogin())
                .dateCreation(LocalDateTime.now())
                .build();

        return demandeRepo.save(demande);
    }

    // Modification/suppression réservées au créateur, et seulement tant qu'aucun validateur
    // (chef, CSA ou Directeur) ne s'est encore prononcé — ni validé, ni rejeté — sur la demande.
    public DemandeAbsence modifier(String id, DemandeAbsenceRequest req) {
        DemandeAbsence demande = demandeRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Demande d'absence introuvable"));
        User createur = currentUser();

        if (!demande.getCreePar().equals(createur.getLogin()))
            throw new RuntimeException("Vous ne pouvez modifier que vos propres demandes");
        if (!pasEncoreTraitee(demande))
            throw new RuntimeException("Cette demande a déjà été traitée par un validateur, elle ne peut plus être modifiée");

        Personnel demandeur = resoudreDemandeur(createur, req.getBeneficiaireId());

        if (req.getDateDebut().isBefore(java.time.LocalDate.now())) {
            throw new RuntimeException("La date de début ne peut pas être antérieure à aujourd'hui");
        }
        if (req.getDateFin().isBefore(req.getDateDebut())) {
            throw new RuntimeException("La date de fin ne peut pas précéder la date de début");
        }
        int nombreJours = (int) (ChronoUnit.DAYS.between(req.getDateDebut(), req.getDateFin()) + 1);

        if (req.getType() == TypeAbsence.AUTORISATION && (req.getMotif() == null || req.getMotif().isBlank())) {
            throw new RuntimeException("Le motif est obligatoire pour une autorisation d'absence");
        }

        if (req.getType() == TypeAbsence.CONGE) {
            Integer soldeDisponible = demandeur.getSoldeDisponible();
            if (soldeDisponible != null && nombreJours > soldeDisponible) {
                throw new RuntimeException("La période demandée (" + nombreJours + " jour(s)) dépasse votre solde de congés disponible ("
                        + soldeDisponible + " jour(s))");
            }
        }

        demande.setDemandeurId(demandeur.getId());
        demande.setDemandeurNom(demandeur.getFirstname() + " " + demandeur.getLastname());
        demande.setDivisionId(demandeur.getDivision() != null ? demandeur.getDivision().getId() : null);
        demande.setType(req.getType());
        demande.setDateDebut(req.getDateDebut());
        demande.setDateFin(req.getDateFin());
        demande.setNombreJours(nombreJours);
        demande.setMotif(req.getMotif());
        demande.setStatut(statutDepart(demandeur));

        return demandeRepo.save(demande);
    }

    public void supprimer(String id) {
        DemandeAbsence demande = demandeRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Demande d'absence introuvable"));
        User createur = currentUser();

        if (!demande.getCreePar().equals(createur.getLogin()))
            throw new RuntimeException("Vous ne pouvez supprimer que vos propres demandes");
        if (!pasEncoreTraitee(demande))
            throw new RuntimeException("Cette demande a déjà été traitée par un validateur, elle ne peut plus être supprimée");

        demandeRepo.delete(demande);
    }

    private boolean pasEncoreTraitee(DemandeAbsence d) {
        return !d.isValidationChef() && !d.isRejetChef() && !d.isValidationCsa() && !d.isRejetCsa() && !d.isValidationDirecteur();
    }

    // La fiche Personnel du compte connecté — jamais la copie embarquée (User.personnel),
    // potentiellement périmée, mais la fiche elle-même quand elle existe.
    private Personnel monPersonnel(User moi) {
        if (moi.getPersonnelId() != null) {
            return personnelRepository.findById(moi.getPersonnelId()).orElse(moi.getPersonnel());
        }
        return moi.getPersonnel();
    }

    // Comme pour les expressions de besoin : le chef de service peut agir pour un agent de sa
    // propre division, le CSA/Directeur/Assistante Directeur pour n'importe quel agent — sinon
    // uniquement pour soi-même. Un bénéficiaire est toujours désigné par sa fiche Personnel,
    // avec ou sans compte applicatif.
    private Personnel resoudreDemandeur(User createur, String beneficiaireId) {
        Personnel moi = monPersonnel(createur);
        if (beneficiaireId == null || beneficiaireId.isBlank() || beneficiaireId.equals(moi.getId())) {
            return moi;
        }
        Personnel beneficiaire = personnelRepository.findById(beneficiaireId)
                .orElseThrow(() -> new RuntimeException("Agent introuvable"));

        boolean superviseurGlobal = hasAuthority("CSA") || hasAuthority("DIRECTEUR") || hasAuthority("ASSISTANTE_DIRECTEUR");
        if (!superviseurGlobal) {
            Division divisionBeneficiaire = beneficiaire.getDivision() != null
                    ? divisionRepo.findById(beneficiaire.getDivision().getId()).orElse(null)
                    : null;
            if (divisionBeneficiaire == null || !createur.getId().equals(divisionBeneficiaire.getChefServiceId())) {
                throw new RuntimeException("Vous ne pouvez créer une demande que pour un agent de votre division dont vous êtes le chef");
            }
        }
        return beneficiaire;
    }

    // Le rôle (CSA/Directeur/...) est un attribut du compte, pas de la fiche Personnel — un
    // bénéficiaire sans compte n'a donc jamais de rôle et suit toujours le circuit standard.
    private StatutAbsence statutDepart(Personnel demandeur) {
        Optional<User> compte = userRepository.findByPersonnelId(demandeur.getId());
        // Seuls CSA/Directeur démarrent directement au CSA. Un agent ADMIN (ex. service
        // informatique) suit son chef de division comme tout autre agent — s'il n'a pas de chef,
        // le contrôle ci-dessous l'envoie de toute façon au CSA.
        Role role = compte.map(User::getProfil).map(p -> p != null ? p.getName() : null).orElse(null);
        if (role == Role.CSA || role == Role.DIRECTEUR || role == Role.ASSISTANTE_DIRECTEUR) {
            return StatutAbsence.EN_ATTENTE_CSA;
        }
        Division division = demandeur.getDivision() != null
                ? divisionRepo.findById(demandeur.getDivision().getId()).orElse(null)
                : null;
        if (division == null || division.getChefServiceId() == null || division.getChefServiceId().isBlank()) {
            return StatutAbsence.EN_ATTENTE_CSA;
        }
        if (compte.isPresent() && division.getChefServiceId().equals(compte.get().getId())) {
            return StatutAbsence.EN_ATTENTE_CSA;
        }
        return StatutAbsence.EN_ATTENTE_CHEF;
    }

    // Vrai si l'utilisateur est chef d'au moins une division active (indépendamment de son rôle).
    private boolean estChefDeDivision(User user) {
        return divisionRepo.findByActifTrue().stream().anyMatch(d -> user.getId().equals(d.getChefServiceId()));
    }

    // "Mes demandes" = uniquement celles où je suis bénéficiaire — ne doit pas se mélanger avec
    // celles que je crée pour un agent (celles-ci se gèrent depuis "Demandes de mes agents").
    public List<DemandeAbsence> mesDemandes(TypeAbsence type) {
        Personnel moi = monPersonnel(currentUser());
        return demandeRepo.findByDemandeurIdAndTypeOrderByDateCreationDesc(moi.getId(), type);
    }

    public List<DemandeAbsence> aValider(TypeAbsence type) {
        User user = currentUser();
        Role role = user.getProfil() != null ? user.getProfil().getName() : null;

        if (role == Role.CSA) {
            return demandeRepo.findByStatutAndTypeOrderByDateCreationDesc(StatutAbsence.EN_ATTENTE_CSA, type);
        }
        if (role == Role.DIRECTEUR || role == Role.ASSISTANTE_DIRECTEUR) {
            return demandeRepo.findByStatutAndTypeOrderByDateCreationDesc(StatutAbsence.EN_ATTENTE_DIRECTEUR, type);
        }
        if (role == Role.ADMIN && !estChefDeDivision(user)) {
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
        if (role == Role.DIRECTEUR || role == Role.ASSISTANTE_DIRECTEUR || (role == Role.ADMIN && !estChefDeDivision(user))) {
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

    // Vue globale, accessible à tout agent (pas seulement chef/CSA/Directeur) : qui est
    // actuellement en congé validé, ou le sera bientôt, et son suppléant éventuel — pour pouvoir
    // renseigner un usager sans avoir à interroger le chef de service.
    public List<DemandeAbsence> congesEnCours() {
        LocalDate aujourdHui = LocalDate.now();
        return demandeRepo.findByTypeAndStatutOrderByDateDebutAsc(TypeAbsence.CONGE, StatutAbsence.VALIDEE).stream()
                .filter(d -> !d.getDateFin().isBefore(aujourdHui))
                .collect(Collectors.toList());
    }

    // Chaque chef de service désigne le suppléant de ses propres agents (parmi les agents de sa
    // division uniquement) ; le CSA et le Directeur (comme l'Assistante Directeur et l'Admin),
    // supervisant tout le personnel, le font pour n'importe quel agent. Visible ensuite par tous
    // via congesEnCours(). suppleantId vide retire le suppléant déjà désigné.
    public DemandeAbsence definirSuppleant(String id, String suppleantId) {
        DemandeAbsence demande = demandeRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Demande d'absence introuvable"));
        if (demande.getType() != TypeAbsence.CONGE)
            throw new RuntimeException("Un suppléant ne peut être désigné que pour un congé");
        if (demande.getStatut() != StatutAbsence.VALIDEE)
            throw new RuntimeException("Un suppléant ne peut être désigné que pour un congé déjà validé");

        User caller = currentUser();
        Division divisionAgent = demande.getDivisionId() != null
                ? divisionRepo.findById(demande.getDivisionId()).orElse(null)
                : null;
        boolean superviseurGlobal = hasAuthority("CSA") || hasAuthority("DIRECTEUR")
                || hasAuthority("ASSISTANTE_DIRECTEUR") || hasAuthority("ADMIN");
        boolean chefDeCetteDivision = divisionAgent != null && caller.getId().equals(divisionAgent.getChefServiceId());
        if (!superviseurGlobal && !chefDeCetteDivision)
            throw new RuntimeException("Vous n'êtes pas autorisé à désigner un suppléant pour cet agent");

        if (suppleantId == null || suppleantId.isBlank()) {
            demande.setSuppleantId(null);
            demande.setSuppleantNom(null);
            demande.setSuppleantDefiniPar(null);
            demande.setSuppleantDefiniParNom(null);
            demande.setDateDefinitionSuppleant(null);
            return demandeRepo.save(demande);
        }

        Personnel suppleant = personnelRepository.findById(suppleantId)
                .orElseThrow(() -> new RuntimeException("Agent introuvable"));
        if (suppleant.getId().equals(demande.getDemandeurId()))
            throw new RuntimeException("Le suppléant ne peut pas être l'agent lui-même");

        // Un chef de service ne désigne un suppléant que parmi les agents de sa propre division —
        // le CSA/Directeur, supervisant tout le personnel, n'ont pas cette contrainte.
        if (!superviseurGlobal) {
            String divisionSuppleantId = suppleant.getDivision() != null ? suppleant.getDivision().getId() : null;
            if (divisionSuppleantId == null || divisionAgent == null || !divisionSuppleantId.equals(divisionAgent.getId()))
                throw new RuntimeException("Le suppléant doit être un agent de la même division");
        }

        // Un agent lui-même en congé validé sur une période qui chevauche celle de l'absent ne
        // peut pas le suppléer — il ne serait pas davantage disponible que lui.
        boolean suppleantEnCongeSurPeriode = demandeRepo.findByTypeAndStatutOrderByDateDebutAsc(TypeAbsence.CONGE, StatutAbsence.VALIDEE)
                .stream()
                .filter(d -> d.getDemandeurId().equals(suppleant.getId()))
                .anyMatch(d -> !(d.getDateFin().isBefore(demande.getDateDebut()) || d.getDateDebut().isAfter(demande.getDateFin())));
        if (suppleantEnCongeSurPeriode)
            throw new RuntimeException("Cet agent est lui-même en congé pendant cette période, il ne peut pas être désigné suppléant");

        demande.setSuppleantId(suppleant.getId());
        demande.setSuppleantNom((suppleant.getFirstname() + " " + suppleant.getLastname()).trim());
        demande.setSuppleantDefiniPar(caller.getLogin());
        demande.setSuppleantDefiniParNom(nomComplet(caller));
        demande.setDateDefinitionSuppleant(LocalDateTime.now());
        return demandeRepo.save(demande);
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
                if (!hasAuthority("DIRECTEUR") && !hasAuthority("ASSISTANTE_DIRECTEUR")) {
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
        Personnel personnel = personnelRepository.findById(demande.getDemandeurId()).orElse(null);
        if (personnel == null || personnel.getSoldeConges() == null) {
            return;
        }
        int cumulAutorisation = personnel.getJoursAutorisationCumules() != null ? personnel.getJoursAutorisationCumules() : 0;
        personnel.setSoldeConges(Math.max(0, personnel.getSoldeConges() - demande.getNombreJours() - cumulAutorisation));
        personnel.setJoursAutorisationCumules(0);
        personnelRepository.save(personnel);
        personnelCompteService.synchroniserCompteLie(personnel);
    }

    // Une AUTORISATION validée ne décompte rien dans l'immédiat, mais s'accumule pour être
    // régularisée (déduite) au prochain congé validé.
    private void cumulerAutorisation(DemandeAbsence demande) {
        Personnel personnel = personnelRepository.findById(demande.getDemandeurId()).orElse(null);
        if (personnel == null) {
            return;
        }
        int cumulActuel = personnel.getJoursAutorisationCumules() != null ? personnel.getJoursAutorisationCumules() : 0;
        personnel.setJoursAutorisationCumules(cumulActuel + demande.getNombreJours());
        personnelRepository.save(personnel);
        personnelCompteService.synchroniserCompteLie(personnel);
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
