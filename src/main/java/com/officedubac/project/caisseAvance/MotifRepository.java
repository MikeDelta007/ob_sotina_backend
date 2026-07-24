package com.officedubac.project.caisseAvance;

import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface MotifRepository extends MongoRepository<Motif, String> {
    List<Motif> findByActifTrue();
}
