package com.officedubac.project.modules.A1.repository;

import com.officedubac.project.modules.A1.model.RelevNoteA1;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RelevNoteA1Repository extends MongoRepository<RelevNoteA1, String> {

    Page<RelevNoteA1> findByCandidat_NumeroTableContainingIgnoreCaseAndAnnee(
            String numeroTable, Integer annee, Pageable pageable);

    Page<RelevNoteA1> findByCandidat_NumeroTableContainingIgnoreCase(String numeroTable, Pageable pageable);

    Page<RelevNoteA1> findByAnnee(Integer annee, Pageable pageable);
}
