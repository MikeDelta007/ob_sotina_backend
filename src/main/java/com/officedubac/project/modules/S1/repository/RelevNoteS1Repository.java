package com.officedubac.project.modules.S1.repository;

import com.officedubac.project.modules.S1.model.RelevNoteS1;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RelevNoteS1Repository extends MongoRepository<RelevNoteS1, String> {

    Page<RelevNoteS1> findByCandidat_NumeroTableContainingIgnoreCaseAndAnnee(
            String numeroTable, Integer annee, Pageable pageable);

    Page<RelevNoteS1> findByCandidat_NumeroTableContainingIgnoreCase(String numeroTable, Pageable pageable);

    Page<RelevNoteS1> findByAnnee(Integer annee, Pageable pageable);
}
