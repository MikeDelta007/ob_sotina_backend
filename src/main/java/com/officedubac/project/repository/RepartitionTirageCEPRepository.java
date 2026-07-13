package com.officedubac.project.repository;

import com.officedubac.project.models.RepartitionTirageCEP;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RepartitionTirageCEPRepository extends MongoRepository<RepartitionTirageCEP, String> {

    List<RepartitionTirageCEP> findBySession(int session);

    void deleteBySession(int session);


    Optional<RepartitionTirageCEP> findByJuryAndCentreEcrit(Integer jury, String centreEcrit);
}