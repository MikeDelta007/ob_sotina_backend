package com.officedubac.project.modules.L1B.repository;

import com.officedubac.project.modules.L1B.model.RelevNoteL1B;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RelevNoteL1BRepository extends MongoRepository<RelevNoteL1B, String> {

    Page<RelevNoteL1B> findByCandidat_NumeroTableContainingIgnoreCaseAndAnnee(
            String numeroTable, Integer annee, Pageable pageable);

    Page<RelevNoteL1B> findByCandidat_NumeroTableContainingIgnoreCase(String numeroTable, Pageable pageable);

    Page<RelevNoteL1B> findByAnnee(Integer annee, Pageable pageable);
}
