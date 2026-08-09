package com.officedubac.project.modules.T1.repository;

import com.officedubac.project.modules.T1.model.RelevNoteT1;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RelevNoteT1Repository extends MongoRepository<RelevNoteT1, String> {

    Page<RelevNoteT1> findByCandidat_NumeroTableContainingIgnoreCaseAndAnnee(
            String numeroTable, Integer annee, Pageable pageable);

    Page<RelevNoteT1> findByCandidat_NumeroTableContainingIgnoreCase(String numeroTable, Pageable pageable);

    Page<RelevNoteT1> findByAnnee(Integer annee, Pageable pageable);
}
