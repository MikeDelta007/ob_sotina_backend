package com.officedubac.project.modules.S3.repository;

import com.officedubac.project.modules.S3.model.RelevNoteS3;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RelevNoteS3Repository extends MongoRepository<RelevNoteS3, String> {

    Page<RelevNoteS3> findByCandidat_NumeroTableContainingIgnoreCaseAndAnnee(
            String numeroTable, Integer annee, Pageable pageable);

    Page<RelevNoteS3> findByCandidat_NumeroTableContainingIgnoreCase(String numeroTable, Pageable pageable);

    Page<RelevNoteS3> findByAnnee(Integer annee, Pageable pageable);
}
