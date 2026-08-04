package com.officedubac.project.modules.A4.model;

import com.officedubac.project.modules.A4.model.Enums.DecisionJury;
import com.officedubac.project.modules.A4.model.Enums.Mention;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "relevesNotesA4")
public class ReleveA4 {

    @Id
    private String id;

    private String juryNumero;
    private String centre;
    private String session; // "session de ......." (texte libre : ex. "1976" ou une année)
    private Candidat candidat;

    // ---- 1er groupe d'épreuves (barème 340) ----
    private List<NoteEpreuve> notesPremierGroupe = new ArrayList<>();
    private Integer totalPremierGroupe; // sur 340 (= 1er TOTAL = 2e TOTAL, reporté)

    // ---- 2eme groupe d'épreuves ----
    private Integer reportPremierTotal; // = totalPremierGroupe
    private List<NoteEpreuve> notesDeuxiemeGroupe = new ArrayList<>();
    private List<EpreuveOraleControle> epreuvesOralesControle = new ArrayList<>();
    private List<EpreuveFacultative> epreuvesFacultatives = new ArrayList<>();
    private EducationPhysique educationPhysique;

    private Integer totalProvisoire;
    private Integer totalDefinitif; // sur 400

    private DecisionJury decisionPremierGroupe;
    private Mention mentionPremierGroupe;
    private DecisionJury decisionDeuxiemeGroupe;
    private Mention mentionDeuxiemeGroupe;

    private String lieuDelivrance; // "Dakar" par défaut sur ce formulaire
    private LocalDate dateDelivrance;
    private String presidentJury;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getJuryNumero() { return juryNumero; }
    public void setJuryNumero(String juryNumero) { this.juryNumero = juryNumero; }

    public String getCentre() { return centre; }
    public void setCentre(String centre) { this.centre = centre; }

    public String getSession() { return session; }
    public void setSession(String session) { this.session = session; }

    public Candidat getCandidat() { return candidat; }
    public void setCandidat(Candidat candidat) { this.candidat = candidat; }

    public List<NoteEpreuve> getNotesPremierGroupe() { return notesPremierGroupe; }
    public void setNotesPremierGroupe(List<NoteEpreuve> notesPremierGroupe) { this.notesPremierGroupe = notesPremierGroupe; }

    public Integer getTotalPremierGroupe() { return totalPremierGroupe; }
    public void setTotalPremierGroupe(Integer totalPremierGroupe) { this.totalPremierGroupe = totalPremierGroupe; }

    public Integer getReportPremierTotal() { return reportPremierTotal; }
    public void setReportPremierTotal(Integer reportPremierTotal) { this.reportPremierTotal = reportPremierTotal; }

    public List<NoteEpreuve> getNotesDeuxiemeGroupe() { return notesDeuxiemeGroupe; }
    public void setNotesDeuxiemeGroupe(List<NoteEpreuve> notesDeuxiemeGroupe) { this.notesDeuxiemeGroupe = notesDeuxiemeGroupe; }

    public List<EpreuveOraleControle> getEpreuvesOralesControle() { return epreuvesOralesControle; }
    public void setEpreuvesOralesControle(List<EpreuveOraleControle> epreuvesOralesControle) { this.epreuvesOralesControle = epreuvesOralesControle; }

    public List<EpreuveFacultative> getEpreuvesFacultatives() { return epreuvesFacultatives; }
    public void setEpreuvesFacultatives(List<EpreuveFacultative> epreuvesFacultatives) { this.epreuvesFacultatives = epreuvesFacultatives; }

    public EducationPhysique getEducationPhysique() { return educationPhysique; }
    public void setEducationPhysique(EducationPhysique educationPhysique) { this.educationPhysique = educationPhysique; }

    public Integer getTotalProvisoire() { return totalProvisoire; }
    public void setTotalProvisoire(Integer totalProvisoire) { this.totalProvisoire = totalProvisoire; }

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
