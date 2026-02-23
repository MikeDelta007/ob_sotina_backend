package com.officedubac.project.repository;

import com.officedubac.project.dto.HoraireRequest;
import com.officedubac.project.models.FusionRepartitionTirage;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HoraireRequestRepository extends MongoRepository<HoraireRequest, String>
{

}
