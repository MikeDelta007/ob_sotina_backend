package com.officedubac.project.caisseAvance;

import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface CaisseAvanceRepository extends MongoRepository<CaisseAvance, String> {
    // On n'a qu'une seule caisse courante (le dernier document)
    Optional<CaisseAvance> findTopByOrderByDateCreationDesc();
}
