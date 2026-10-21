package com.officedubac.project.mission;

import org.springframework.data.mongodb.repository.MongoRepository;
import java.time.LocalDate;
import java.util.List;

public interface OrdreMissionRepository extends MongoRepository<OrdreMission, String> {
    List<OrdreMission> findByLignes_AgentIdOrderByDateCreationDesc(String agentId);
    List<OrdreMission> findAllByOrderByDateCreationDesc();
    List<OrdreMission> findByLignes_AgentIdAndAnnuleFalse(String agentId);
    List<OrdreMission> findByDateDebutBetweenAndAnnuleFalseOrderByDateDebutAsc(LocalDate debut, LocalDate fin);
}
