package com.officedubac.project.modules.L1A.model;

import com.officedubac.project.modules.L1A.model.Enums.DecisionJury;
import com.officedubac.project.modules.L1A.model.Enums.Mention;
import com.officedubac.project.modules.L1A.model.Enums.TypeSession;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "relevesNotesL1A")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RelevNoteL1A {

    @Id
    private String id;

    private TypeSession session;
    private String juryNumero;
    private Integer annee;
    private Candidat candidat;

    private List<NoteEpreuve> notesPremierGroupe = new ArrayList<>();
    private Integer totalPremierGroupe;

    private Integer reportPremierTotal;
    private List<NoteEpreuve> notesDeuxiemeGroupe = new ArrayList<>();
    private List<EpreuveOraleControle> epreuvesOralesControle = new ArrayList<>();
    private List<EpreuveFacultative> epreuvesFacultatives = new ArrayList<>();
    private EducationPhysique educationPhysique;

    private Integer totalProvisoire;
    private Integer totalDefinitif;

    private DecisionJury decisionPremierGroupe;
    private Mention mentionPremierGroupe;
    private DecisionJury decisionDeuxiemeGroupe;
    private Mention mentionDeuxiemeGroupe;

    private String lieuDelivrance;
    private LocalDate dateDelivrance;
    private String presidentJury;
    private Instant createdAt;
}
