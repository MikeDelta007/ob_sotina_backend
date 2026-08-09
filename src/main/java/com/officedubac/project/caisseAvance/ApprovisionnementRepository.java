package com.officedubac.project.caisseAvance;

import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface ApprovisionnementRepository extends MongoRepository<Approvisionnement, String> {
    List<Approvisionnement> findAllByOrderByDateCreationDesc();
}
