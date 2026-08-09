package com.officedubac.project.modules.A4.repository;

import com.officedubac.project.modules.A4.model.RelevNoteA4;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RelevNoteA4Repository extends MongoRepository<RelevNoteA4, String> {

    Page<RelevNoteA4> findByCandidat_NumeroTableContainingIgnoreCaseAndAnnee(
            String numeroTable, Integer annee, Pageable pageable);

    Page<RelevNoteA4> findByCandidat_NumeroTableContainingIgnoreCase(String numeroTable, Pageable pageable);

    Page<RelevNoteA4> findByAnnee(Integer annee, Pageable pageable);
}
