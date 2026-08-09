package com.officedubac.project.modules.a1deuxiemepartie.repository;

import com.officedubac.project.modules.a1deuxiemepartie.model.ReleveA1DeuxiemePartie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ReleveA1DeuxiemePartieRepository extends MongoRepository<ReleveA1DeuxiemePartie, String> {

    Page<ReleveA1DeuxiemePartie> findByCandidat_NumeroTableContainingIgnoreCase(String numeroTable, Pageable pageable);
}
