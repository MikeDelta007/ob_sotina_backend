package com.officedubac.project.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Map;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "repartition_tirage_CEP")
public class RepartitionTirageCEP
{
    @Id
    private String id;

    private Integer session;
    private Integer jury;
    private String centreEcrit;
    private String academia;
    private Long effectif;
    private List<String> series;

    private Map<String, GroupeMatiere> matieres;

}
