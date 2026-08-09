package com.officedubac.project.modules.F1.repository;

import com.officedubac.project.modules.F1.model.RelevNoteF1;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RelevNoteF1Repository extends MongoRepository<RelevNoteF1, String> {

    Page<RelevNoteF1> findByCandidat_NumeroTableContainingIgnoreCaseAndAnnee(
            String numeroTable, Integer annee, Pageable pageable);

    Page<RelevNoteF1> findByCandidat_NumeroTableContainingIgnoreCase(String numeroTable, Pageable pageable);

    Page<RelevNoteF1> findByAnnee(Integer annee, Pageable pageable);
}
