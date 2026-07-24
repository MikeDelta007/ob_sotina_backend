package com.officedubac.project.caisseAvance;

import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface MandatementRepository extends MongoRepository<Mandatement, String> {
    List<Mandatement> findAllByOrderByDateCreationDesc();
    List<Mandatement> findByCreePar(String username);
    long countByCreePar(String username);
}
