package com.officedubac.project.modules.G2.repository;

import com.officedubac.project.modules.G2.model.RelevNoteG2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RelevNoteG2Repository extends MongoRepository<RelevNoteG2, String> {

    Page<RelevNoteG2> findByCandidat_NumeroTableContainingIgnoreCaseAndAnnee(
            String numeroTable, Integer annee, Pageable pageable);

    Page<RelevNoteG2> findByCandidat_NumeroTableContainingIgnoreCase(String numeroTable, Pageable pageable);

    Page<RelevNoteG2> findByAnnee(Integer annee, Pageable pageable);
}
