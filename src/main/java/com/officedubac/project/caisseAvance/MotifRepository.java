package com.officedubac.project.caisseAvance;

import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface MotifRepository extends MongoRepository<Motif, String> {
    List<Motif> findByActifTrueOrderByLibelleAsc();
    // "systeme différent de true" (et non "= false") : les motifs créés avant l'ajout du champ n'en
    // ont pas en base, et Mongo ne les renvoie pas pour {systeme: false}.
    List<Motif> findByActifTrueAndSystemeNotOrderByLibelleAsc(boolean systeme);
    List<Motif> findBySystemeNotOrderByLibelleAsc(boolean systeme);
    java.util.Optional<Motif> findFirstByLibelleAndSystemeTrue(String libelle);
}
