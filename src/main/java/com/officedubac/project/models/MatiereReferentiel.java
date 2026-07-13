package com.officedubac.project.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "matiere_referentiel")
@Builder
public class MatiereReferentiel
{
    @Id
    private String id;
    private String libelle;
    private Double coefficient;
    private Integer ordre;

}
