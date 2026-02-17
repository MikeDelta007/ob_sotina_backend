package com.officedubac.project.repository;

import com.officedubac.project.models.RepartitionTirageCEP;
import com.officedubac.project.models.RepartitionTirageCES;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RepartitionTirageCSRepository extends MongoRepository<RepartitionTirageCES, String> {

    List<RepartitionTirageCES> findBySession(int session);

    void deleteBySession(int session);
}