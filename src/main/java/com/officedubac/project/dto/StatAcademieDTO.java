package com.officedubac.project.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class StatAcademieDTO
{
    private String inspectionAcademie;
    private long male;
    private long female;
}
