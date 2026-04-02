package com.officedubac.project.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Data
public class SourceCandidatCGSDTO
{
    private String firstname;//0
    private String lastname;//1
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date_birth;//2
    private String place_birth;//3
    private String gender;//10
    private String level;//9
    private String discipline;//11
    private String centreCompo;//12
    private String etab;//12
    private String academia;//12
    private String serie;//9
    private Long session;//9
}