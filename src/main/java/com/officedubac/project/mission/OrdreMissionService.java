package com.officedubac.project.mission;

import com.officedubac.project.models.Civilite;
import com.officedubac.project.models.User;
import com.officedubac.project.models.Ville;
import com.officedubac.project.personnel.Fonction;
import com.officedubac.project.personnel.Personnel;
import com.officedubac.project.personnel.PersonnelRepository;
import com.officedubac.project.personnel.Voiture;
import com.officedubac.project.personnel.VoitureRepository;
import com.officedubac.project.repository.UserRepository;
import com.officedubac.project.repository.VilleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrdreMissionService {

    private static final DateTimeFormatter DATE_FR = DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.FRENCH);

    private final OrdreMissionRepository ordreMissionRepo;
    private final UserRepository userRepository;
    private final PersonnelRepository personnelRepo;
    private final VoitureRepository voitureRepo;
    private final VilleRepository villeRepo;
    private final MotifMissionRepository motifMissionRepo;

    public OrdreMission creer(OrdreMissionRequest req) {
        User csa = currentUser();
        List<Ville> villes = resoudreVilles(req.getVilleIds());
        MotifMission motif = resoudreMotif(req.getMotifId());
        verifierAbsenceDeChevauchement(req, null);
        List<LigneMission> lignes = resoudreLignes(req.getLignes());

        OrdreMission ordre = OrdreMission.builder()
                .villeIds(req.getVilleIds())
                .villeNoms(villes.stream().map(Ville::getName).collect(Collectors.toList()))
                .motifId(motif.getId())
                .motifLibelle(motif.getLibelle())
                .dateDebut(req.getDateDebut())
                .dateFin(req.getDateFin())
                .lignes(lignes)
                .creeParId(csa.getId())
                .creeParNom(csa.getPersonnel().getFirstname() + " " + csa.getPersonnel().getLastname())
                .dateCreation(java.time.LocalDateTime.now())
                .annule(false)
                .build();

        return ordreMissionRepo.save(ordre);
    }

    public OrdreMission modifier(String id, OrdreMissionRequest req) {
        OrdreMission ordre = ordreMissionRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Ordre de mission introuvable"));
        if (ordre.isAnnule()) {
            throw new RuntimeException("Cet ordre de mission a été annulé");
        }
        List<Ville> villes = resoudreVilles(req.getVilleIds());
        MotifMission motif = resoudreMotif(req.getMotifId());
        verifierAbsenceDeChevauchement(req, id);
        List<LigneMission> lignes = resoudreLignes(req.getLignes());

        ordre.setVilleIds(req.getVilleIds());
        ordre.setVilleNoms(villes.stream().map(Ville::getName).collect(Collectors.toList()));
        ordre.setMotifId(motif.getId());
        ordre.setMotifLibelle(motif.getLibelle());
        ordre.setDateDebut(req.getDateDebut());
        ordre.setDateFin(req.getDateFin());
        ordre.setLignes(lignes);

        return ordreMissionRepo.save(ordre);
    }

    private List<Ville> resoudreVilles(List<String> villeIds) {
        return villeIds.stream()
                .map(id -> villeRepo.findById(id).orElseThrow(() -> new RuntimeException("Ville introuvable : " + id)))
                .collect(Collectors.toList());
    }

    private MotifMission resoudreMotif(String motifId) {
        return motifMissionRepo.findById(motifId)
                .orElseThrow(() -> new RuntimeException("Motif introuvable"));
    }

    // Un agent ne peut pas être sur deux missions actives dont les périodes se chevauchent.
    // excludeOrdreId : l'ordre en cours de modification, à ignorer dans la comparaison.
    private void verifierAbsenceDeChevauchement(OrdreMissionRequest req, String excludeOrdreId) {
        for (LigneMissionRequest ligneReq : req.getLignes()) {
            AgentResolu agent = resoudreAgent(ligneReq.getAgentId());
            List<OrdreMission> missionsActives = ordreMissionRepo.findByLignes_AgentIdAndAnnuleFalse(agent.id());
            for (OrdreMission autre : missionsActives) {
                if (excludeOrdreId != null && excludeOrdreId.equals(autre.getId())) continue;
                boolean chevauche = !autre.getDateDebut().isAfter(req.getDateFin())
                        && !autre.getDateFin().isBefore(req.getDateDebut());
                if (chevauche) {
                    throw new RuntimeException(agent.nom() + " figure déjà sur une mission du "
                            + autre.getDateDebut().format(DATE_FR) + " au " + autre.getDateFin().format(DATE_FR));
                }
            }
        }
    }

    private List<LigneMission> resoudreLignes(List<LigneMissionRequest> lignesReq) {
        return lignesReq.stream().map(this::resoudreLigne).collect(Collectors.toList());
    }

    private LigneMission resoudreLigne(LigneMissionRequest req) {
        AgentResolu agent = resoudreAgent(req.getAgentId());

        MoyenTransport moyen = req.getMoyenTransport();
        Voiture voiture = switch (moyen) {
            case VEHICULE_PERSONNEL -> resoudreVoitureDeLAgent(agent);
            case VEHICULE_PARC -> resoudreVoitureChoisie(req.getVoitureId());
            case TRANSPORT_COMMUN -> null;
        };

        return LigneMission.builder()
                .agentId(agent.id())
                .agentNom(agent.nom())
                .agentQualite(agent.qualite())
                .moyenTransport(moyen)
                .voitureId(voiture != null ? voiture.getId() : null)
                .voitureImmatriculation(voiture != null ? voiture.getImmatriculation() : null)
                .build();
    }

    // Un agent de mission est toujours une fiche Personnel (cf. PersonnelRepository) — qu'elle
    // soit ou non rattachée à un compte applicatif. Les comptes User ne sont jamais utilisés ici :
    // les agents à choisir, comme les véhicules, viennent du personnel, pas des comptes.
    private record AgentResolu(String id, String nom, String qualite) {}

    private AgentResolu resoudreAgent(String agentId) {
        Personnel personnel = personnelRepo.findById(agentId)
                .orElseThrow(() -> new RuntimeException("Agent introuvable : " + agentId));
        return new AgentResolu(personnel.getId(),
                nomComplet(personnel.getCivilite(), personnel.getFirstname(), personnel.getLastname()),
                qualite(personnel.getFonction()));
    }

    private String nomComplet(Civilite civilite, String firstname, String lastname) {
        String prefixe = switch (civilite == null ? Civilite.Mr : civilite) {
            case Mr -> "Monsieur";
            case Mme -> "Madame";
            case Mlle -> "Mademoiselle";
        };
        return prefixe + " " + firstname + " " + lastname;
    }

    // "Qualité et grade" du modèle papier : le chauffeur est affiché seul en majuscules, les
    // autres agents comme "Agent Office du Baccalauréat – <fonction>".
    private String qualite(Fonction fonction) {
        if (fonction == null || fonction.getLibelle() == null || fonction.getLibelle().isBlank()) {
            return "Agent Office du Baccalauréat";
        }
        if ("chauffeur".equalsIgnoreCase(fonction.getLibelle().trim())) {
            return "CHAUFFEUR";
        }
        return "Agent Office du Baccalauréat – " + fonction.getLibelle();
    }

    // Un véhicule "personnel" peut être rattaché à la fiche Personnel de l'agent via l'un ou
    // l'autre champ selon le type choisi à la création du véhicule (AGENT ou EXTERNE) — les deux
    // référencent désormais un Personnel, donc on vérifie les deux.
    private Voiture resoudreVoitureDeLAgent(AgentResolu agent) {
        Optional<Voiture> voiture = voitureRepo.findByProprietaireAgentIdAndActifTrue(agent.id())
                .or(() -> voitureRepo.findByProprietairePersonnelIdAndActifTrue(agent.id()));
        return voiture.orElseThrow(() -> new RuntimeException("Aucun véhicule enregistré pour cet agent"));
    }

    private Voiture resoudreVoitureChoisie(String voitureId) {
        if (voitureId == null || voitureId.isBlank()) {
            throw new RuntimeException("Sélectionnez un véhicule pour cette ligne");
        }
        return voitureRepo.findById(voitureId)
                .orElseThrow(() -> new RuntimeException("Voiture introuvable"));
    }

    public OrdreMission annuler(String id) {
        OrdreMission ordre = ordreMissionRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Ordre de mission introuvable"));
        ordre.setAnnule(true);
        ordre.setAnnuleParId(currentUser().getId());
        ordre.setDateAnnulation(java.time.LocalDateTime.now());
        return ordreMissionRepo.save(ordre);
    }

    public List<OrdreMission> mesMissions() {
        User user = currentUser();
        // Les lignes de mission sont indexées par id de fiche Personnel, pas par id de compte User.
        String personnelId = user.getPersonnelId() != null ? user.getPersonnelId() : user.getId();
        return ordreMissionRepo.findByLignes_AgentIdOrderByDateCreationDesc(personnelId);
    }

    public List<OrdreMission> toutes() {
        return ordreMissionRepo.findAllByOrderByDateCreationDesc();
    }

    public OrdreMission getById(String id) {
        return ordreMissionRepo.findById(id).orElseThrow(() -> new RuntimeException("Ordre de mission introuvable"));
    }

    // Tous les ordres actifs (non annulés) dont la date de début tombe dans l'intervalle donné —
    // utilisé pour l'export PDF groupé par intervalle de dates (motifs/villes potentiellement
    // différents, contrairement à l'export groupé par sélection).
    public List<OrdreMission> parIntervalleDeDates(java.time.LocalDate debut, java.time.LocalDate fin) {
        return ordreMissionRepo.findByDateDebutBetweenAndAnnuleFalseOrderByDateDebutAsc(debut, fin);
    }

    // ── Motifs (liste déroulante à la création) — liste propre aux ordres de mission ──

    public List<MotifMission> getMotifs() {
        return motifMissionRepo.findByActifTrueOrderByLibelleAsc();
    }

    public List<MotifMission> getAllMotifs() {
        return motifMissionRepo.findByOrderByLibelleAsc();
    }

    public MotifMission creerMotif(String libelle) {
        return motifMissionRepo.save(MotifMission.builder().libelle(libelle).actif(true).build());
    }

    public MotifMission modifierMotif(String id, String libelle, boolean actif) {
        MotifMission motif = motifMissionRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Motif introuvable"));
        motif.setLibelle(libelle);
        motif.setActif(actif);
        return motifMissionRepo.save(motif);
    }

    public void supprimerMotif(String id) {
        motifMissionRepo.findById(id).ifPresent(m -> { m.setActif(false); motifMissionRepo.save(m); });
    }

    private User currentUser() {
        String login = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByLogin(login).orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
    }
}
