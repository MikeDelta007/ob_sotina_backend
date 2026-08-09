package com.officedubac.project.modules.a3deuxiemepartie.repository;

import com.officedubac.project.modules.a3deuxiemepartie.model.ReleveA3DeuxiemePartie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ReleveA3DeuxiemePartieRepository extends MongoRepository<ReleveA3DeuxiemePartie, String> {

    Page<ReleveA3DeuxiemePartie> findByCandidat_NumeroTableContainingIgnoreCase(String numeroTable, Pageable pageable);
}
