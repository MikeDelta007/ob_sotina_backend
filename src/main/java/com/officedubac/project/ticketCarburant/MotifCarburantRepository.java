package com.officedubac.project.ticketCarburant;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface MotifCarburantRepository extends MongoRepository<MotifCarburant, String> {
    List<MotifCarburant> findByActifTrueOrderByLibelleAsc();
    List<MotifCarburant> findByOrderByLibelleAsc();
}
