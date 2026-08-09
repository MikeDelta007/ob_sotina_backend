package com.officedubac.project.modules.S4.repository;

import com.officedubac.project.modules.S4.model.RelevNoteS4;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RelevNoteS4Repository extends MongoRepository<RelevNoteS4, String> {

    Page<RelevNoteS4> findByCandidat_NumeroTableContainingIgnoreCaseAndAnnee(
            String numeroTable, Integer annee, Pageable pageable);

    Page<RelevNoteS4> findByCandidat_NumeroTableContainingIgnoreCase(String numeroTable, Pageable pageable);

    Page<RelevNoteS4> findByAnnee(Integer annee, Pageable pageable);
}
