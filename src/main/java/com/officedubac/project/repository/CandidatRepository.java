package com.officedubac.project.repository;

import com.officedubac.project.models.Candidat;
import com.officedubac.project.models.CentreEtatCivil;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CandidatRepository extends MongoRepository<Candidat, String>
{
    @Query("{ 'registry_num': ?0 }")
    Candidat findByRegistryNum(int registryNum);

    @Query("{ 'subject': ?0 }")
    List<Candidat> findBySubject(String subject);

    List<Candidat> findByEtablissementId(String etablissementId);

    List<Candidat> findByEtablissementIdAndSerieCodeAndSession(String etablissementId, String serieCode, Long session);

    List<Candidat> findByEtablissementIdAndSessionAndSerieCode(String etablissementId, Long session, String serieCode);

    List<Candidat> findByEtablissementIdAndSessionAndSerieCodeAndDecision(String etablissementId, Long session, String serieCode, int decision);

    List<Candidat> findByEtablissementIdAndSessionAndDecision(String etablissementId, Long session, int decision);

    List<Candidat> findByEtablissementIdAndSessionAndSubject(String etablissementId, Long session, String subject);

    List<Candidat> findByEtablissementIdAndSession(String etablissementId, Long session);

    // Dans CandidatRepository.java
    List<Candidat> findBySessionAndDecision(Long session, Integer decision);

    Long countBySessionAndEtablissement_IdAndEprFacListANotNull(Long session, String etablissementId);

    Long countBySessionAndEtablissement_IdAndEprFacListBNotNull(Long session, String etablissementId);

    Long countBySessionAndEtablissement_Id(Long session, String etablissementId);

    @Query("{ 'year_registry_num': ?0, 'registry_num': ?1, 'centreEtatCivil.name': ?2, 'session': ?3 }")
    Candidat findCandidate(int yearRegistryNum, String registryNum, String centreEtatCivilName, Long session);

    Candidat findByPhone1AndSession(String phone1, Long session);
    Candidat findByEmailAndSession(String email, Long session);

    Candidat findByDosNumberAndSessionAndEtablissement_Id(String dosNumber, Long session, String etablissementId);

    boolean existsBySubject(String wording);

    List<Candidat> findBySession(Long session);

}
