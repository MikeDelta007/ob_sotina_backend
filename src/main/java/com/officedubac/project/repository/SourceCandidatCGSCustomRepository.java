package com.officedubac.project.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SourceCandidatCGSCustomRepository
{
    Page<String> findDistinctAcademies(Pageable pageable);
}
