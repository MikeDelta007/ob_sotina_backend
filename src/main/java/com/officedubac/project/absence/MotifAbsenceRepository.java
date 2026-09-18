package com.officedubac.project.absence;

import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface MotifAbsenceRepository extends MongoRepository<MotifAbsence, String> {
    List<MotifAbsence> findByActifTrue();
}
