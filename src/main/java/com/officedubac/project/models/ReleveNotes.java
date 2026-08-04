package com.officedubac.project.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "releve_notes")
@Builder
public class ReleveNotes
{
    @Id
    private String id;
    @Indexed
    private String serieId;
    private String serieCode;
    private String serieLibelle;
    private String nom;
    private String prenom;
    @Field("date_naissance")
    private LocalDate dateNaissance;
    private String lieuNaissance;
    private String centreExamen;
    @Indexed
    private String numeroTable;
    private String session;
    private String annee;
    private Double moyenneGenerale;
    private String mention;
    private List<LigneNote> lignesGroupe1 = new ArrayList<>();
    private List<LigneNote> lignesGroupe2 = new ArrayList<>();
    @CreatedDate
    private Instant createdAt;
}
