package com.officedubac.project.modules.A3.repository;

import com.officedubac.project.modules.A3.model.RelevNoteA3;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RelevNoteA3Repository extends MongoRepository<RelevNoteA3, String> {

    Page<RelevNoteA3> findByCandidat_NumeroTableContainingIgnoreCaseAndAnnee(
            String numeroTable, Integer annee, Pageable pageable);

    Page<RelevNoteA3> findByCandidat_NumeroTableContainingIgnoreCase(String numeroTable, Pageable pageable);

    Page<RelevNoteA3> findByAnnee(Integer annee, Pageable pageable);
}
