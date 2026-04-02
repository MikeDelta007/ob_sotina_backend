package com.officedubac.project.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Map;
import java.util.Set;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "repartition_tirage_CGS")
public class RepartitionTirageCGS
{
    @Id
    private String id;
    private Long session;
    private String centreEcrit;
    private String academia;
    private String discipline;
    private String level;
    private Long effectif;
    private List<String> series;

    private Map<String, GroupeMatiereCGS> matieres;

}
