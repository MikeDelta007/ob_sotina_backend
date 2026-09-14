package com.officedubac.project.absence;

import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface DemandeAbsenceRepository extends MongoRepository<DemandeAbsence, String> {
    List<DemandeAbsence> findByDemandeurIdOrderByDateCreationDesc(String demandeurId);
    List<DemandeAbsence> findByStatutOrderByDateCreationDesc(StatutAbsence statut);
    List<DemandeAbsence> findByStatutAndDivisionIdInOrderByDateCreationDesc(StatutAbsence statut, List<String> divisionIds);
}
