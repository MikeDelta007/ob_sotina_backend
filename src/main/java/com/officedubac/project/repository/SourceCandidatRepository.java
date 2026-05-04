package com.officedubac.project.repository;

import com.officedubac.project.models.SourceCandidat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface SourceCandidatRepository extends MongoRepository<SourceCandidat, String>, SourceCandidatCustomRepository
{
    List<SourceCandidat> findBySession(int session);
    @Query(value = "{ 'centreEcritSecondaire': { $ne: null } }")
    List<SourceCandidat> findCandidatsWithCentreSecondaire();

    @Query(value = "{ 'centreEcritSecondaire': null }")
    List<SourceCandidat> findLightCandidats();

    List<SourceCandidat> findByAcaCentEcrit(String aca);

    @Aggregation(pipeline = {
            "{ $group: { _id: '$serie', total: { $sum: 1 }, filles: { $sum: { $cond: [ { $eq: ['$gender', 'F'] }, 1, 0 ] } }, public: { $sum: { $cond: [ { $eq: ['$etablissement', 'public'] }, 1, 0 ] } }, prive: { $sum: { $cond: [ { $eq: ['$etablissement', 'prive'] }, 1, 0 ] } } } }",
            "{ $sort: { _id: 1 } }"
    })
    List<Map<String, Object>> getStatsBySerie();

    @Aggregation(pipeline = {
            "{ $group: { _id: null, total: { $sum: 1 } } }"
    })
    Long getTotalCandidats();
}
