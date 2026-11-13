package com.officedubac.project.axe;

import com.officedubac.project.personnel.Personnel;
import com.officedubac.project.personnel.PersonnelRepository;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CampagneService {

    private final CampagneRepository campagneRepo;
    private final AxeRepository axeRepo;
    private final PersonnelRepository personnelRepo;

    // ═══════════════════════════════════════════════════════════════
    // CAMPAGNE (en-tête) — les missions s'ajoutent séparément, voir plus bas
    // ═══════════════════════════════════════════════════════════════
    public List<Campagne> getAll() {
        return campagneRepo.findByActifTrueOrderByDateCreationDesc();
    }

    // Campagnes actives dont la période n'est pas encore terminée — proposées lors de l'ajout
    // d'une mission (on ne construit pas de mission sur une campagne déjà close).
    public List<Campagne> getEnCours() {
        LocalDate aujourdHui = LocalDate.now();
        return campagneRepo.findByActifTrueOrderByDateCreationDesc().stream()
                .filter(c -> !c.getDateFin().isBefore(aujourdHui))
                .toList();
    }

    public List<Campagne> getAllAdmin() {
        return campagneRepo.findByOrderByDateCreationDesc();
    }

    public Campagne getById(String id) {
        return campagneRepo.findById(id).orElseThrow(() -> new RuntimeException("Campagne introuvable : " + id));
    }

    public Campagne creer(CampagneRequest req) {
        validerDates(req.getDateDebut(), req.getDateFin());
        Campagne campagne = Campagne.builder()
                .nom(req.getNom())
                .dateDebut(req.getDateDebut())
                .dateFin(req.getDateFin())
                .actif(true)
                .missions(new ArrayList<>())
                .creePar(getUsername())
                .build();
        return campagneRepo.save(campagne);
    }

    public Campagne modifier(String id, CampagneRequest req) {
        Campagne campagne = getById(id);
        validerDates(req.getDateDebut(), req.getDateFin());
        campagne.setNom(req.getNom());
        campagne.setDateDebut(req.getDateDebut());
        campagne.setDateFin(req.getDateFin());
        return campagneRepo.save(campagne);
    }

    public void supprimer(String id) {
        Campagne campagne = getById(id);
        campagne.setActif(false);
        campagneRepo.save(campagne);
    }

    private void validerDates(LocalDate debut, LocalDate fin) {
        if (fin.isBefore(debut))
            throw new RuntimeException("La date de fin ne peut pas précéder la date de début");
    }

    // ═══════════════════════════════════════════════════════════════
    // MISSIONS — un lot peut être ajouté en une fois (plusieurs axes/missions pour la même
    // campagne dans le même envoi) ; modification/suppression restent unitaires
    // ═══════════════════════════════════════════════════════════════
    public Campagne ajouterMissions(String campagneId, List<MissionRequest> requetes) {
        if (requetes == null || requetes.isEmpty())
            throw new RuntimeException("Aucune mission à ajouter");

        Campagne campagne = getById(campagneId);
        List<Campagne.Mission> nouvelles = new ArrayList<>();
        for (MissionRequest req : requetes) {
            Campagne.Mission mission = resoudreMission(req);
            mission.setId(new ObjectId().toHexString());
            nouvelles.add(mission);
        }

        // Dans ce même lot : ni un axe, ni un agent, ne doit apparaître deux fois sur des
        // périodes qui se chevauchent, avant même de comparer au reste de la base.
        for (int i = 0; i < nouvelles.size(); i++) {
            for (int j = i + 1; j < nouvelles.size(); j++) {
                Campagne.Mission a = nouvelles.get(i);
                Campagne.Mission b = nouvelles.get(j);
                if (!seChevauchent(a.getDateDebut(), a.getDateFin(), b.getDateDebut(), b.getDateFin())) continue;

                if (a.getAxeId().equals(b.getAxeId()))
                    throw new RuntimeException("L'axe \"" + a.getAxeItineraire()
                            + "\" apparaît deux fois avec des périodes qui se chevauchent dans cet ajout");

                String agentCommun = premierAgentCommun(a, b);
                if (agentCommun != null)
                    throw new RuntimeException("\"" + agentCommun
                            + "\" apparaît sur deux missions avec des périodes qui se chevauchent dans cet ajout");
            }
        }
        for (Campagne.Mission mission : nouvelles) {
            verifierChevauchement(mission, campagneId, null);
        }

        List<Campagne.Mission> missions = new ArrayList<>(campagne.getMissions());
        missions.addAll(nouvelles);
        campagne.setMissions(missions);
        return campagneRepo.save(campagne);
    }

    public Campagne modifierMission(String campagneId, String missionId, MissionRequest req) {
        Campagne campagne = getById(campagneId);
        boolean existe = campagne.getMissions().stream().anyMatch(m -> missionId.equals(m.getId()));
        if (!existe) throw new RuntimeException("Mission introuvable dans cette campagne : " + missionId);

        Campagne.Mission modifiee = resoudreMission(req);
        modifiee.setId(missionId);
        verifierChevauchement(modifiee, campagneId, missionId);

        List<Campagne.Mission> missions = campagne.getMissions().stream()
                .map(m -> missionId.equals(m.getId()) ? modifiee : m)
                .toList();
        campagne.setMissions(missions);
        return campagneRepo.save(campagne);
    }

    public Campagne supprimerMission(String campagneId, String missionId) {
        Campagne campagne = getById(campagneId);
        List<Campagne.Mission> missions = campagne.getMissions().stream()
                .filter(m -> !missionId.equals(m.getId()))
                .toList();
        campagne.setMissions(missions);
        return campagneRepo.save(campagne);
    }

    private Campagne.Mission resoudreMission(MissionRequest req) {
        if (req.getAxeId() == null || req.getAxeId().isBlank())
            throw new RuntimeException("Axe requis pour chaque mission");
        if (req.getDateDebut() == null || req.getDateFin() == null)
            throw new RuntimeException("Dates de début et de fin requises pour chaque mission");
        if (req.getDateFin().isBefore(req.getDateDebut()))
            throw new RuntimeException("La date de fin ne peut pas précéder la date de début de la mission");
        if (req.getNombreTickets() == null || req.getNombreTickets() < 1)
            throw new RuntimeException("Nombre de tickets requis pour chaque mission");

        Axe axe = axeRepo.findById(req.getAxeId())
                .orElseThrow(() -> new RuntimeException("Axe introuvable : " + req.getAxeId()));

        List<String> agentIds = req.getAgentIds() != null ? req.getAgentIds() : List.of();
        List<String> agentNoms = agentIds.stream()
                .map(id -> personnelRepo.findById(id).map(this::nomComplet).orElse(id))
                .toList();

        return Campagne.Mission.builder()
                .axeId(axe.getId())
                .axeNumero(axe.getNumero())
                .axeItineraire(axe.getItineraire())
                .dateDebut(req.getDateDebut())
                .dateFin(req.getDateFin())
                .agentIds(agentIds)
                .agentNoms(agentNoms)
                .nombreTickets(req.getNombreTickets())
                .build();
    }

    // Un axe ne peut être affecté qu'à une seule mission à la fois sur une période donnée, et un
    // agent ne peut pas non plus se retrouver sur deux missions dont les périodes se chevauchent
    // — tous azimuts (n'importe quelle campagne) — excluant la mission elle-même lors d'une
    // modification.
    private void verifierChevauchement(Campagne.Mission nouvelle, String campagneId, String excludeMissionId) {
        for (Campagne autre : campagneRepo.findByActifTrueOrderByDateCreationDesc()) {
            for (Campagne.Mission m : autre.getMissions()) {
                boolean estLaMissionEnCoursDeModif = autre.getId().equals(campagneId)
                        && excludeMissionId != null && excludeMissionId.equals(m.getId());
                if (estLaMissionEnCoursDeModif) continue;
                if (!seChevauchent(nouvelle.getDateDebut(), nouvelle.getDateFin(), m.getDateDebut(), m.getDateFin()))
                    continue;

                if (m.getAxeId().equals(nouvelle.getAxeId()))
                    throw new RuntimeException("L'axe \"" + nouvelle.getAxeItineraire()
                            + "\" est déjà affecté à une mission de la campagne \"" + autre.getNom()
                            + "\" sur une période qui chevauche ces dates");

                String agentCommun = premierAgentCommun(nouvelle, m);
                if (agentCommun != null)
                    throw new RuntimeException("\"" + agentCommun
                            + "\" est déjà affecté à une mission de la campagne \"" + autre.getNom()
                            + "\" sur une période qui chevauche ces dates");
            }
        }
    }

    // Premier agent présent dans les deux missions, ou null — utilisé pour détecter un
    // chevauchement d'agent et nommer l'agent fautif dans le message d'erreur.
    private String premierAgentCommun(Campagne.Mission a, Campagne.Mission b) {
        List<String> idsA = a.getAgentIds();
        List<String> idsB = b.getAgentIds();
        if (idsA == null || idsB == null) return null;
        for (int i = 0; i < idsA.size(); i++) {
            if (idsB.contains(idsA.get(i))) {
                List<String> noms = a.getAgentNoms();
                return (noms != null && noms.size() > i) ? noms.get(i) : idsA.get(i);
            }
        }
        return null;
    }

    private boolean seChevauchent(LocalDate debut1, LocalDate fin1, LocalDate debut2, LocalDate fin2) {
        return !(fin1.isBefore(debut2) || debut1.isAfter(fin2));
    }

    private String nomComplet(Personnel p) {
        String nom = ((p.getFirstname() != null ? p.getFirstname() : "") + " "
                + (p.getLastname() != null ? p.getLastname() : "")).trim();
        return nom.isEmpty() ? p.getId() : nom;
    }

    private String getUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
