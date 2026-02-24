package com.officedubac.project.repository;

import com.officedubac.project.models.Profil;
import com.officedubac.project.models.RegleMatiere;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RegleMatiereRepository extends MongoRepository<RegleMatiere, String>
{

}
