package com.officedubac.project.modules.L1A.repository;

import com.officedubac.project.modules.L1A.model.RelevNoteL1A;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RelevNoteL1ARepository extends MongoRepository<RelevNoteL1A, String> {

    Page<RelevNoteL1A> findByCandidat_NumeroTableContainingIgnoreCaseAndAnnee(
            String numeroTable, Integer annee, Pageable pageable);

    Page<RelevNoteL1A> findByCandidat_NumeroTableContainingIgnoreCase(String numeroTable, Pageable pageable);

    Page<RelevNoteL1A> findByAnnee(Integer annee, Pageable pageable);
}
