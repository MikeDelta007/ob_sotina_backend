package com.officedubac.project.modules.f1deuxiemepartie.repository;

import com.officedubac.project.modules.f1deuxiemepartie.model.ReleveF1DeuxiemePartie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ReleveF1DeuxiemePartieRepository extends MongoRepository<ReleveF1DeuxiemePartie, String> {

    Page<ReleveF1DeuxiemePartie> findByCandidat_NumeroTableContainingIgnoreCase(String numeroTable, Pageable pageable);
}
