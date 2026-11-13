package com.officedubac.project.axe;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CampagneRepository extends MongoRepository<Campagne, String> {
    List<Campagne> findByActifTrueOrderByDateCreationDesc();
    List<Campagne> findByOrderByDateCreationDesc();
}
