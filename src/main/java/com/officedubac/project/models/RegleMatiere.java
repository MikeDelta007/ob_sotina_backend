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
    private String code;
    private Set<String> series;
    private String type;
    private String champ;
    private String valeur;
    private String groupe;
    private String date1;
    private String heure1;
    private String date2;
    private String heure2;

}