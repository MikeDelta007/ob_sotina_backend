package com.officedubac.project.modules.a2deuxiemepartie.repository;

import com.officedubac.project.modules.a2deuxiemepartie.model.ReleveA2DeuxiemePartie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ReleveA2DeuxiemePartieRepository extends MongoRepository<ReleveA2DeuxiemePartie, String> {

    Page<ReleveA2DeuxiemePartie> findByCandidat_NumeroTableContainingIgnoreCase(String numeroTable, Pageable pageable);
}
