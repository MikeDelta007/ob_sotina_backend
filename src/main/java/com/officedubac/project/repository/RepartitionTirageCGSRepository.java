package com.officedubac.project.repository;

import com.officedubac.project.models.RepartitionTirageCEP;
import com.officedubac.project.models.RepartitionTirageCGS;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RepartitionTirageCGSRepository extends MongoRepository<RepartitionTirageCGS, String> {

    List<RepartitionTirageCGS> findBySession(int session);

    void deleteBySession(int session);

    List<RepartitionTirageCGS> findAllByOrderByDisciplineAsc();
}