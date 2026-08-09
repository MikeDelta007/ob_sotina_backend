package com.officedubac.project.modules.c2emepartie.repository;

import com.officedubac.project.modules.c2emepartie.model.ReleveC2emePartie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ReleveC2emePartieRepository extends MongoRepository<ReleveC2emePartie, String> {

    Page<ReleveC2emePartie> findByCandidat_NumeroTableContainingIgnoreCase(String numeroTable, Pageable pageable);
}
