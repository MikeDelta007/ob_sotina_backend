package com.officedubac.project.personnel;

import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface PersonnelRepository extends MongoRepository<Personnel, String> {
    List<Personnel> findByActifTrue();
    List<Personnel> findByDivision_IdInAndActifTrue(List<String> divisionIds);
}
