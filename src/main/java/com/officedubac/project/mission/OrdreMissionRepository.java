package com.officedubac.project.mission;

import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface OrdreMissionRepository extends MongoRepository<OrdreMission, String> {
    List<OrdreMission> findByAgentIdOrderByDateCreationDesc(String agentId);
    List<OrdreMission> findAllByOrderByDateCreationDesc();
}
