package com.officedubac.project.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "ligne_note")
@Builder
public class LigneNote
{
    private Integer groupe;
    private String matiereRefId; // null si série dynamique ou ligne libre
    private String libelleMatiere;
    private Double coefficient;
    private Double note;
    private Double points;
}
