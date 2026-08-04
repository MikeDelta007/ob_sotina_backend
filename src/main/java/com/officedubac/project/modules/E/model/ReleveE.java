package com.officedubac.project.modules.E.model;

import com.officedubac.project.modules.E.model.Enums.DecisionJury;
import com.officedubac.project.modules.E.model.Enums.Mention;
import com.officedubac.project.modules.E.model.Enums.TypeSession;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "relevesNotesE")
public class ReleveE {

    @Id
    private String id;

    private TypeSession session;
    private String juryNumero;
    private Integer annee;
    private Candidat candidat;

    // ---- 1er groupe d'épreuves (barème 640) ----
    private List<NoteEpreuve> notesPremierGroupe = new ArrayList<>();
    private Integer totalPremierGroupe; // sur 640 (= 1er TOTAL = 2eme TOTAL, reporté)

    // ---- 2eme groupe d'épreuves ----
    private Integer reportPremierTotal; // = totalPremierGroupe
    private List<EpreuveDeControle> epreuvesDeControle = new ArrayList<>();
    private List<EpreuveFacultative> epreuvesFacultatives = new ArrayList<>();
    private EducationPhysique educationPhysique;

    private Integer totalDefinitif; // sur 640

    private DecisionJury decisionPremierGroupe;
    private Mention mentionPremierGroupe;
    private DecisionJury decisionDeuxiemeGroupe;
    private Mention mentionDeuxiemeGroupe;

    private String lieuDelivrance;
    private LocalDate dateDelivrance;
    private String presidentJury;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public TypeSession getSession() { return session; }
    public void setSession(TypeSession session) { this.session = session; }

    public String getJuryNumero() { return juryNumero; }
    public void setJuryNumero(String juryNumero) { this.juryNumero = juryNumero; }

    public Integer getAnnee() { return annee; }
    public void setAnnee(Integer annee) { this.annee = annee; }

    public Candidat getCandidat() { return candidat; }
    public void setCandidat(Candidat candidat) { this.candidat = candidat; }

    public List<NoteEpreuve> getNotesPremierGroupe() { return notesPremierGroupe; }
    public void setNotesPremierGroupe(List<NoteEpreuve> notesPremierGroupe) { this.notesPremierGroupe = notesPremierGroupe; }

    public Integer getTotalPremierGroupe() { return totalPremierGroupe; }
    public void setTotalPremierGroupe(Integer totalPremierGroupe) { this.totalPremierGroupe = totalPremierGroupe; }

    public Integer getReportPremierTotal() { return reportPremierTotal; }
    public void setReportPremierTotal(Integer reportPremierTotal) { this.reportPremierTotal = reportPremierTotal; }

    public List<EpreuveDeControle> getEpreuvesDeControle() { return epreuvesDeControle; }
    public void setEpreuvesDeControle(List<EpreuveDeControle> epreuvesDeControle) { this.epreuvesDeControle = epreuvesDeControle; }

    public List<EpreuveFacultative> getEpreuvesFacultatives() { return epreuvesFacultatives; }
    public void setEpreuvesFacultatives(List<EpreuveFacultative> epreuvesFacultatives) { this.epreuvesFacultatives = epreuvesFacultatives; }

    public EducationPhysique getEducationPhysique() { return educationPhysique; }
    public void setEducationPhysique(EducationPhysique educationPhysique) { this.educationPhysique = educationPhysique; }

    public Integer getTotalDefinitif() { return totalDefinitif; }
    public void setTotalDefinitif(Integer totalDefinitif) { this.totalDefinitif = totalDefinitif; }

    public DecisionJury getDecisionPremierGroupe() { return decisionPremierGroupe; }
    public void setDecisionPremierGroupe(DecisionJury decisionPremierGroupe) { this.decisionPremierGroupe = decisionPremierGroupe; }

    public Mention getMentionPremierGroupe() { return mentionPremierGroupe; }
    public void setMentionPremierGroupe(Mention mentionPremierGroupe) { this.mentionPremierGroupe = mentionPremierGroupe; }

    public DecisionJury getDecisionDeuxiemeGroupe() { return decisionDeuxiemeGroupe; }
    public void setDecisionDeuxiemeGroupe(DecisionJury decisionDeuxiemeGroupe) { this.decisionDeuxiemeGroupe = decisionDeuxiemeGroupe; }

    public Mention getMentionDeuxiemeGroupe() { return mentionDeuxiemeGroupe; }
    public void setMentionDeuxiemeGroupe(Mention mentionDeuxiemeGroupe) { this.mentionDeuxiemeGroupe = mentionDeuxiemeGroupe; }

    public String getLieuDelivrance() { return lieuDelivrance; }
    public void setLieuDelivrance(String lieuDelivrance) { this.lieuDelivrance = lieuDelivrance; }

    public LocalDate getDateDelivrance() { return dateDelivrance; }
    public void setDateDelivrance(LocalDate dateDelivrance) { this.dateDelivrance = dateDelivrance; }

    public String getPresidentJury() { return presidentJury; }
    public void setPresidentJury(String presidentJury) { this.presidentJury = presidentJury; }
}
