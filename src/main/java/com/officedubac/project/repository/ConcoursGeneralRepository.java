package com.officedubac.project.repository;

import com.officedubac.project.models.Candidat;
import com.officedubac.project.models.ConcoursGeneral;
import com.officedubac.project.models.Departement;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConcoursGeneralRepository extends MongoRepository<ConcoursGeneral, String>
{
    @Query("{ 'level': ?0, 'specialite': ?1}")
    List<ConcoursGeneral> findByLevelAndSpecialite(String level, String specialite);

    List<ConcoursGeneral> findByEtablissementIdAndSession(String etablissementId, Long session);

    List<ConcoursGeneral> findByEtablissementIdAndSessionAndSpecialiteAndLevel(String etablissementId, Long session, String specialite, String level);

}
