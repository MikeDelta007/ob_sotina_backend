package com.officedubac.project.modules.D.repository;

import com.officedubac.project.modules.D.model.RelevNoteD;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RelevNoteDRepository extends MongoRepository<RelevNoteD, String> {

    Page<RelevNoteD> findByCandidat_NumeroTableContainingIgnoreCaseAndAnnee(
            String numeroTable, Integer annee, Pageable pageable);

    Page<RelevNoteD> findByCandidat_NumeroTableContainingIgnoreCase(String numeroTable, Pageable pageable);

    Page<RelevNoteD> findByAnnee(Integer annee, Pageable pageable);
}
