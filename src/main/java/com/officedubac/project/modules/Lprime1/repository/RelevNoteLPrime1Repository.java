package com.officedubac.project.modules.Lprime1.repository;

import com.officedubac.project.modules.Lprime1.model.RelevNoteLPrime1;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RelevNoteLPrime1Repository extends MongoRepository<RelevNoteLPrime1, String> {

    Page<RelevNoteLPrime1> findByCandidat_NumeroTableContainingIgnoreCaseAndAnnee(
            String numeroTable, Integer annee, Pageable pageable);

    Page<RelevNoteLPrime1> findByCandidat_NumeroTableContainingIgnoreCase(String numeroTable, Pageable pageable);

    Page<RelevNoteLPrime1> findByAnnee(Integer annee, Pageable pageable);
}
