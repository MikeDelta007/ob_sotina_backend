package com.officedubac.project.repository;

import com.officedubac.project.models.FusionRepartitionTirage;
import com.officedubac.project.models.RepartitionTirageCEP;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FusionRepartitionTirageRepository extends MongoRepository<FusionRepartitionTirage, String>
{
    @Query("{ 'jury' : { $nin : ?0 } }")
    List<FusionRepartitionTirage> findAllByJuryNotIn(List<Integer> jurysExclus);
}