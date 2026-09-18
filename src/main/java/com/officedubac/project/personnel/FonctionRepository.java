package com.officedubac.project.personnel;

import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface FonctionRepository extends MongoRepository<Fonction, String> {
    List<Fonction> findByActifTrue();
}
