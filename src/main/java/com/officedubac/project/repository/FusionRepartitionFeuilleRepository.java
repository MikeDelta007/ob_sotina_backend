package com.officedubac.project.repository;

import com.officedubac.project.models.FusionRepartitionFeuille;
import com.officedubac.project.models.FusionRepartitionTirage;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FusionRepartitionFeuilleRepository extends MongoRepository<FusionRepartitionFeuille, String>
{
    
}