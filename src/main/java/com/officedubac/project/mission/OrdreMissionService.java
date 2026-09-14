package com.officedubac.project.mission;

import com.officedubac.project.models.User;
import com.officedubac.project.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrdreMissionService {

    private final OrdreMissionRepository ordreMissionRepo;
    private final UserRepository userRepository;

    public OrdreMission creer(OrdreMissionRequest req) {
        User csa = currentUser();
        User agent = userRepository.findById(req.getAgentId())
                .orElseThrow(() -> new RuntimeException("Agent introuvable"));

        OrdreMission ordre = OrdreMission.builder()
                .agentId(agent.getId())
                .agentNom(agent.getFirstname() + " " + agent.getLastname())
                .destination(req.getDestination())
                .motif(req.getMotif())
                .dateDebut(req.getDateDebut())
                .dateFin(req.getDateFin())
                .creeParId(csa.getId())
                .creeParNom(csa.getFirstname() + " " + csa.getLastname())
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
        User agent = userRepository.findById(req.getAgentId())
                .orElseThrow(() -> new RuntimeException("Agent introuvable"));

        ordre.setAgentId(agent.getId());
        ordre.setAgentNom(agent.getFirstname() + " " + agent.getLastname());
        ordre.setDestination(req.getDestination());
        ordre.setMotif(req.getMotif());
        ordre.setDateDebut(req.getDateDebut());
        ordre.setDateFin(req.getDateFin());

        return ordreMissionRepo.save(ordre);
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
        return ordreMissionRepo.findByAgentIdOrderByDateCreationDesc(currentUser().getId());
    }

    public List<OrdreMission> toutes() {
        return ordreMissionRepo.findAllByOrderByDateCreationDesc();
    }

    private User currentUser() {
        String login = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByLogin(login).orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
    }
}
