package com.officedubac.project.repository;

import com.officedubac.project.models.ImportMeta;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImportMetaRepository extends MongoRepository<ImportMeta, String>
{
}
