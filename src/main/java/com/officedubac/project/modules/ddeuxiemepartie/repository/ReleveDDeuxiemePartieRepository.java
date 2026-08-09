package com.officedubac.project.modules.ddeuxiemepartie.repository;

import com.officedubac.project.modules.ddeuxiemepartie.model.ReleveDDeuxiemePartie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ReleveDDeuxiemePartieRepository extends MongoRepository<ReleveDDeuxiemePartie, String> {

    Page<ReleveDDeuxiemePartie> findByCandidat_NumeroTableContainingIgnoreCase(String numeroTable, Pageable pageable);
}
