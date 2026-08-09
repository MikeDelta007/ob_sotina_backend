package com.officedubac.project.modules.A2.model;

import com.officedubac.project.modules.A2.model.Enums;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "relevesNotesA2")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RelevNoteA2 {
    @Id
    private String id;

    // ---- En-tête du formulaire ----
    private Enums.TypeSession session;      // NORMALE ou REMPLACEMENT
    private String juryNumero;
    private Integer annee;
    private Candidat candidat;

    // ---- 1er groupe d'épreuves ----
    private List<NoteEpreuve> notesPremierGroupe = new ArrayList<>();
    private Integer totalPremierGroupe;      // sur 320 (= 2e total, reporté)

    // ---- 2eme groupe d'épreuves ----
    private Integer reportPremierTotal;      // = totalPremierGroupe, affiché en tête du 2e groupe
    private List<NoteEpreuve> notesDeuxiemeGroupe = new ArrayList<>();
    private List<EpreuveOraleControle> epreuvesOralesControle = new ArrayList<>();
    private List<EpreuveFacultative> epreuvesFacultatives = new ArrayList<>();
    private EducationPhysique educationPhysique;

    private Integer totalProvisoire;
    private Integer totalDefinitif;          // sur 400

    // ---- Décisions du jury ----
    private Enums.DecisionJury decisionPremierGroupe;
    private Enums.Mention mentionPremierGroupe;
    private Enums.DecisionJury decisionDeuxiemeGroupe;
    private Enums.Mention mentionDeuxiemeGroupe;

    private String lieuDelivrance;
    private LocalDate dateDelivrance;
    private String presidentJury;
    private Instant createdAt;
}
