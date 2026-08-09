package com.officedubac.project.modules.G.repository;

import com.officedubac.project.modules.G.model.RelevNoteG;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RelevNoteGRepository extends MongoRepository<RelevNoteG, String> {

    Page<RelevNoteG> findByCandidat_NumeroTableContainingIgnoreCaseAndAnnee(
            String numeroTable, Integer annee, Pageable pageable);

    Page<RelevNoteG> findByCandidat_NumeroTableContainingIgnoreCase(String numeroTable, Pageable pageable);

    Page<RelevNoteG> findByAnnee(Integer annee, Pageable pageable);
}
