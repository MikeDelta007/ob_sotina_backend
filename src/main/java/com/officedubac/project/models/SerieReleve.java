package com.officedubac.project.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "serie_releve")
@Builder
public class SerieReleve
{
    @Id
    private String id;
    @Indexed(unique = true)
    private String code;
    private String libelle;
    private List<MatiereReferentiel> matieresGroupe1 = new ArrayList<>();
}
