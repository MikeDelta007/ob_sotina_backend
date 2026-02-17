package com.officedubac.project.repository;

import com.officedubac.project.models.RepartitionFeuilleCEP;
import com.officedubac.project.models.RepartitionFeuilleCES;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RepartitionFeuilleCESRepository extends MongoRepository<RepartitionFeuilleCES, String>
{
    List<RepartitionFeuilleCES> findBySession(int session);

    void deleteBySession(int session);
}
