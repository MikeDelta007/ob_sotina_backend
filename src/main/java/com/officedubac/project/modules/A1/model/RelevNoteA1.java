package com.officedubac.project.modules.A1.model;

import com.officedubac.project.modules.A1.model.Enums.DecisionJury;
import com.officedubac.project.modules.A1.model.Enums.Mention;
import com.officedubac.project.modules.A1.model.Enums.TypeSession;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.time.Instant;

@Document(collection = "relevesNotesA1")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RelevNoteA1 {

    @Id
    private String id;

    // ---- En-tête du formulaire ----
    private TypeSession session;      // NORMALE ou REMPLACEMENT
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
    private DecisionJury decisionPremierGroupe;
    private Mention mentionPremierGroupe;
    private DecisionJury decisionDeuxiemeGroupe;
    private Mention mentionDeuxiemeGroupe;

    private String lieuDelivrance;
    private LocalDate dateDelivrance;
    private String presidentJury;
    private Instant createdAt;
}
