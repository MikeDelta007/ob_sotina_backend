package com.officedubac.project.modules.F7.repository;

import com.officedubac.project.modules.F7.model.RelevNoteF7;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RelevNoteF7Repository extends MongoRepository<RelevNoteF7, String> {

    Page<RelevNoteF7> findByCandidat_NumeroTableContainingIgnoreCaseAndAnnee(
            String numeroTable, Integer annee, Pageable pageable);

    Page<RelevNoteF7> findByCandidat_NumeroTableContainingIgnoreCase(String numeroTable, Pageable pageable);

    Page<RelevNoteF7> findByAnnee(Integer annee, Pageable pageable);
}
