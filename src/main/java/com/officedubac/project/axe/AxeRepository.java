package com.officedubac.project.axe;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface AxeRepository extends MongoRepository<Axe, String> {
    List<Axe> findByActifTrueOrderByNumeroAsc();
    List<Axe> findByOrderByNumeroAsc();
    Optional<Axe> findByNumero(int numero);
}
