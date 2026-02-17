package com.officedubac.project.repository;

import com.officedubac.project.models.Acteurs;
import com.officedubac.project.models.Programmation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProgrammationRepository extends MongoRepository<Programmation, String>
{
    Programmation findTopByOrderByIdDesc();

    Programmation findByEdition(Long session);
}

