package com.officedubac.project.modules.G1.repository;

import com.officedubac.project.modules.G1.model.RelevNoteG1;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RelevNoteG1Repository extends MongoRepository<RelevNoteG1, String> {

    Page<RelevNoteG1> findByCandidat_NumeroTableContainingIgnoreCaseAndAnnee(
            String numeroTable, Integer annee, Pageable pageable);

    Page<RelevNoteG1> findByCandidat_NumeroTableContainingIgnoreCase(String numeroTable, Pageable pageable);

    Page<RelevNoteG1> findByAnnee(Integer annee, Pageable pageable);
}
