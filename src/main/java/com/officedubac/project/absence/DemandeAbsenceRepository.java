package com.officedubac.project.absence;

import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface DemandeAbsenceRepository extends MongoRepository<DemandeAbsence, String> {
    List<DemandeAbsence> findByDemandeurIdAndTypeOrderByDateCreationDesc(String demandeurId, TypeAbsence type);
    List<DemandeAbsence> findByStatutAndTypeOrderByDateCreationDesc(StatutAbsence statut, TypeAbsence type);
    List<DemandeAbsence> findByStatutAndTypeAndDivisionIdInOrderByDateCreationDesc(StatutAbsence statut, TypeAbsence type, List<String> divisionIds);
    List<DemandeAbsence> findByTypeAndDivisionIdInOrderByDateCreationDesc(TypeAbsence type, List<String> divisionIds);
    List<DemandeAbsence> findByTypeOrderByDateCreationDesc(TypeAbsence type);
}
