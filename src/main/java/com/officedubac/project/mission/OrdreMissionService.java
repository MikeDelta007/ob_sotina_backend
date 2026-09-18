package com.officedubac.project.mission;

import com.officedubac.project.models.Region;
import com.officedubac.project.models.User;
import com.officedubac.project.personnel.Personnel;
import com.officedubac.project.personnel.PersonnelRepository;
import com.officedubac.project.personnel.Voiture;
import com.officedubac.project.personnel.VoitureRepository;
import com.officedubac.project.repository.RegionRepository;
import com.officedubac.project.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrdreMissionService {

    private final OrdreMissionRepository ordreMissionRepo;
    private final UserRepository userRepository;
    private final PersonnelRepository personnelRepo;
    private final VoitureRepository voitureRepo;
    private final RegionRepository regionRepo;

    public OrdreMission creer(OrdreMissionRequest req) {
        User csa = currentUser();
        List<Region> regions = resoudreRegions(req.getRegionIds());
        List<LigneMission> lignes = resoudreLignes(req.getLignes());

        OrdreMission ordre = OrdreMission.builder()
                .regionIds(req.getRegionIds())
                .regionNoms(regions.stream().map(Region::getName).collect(Collectors.toList()))
                .motif(req.getMotif())
                .dateDebut(req.getDateDebut())
                .dateFin(req.getDateFin())
                .lignes(lignes)
                .creeParId(csa.getId())
                .creeParNom(csa.getPersonnel().getFirstname() + " " + csa.getPersonnel().getLastname())
                .dateCreation(LocalDateTime.now())
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
        List<Region> regions = resoudreRegions(req.getRegionIds());
        List<LigneMission> lignes = resoudreLignes(req.getLignes());

        ordre.setRegionIds(req.getRegionIds());
        ordre.setRegionNoms(regions.stream().map(Region::getName).collect(Collectors.toList()));
        ordre.setMotif(req.getMotif());
        ordre.setDateDebut(req.getDateDebut());
        ordre.setDateFin(req.getDateFin());
        ordre.setLignes(lignes);

        return ordreMissionRepo.save(ordre);
    }

    private List<Region> resoudreRegions(List<String> regionIds) {
        return regionIds.stream()
                .map(id -> regionRepo.findById(id).orElseThrow(() -> new RuntimeException("Région introuvable : " + id)))
                .collect(Collectors.toList());
    }

    private List<LigneMission> resoudreLignes(List<LigneMissionRequest> lignesReq) {
        return lignesReq.stream().map(this::resoudreLigne).collect(Collectors.toList());
    }

    private LigneMission resoudreLigne(LigneMissionRequest req) {
        AgentResolu agent = resoudreAgent(req.getAgentId());

        boolean disponibiliteVoiture = Boolean.TRUE.equals(req.getDisponibiliteVoiture());
        Voiture voiture = disponibiliteVoiture
                ? resoudreVoitureDeLAgent(agent)
                : resoudreVoitureChoisie(req.getVoitureId());

        return LigneMission.builder()
                .agentId(agent.id())
                .agentNom(agent.nom())
                .disponibiliteVoiture(disponibiliteVoiture)
                .voitureId(voiture.getId())
                .voitureImmatriculation(voiture.getImmatriculation())
                .build();
    }

    // Un agent de mission est soit un compte User (personnel interne), soit un Personnel
    // autonome sans compte (personnel externe importé, cf. PersonnelRepository).
    private record AgentResolu(String id, String nom, boolean externe) {}

    private AgentResolu resoudreAgent(String agentId) {
        Optional<User> user = userRepository.findById(agentId);
        if (user.isPresent()) {
            Personnel p = user.get().getPersonnel();
            return new AgentResolu(user.get().getId(), p.getFirstname() + " " + p.getLastname(), false);
        }
        Personnel personnel = personnelRepo.findById(agentId)
                .orElseThrow(() -> new RuntimeException("Agent introuvable : " + agentId));
        return new AgentResolu(personnel.getId(), personnel.getFirstname() + " " + personnel.getLastname(), true);
    }

    private Voiture resoudreVoitureDeLAgent(AgentResolu agent) {
        Optional<Voiture> voiture = agent.externe()
                ? voitureRepo.findByProprietairePersonnelIdAndActifTrue(agent.id())
                : voitureRepo.findByProprietaireAgentIdAndActifTrue(agent.id());
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
        ordre.setDateAnnulation(LocalDateTime.now());
        return ordreMissionRepo.save(ordre);
    }

    public List<OrdreMission> mesMissions() {
        return ordreMissionRepo.findByLignes_AgentIdOrderByDateCreationDesc(currentUser().getId());
    }

    public List<OrdreMission> toutes() {
        return ordreMissionRepo.findAllByOrderByDateCreationDesc();
    }

    private User currentUser() {
        String login = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByLogin(login).orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
    }
}
