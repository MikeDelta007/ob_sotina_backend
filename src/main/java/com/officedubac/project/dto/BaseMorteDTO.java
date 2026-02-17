package com.officedubac.project.dto;

import com.officedubac.project.models.TypeFiliere;
import com.officedubac.project.models.TypeSerie;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class BaseMorteDTO
{
        private int exclusionDuree;
}
