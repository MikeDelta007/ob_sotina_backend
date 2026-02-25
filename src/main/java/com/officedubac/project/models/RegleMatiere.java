package com.officedubac.project.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Set;

@Document(collection = "regle_matiere")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegleMatiere
{
    @Id
    private String id;
    private String code;          // ex: allemandLV1
    private Set<String> series;   // obligatoire pour type SERIE
    private String type;          // SERIE ou OPTION
    private String champ;         // matiere1/2/3 (si OPTION)
    private String valeur;        // valeur attendue (si OPTION)
    private String date1;
    private String heure1;
    private String date2;
    private String heure2;

}