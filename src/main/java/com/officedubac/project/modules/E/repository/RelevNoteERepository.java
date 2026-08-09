package com.officedubac.project.modules.E.repository;

import com.officedubac.project.modules.E.model.RelevNoteE;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RelevNoteERepository extends MongoRepository<RelevNoteE, String> {

    Page<RelevNoteE> findByCandidat_NumeroTableContainingIgnoreCaseAndAnnee(
            String numeroTable, Integer annee, Pageable pageable);

    Page<RelevNoteE> findByCandidat_NumeroTableContainingIgnoreCase(String numeroTable, Pageable pageable);

    Page<RelevNoteE> findByAnnee(Integer annee, Pageable pageable);
}
