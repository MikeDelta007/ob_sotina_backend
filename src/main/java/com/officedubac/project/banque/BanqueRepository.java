package com.officedubac.project.banque;

import com.officedubac.project.banque.Banque;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BanqueRepository extends MongoRepository<Banque,String> {
    Optional<Banque> findByName(String name);
}
