package com.officedubac.project.repository;

import com.officedubac.project.models.SourceCandidat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SourceCandidatRepository extends MongoRepository<SourceCandidat, String>, SourceCandidatCustomRepository
{
    List<SourceCandidat> findBySession(int session);
    List<SourceCandidat> findByCentreEcritSecondaireIsNotNull();
    List<SourceCandidat> findByAcaCentEcrit(String aca);
}
