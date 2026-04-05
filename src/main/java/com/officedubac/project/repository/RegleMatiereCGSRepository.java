package com.officedubac.project.repository;

import com.officedubac.project.models.RegleMatiere;
import com.officedubac.project.models.RegleMatiereCGS;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RegleMatiereCGSRepository extends MongoRepository<RegleMatiereCGS, String>
{
    RegleMatiereCGS findByValeur(String codeM);

    List<RegleMatiereCGS> findAllByValeurIn(List<String> codes);
}
