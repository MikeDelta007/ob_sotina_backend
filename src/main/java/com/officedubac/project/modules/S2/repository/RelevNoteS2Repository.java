package com.officedubac.project.modules.S2.repository;

import com.officedubac.project.modules.S2.model.RelevNoteS2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RelevNoteS2Repository extends MongoRepository<RelevNoteS2, String> {

    Page<RelevNoteS2> findByCandidat_NumeroTableContainingIgnoreCaseAndAnnee(
            String numeroTable, Integer annee, Pageable pageable);

    Page<RelevNoteS2> findByCandidat_NumeroTableContainingIgnoreCase(String numeroTable, Pageable pageable);

    Page<RelevNoteS2> findByAnnee(Integer annee, Pageable pageable);
}
