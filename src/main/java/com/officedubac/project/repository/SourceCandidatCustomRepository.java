package com.officedubac.project.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SourceCandidatCustomRepository
{
    Page<String> findDistinctAcademies(Pageable pageable);
}
