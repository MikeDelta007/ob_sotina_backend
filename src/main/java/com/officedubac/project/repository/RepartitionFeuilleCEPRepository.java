package com.officedubac.project.repository;

import com.officedubac.project.models.RepartitionFeuilleCEP;
import com.officedubac.project.models.RepartitionTirageCEP;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RepartitionFeuilleCEPRepository extends MongoRepository<RepartitionFeuilleCEP, String>
{
    List<RepartitionFeuilleCEP> findBySession(int session);

    void deleteBySession(int session);
}
