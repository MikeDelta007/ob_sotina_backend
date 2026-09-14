package com.officedubac.project.personnel;

import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface DivisionRepository extends MongoRepository<Division, String> {
    List<Division> findByActifTrue();
}
