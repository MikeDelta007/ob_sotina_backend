package com.officedubac.project.modules.A2.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EducationPhysique {

    private Integer note; // sur 20
    private Integer pointsPositifs;
    private Integer pointsNegatifs;
}
