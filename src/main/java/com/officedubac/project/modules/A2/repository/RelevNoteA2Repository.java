package com.officedubac.project.modules.A2.repository;

import com.officedubac.project.modules.A2.model.RelevNoteA2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RelevNoteA2Repository extends MongoRepository<RelevNoteA2, String> {

    Page<RelevNoteA2> findByCandidat_NumeroTableContainingIgnoreCaseAndAnnee(
            String numeroTable, Integer annee, Pageable pageable);

    Page<RelevNoteA2> findByCandidat_NumeroTableContainingIgnoreCase(String numeroTable, Pageable pageable);

    Page<RelevNoteA2> findByAnnee(Integer annee, Pageable pageable);
}
