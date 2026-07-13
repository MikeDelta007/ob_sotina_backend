package com.officedubac.project.repository;

import com.officedubac.project.models.SerieReleve;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface SerieReleveRepository extends MongoRepository<SerieReleve, String>
{
    Optional<SerieReleve> findByCode(String code);
}
