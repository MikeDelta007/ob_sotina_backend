package com.officedubac.project.modules.L2.repository;

import com.officedubac.project.modules.L2.model.RelevNoteL2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RelevNoteL2Repository extends MongoRepository<RelevNoteL2, String> {

    Page<RelevNoteL2> findByCandidat_NumeroTableContainingIgnoreCaseAndAnnee(
            String numeroTable, Integer annee, Pageable pageable);

    Page<RelevNoteL2> findByCandidat_NumeroTableContainingIgnoreCase(String numeroTable, Pageable pageable);

    Page<RelevNoteL2> findByAnnee(Integer annee, Pageable pageable);
}
