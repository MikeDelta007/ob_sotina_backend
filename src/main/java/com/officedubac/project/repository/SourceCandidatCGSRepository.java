package com.officedubac.project.repository;

import com.officedubac.project.models.SourceCandidatCGS;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SourceCandidatCGSRepository extends MongoRepository<SourceCandidatCGS, String>, SourceCandidatCGSCustomRepository
{
    List<SourceCandidatCGS> findBySession(int session);

    List<SourceCandidatCGS> findByAcademia(String aca);
}
