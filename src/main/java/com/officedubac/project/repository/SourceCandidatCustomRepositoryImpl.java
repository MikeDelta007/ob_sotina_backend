package com.officedubac.project.repository;

import com.officedubac.project.models.SourceCandidat;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SourceCandidatCustomRepositoryImpl implements SourceCandidatCustomRepository {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public Page<String> findDistinctAcademies(Pageable pageable) {

        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("acaCentEcrit").ne(null)),
                Aggregation.group("acaCentEcrit"),
                Aggregation.sort(Sort.Direction.ASC, "_id"),
                Aggregation.skip(pageable.getOffset()),
                Aggregation.limit(pageable.getPageSize())
        );

        AggregationResults<Document> results =
                mongoTemplate.aggregate(aggregation, "sourceCandidat", Document.class);

        List<String> academies = results.getMappedResults()
                .stream()
                .map(d -> d.getString("_id"))
                .toList();

        // ✅ CORRECTION ICI
        List<String> distinctAca = mongoTemplate
                .query(SourceCandidat.class)
                .distinct("acaCentEcrit")
                .as(String.class)
                .all();

        long total = distinctAca.size();

        return new PageImpl<>(academies, pageable, total);
    }
}

