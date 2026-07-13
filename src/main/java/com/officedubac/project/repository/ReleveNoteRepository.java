package com.officedubac.project.repository;

import com.officedubac.project.models.ReleveNotes;
import com.officedubac.project.models.SerieReleve;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface ReleveNoteRepository extends MongoRepository<ReleveNotes, String>
{
    List<ReleveNotes> findBySerieId(String serieId);
    List<ReleveNotes> findByNumeroTable(String numeroTable);
}
