package com.officedubac.project.modules.B.repository;

import com.officedubac.project.modules.B.model.RelevNoteB;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RelevNoteBRepository extends MongoRepository<RelevNoteB, String> {

    Page<RelevNoteB> findByCandidat_NumeroTableContainingIgnoreCaseAndAnnee(
            String numeroTable, Integer annee, Pageable pageable);

    Page<RelevNoteB> findByCandidat_NumeroTableContainingIgnoreCase(String numeroTable, Pageable pageable);

    Page<RelevNoteB> findByAnnee(Integer annee, Pageable pageable);
}
