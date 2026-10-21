package com.officedubac.project.mission;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface MotifMissionRepository extends MongoRepository<MotifMission, String> {
    List<MotifMission> findByActifTrueOrderByLibelleAsc();
    List<MotifMission> findByOrderByLibelleAsc();
}
