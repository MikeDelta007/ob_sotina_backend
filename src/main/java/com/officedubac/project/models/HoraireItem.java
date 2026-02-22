package com.officedubac.project.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HoraireItem {

    private String date1;
    private String heure1;
    private String date2;
    private String heure2;
}