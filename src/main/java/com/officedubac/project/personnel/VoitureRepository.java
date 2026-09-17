package com.officedubac.project.personnel;

import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;
import java.util.Optional;

public interface VoitureRepository extends MongoRepository<Voiture, String> {
    List<Voiture> findByActifTrue();
    Optional<Voiture> findByProprietaireAgentIdAndActifTrue(String proprietaireAgentId);
    Optional<Voiture> findByProprietairePersonnelIdAndActifTrue(String proprietairePersonnelId);
}
