package com.officedubac.project.repository;

import com.officedubac.project.models.Profil;
import com.officedubac.project.models.RegleMatiere;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface RegleMatiereRepository extends MongoRepository<RegleMatiere, String>
{
    RegleMatiere findByCode(String codeM);

    List<RegleMatiere> findAllByCodeIn(List<String> codes);
}
