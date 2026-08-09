package com.officedubac.project.modules.T2.repository;

import com.officedubac.project.modules.T2.model.RelevNoteT2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RelevNoteT2Repository extends MongoRepository<RelevNoteT2, String> {

    Page<RelevNoteT2> findByCandidat_NumeroTableContainingIgnoreCaseAndAnnee(
            String numeroTable, Integer annee, Pageable pageable);

    Page<RelevNoteT2> findByCandidat_NumeroTableContainingIgnoreCase(String numeroTable, Pageable pageable);

    Page<RelevNoteT2> findByAnnee(Integer annee, Pageable pageable);
}
