package com.officedubac.project.modules.S5.repository;

import com.officedubac.project.modules.S5.model.RelevNoteS5;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RelevNoteS5Repository extends MongoRepository<RelevNoteS5, String> {

    Page<RelevNoteS5> findByCandidat_NumeroTableContainingIgnoreCaseAndAnnee(
            String numeroTable, Integer annee, Pageable pageable);

    Page<RelevNoteS5> findByCandidat_NumeroTableContainingIgnoreCase(String numeroTable, Pageable pageable);

    Page<RelevNoteS5> findByAnnee(Integer annee, Pageable pageable);
}
