package com.officedubac.project.modules.C_Deuxieme_Partie.model;

import com.officedubac.project.modules.C_Deuxieme_Partie.model.Enums.DecisionJury;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "relevesNotesCDeuxiemePartie")
public class ReleveCDeuxiemePartie {

    @Id
    private String id;

    private String juryNumero;
    private Candidat candidat;

    private List<NoteEpreuve> notesEcrites = new ArrayList<>();
    private Integer totalEcrit; // sur 280

    private List<NoteEpreuve> notesOrales = new ArrayList<>();
    private Integer totalOral;  // sur 120

    /** Ajustement EPS (+/-), tel qu'imprimé : "TOTAL GENERAL: ECRIT + ORAL + EPS" */
    private Integer ajustementEps;

    private Integer totalGeneral; // sur 400 (barème hors ajustement EPS)

    private DecisionJury decision;

    private String lieuDelivrance;
    private LocalDate dateDelivrance;
    private String presidentJury;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getJuryNumero() { return juryNumero; }
    public void setJuryNumero(String juryNumero) { this.juryNumero = juryNumero; }

    public Candidat getCandidat() { return candidat; }
    public void setCandidat(Candidat candidat) { this.candidat = candidat; }

    public List<NoteEpreuve> getNotesEcrites() { return notesEcrites; }
    public void setNotesEcrites(List<NoteEpreuve> notesEcrites) { this.notesEcrites = notesEcrites; }

    public Integer getTotalEcrit() { return totalEcrit; }
    public void setTotalEcrit(Integer totalEcrit) { this.totalEcrit = totalEcrit; }

    public List<NoteEpreuve> getNotesOrales() { return notesOrales; }
    public void setNotesOrales(List<NoteEpreuve> notesOrales) { this.notesOrales = notesOrales; }

    public Integer getTotalOral() { return totalOral; }
    public void setTotalOral(Integer totalOral) { this.totalOral = totalOral; }

    public Integer getAjustementEps() { return ajustementEps; }
    public void setAjustementEps(Integer ajustementEps) { this.ajustementEps = ajustementEps; }

    public Integer getTotalGeneral() { return totalGeneral; }
    public void setTotalGeneral(Integer totalGeneral) { this.totalGeneral = totalGeneral; }

    public DecisionJury getDecision() { return decision; }
    public void setDecision(DecisionJury decision) { this.decision = decision; }

    public String getLieuDelivrance() { return lieuDelivrance; }
    public void setLieuDelivrance(String lieuDelivrance) { this.lieuDelivrance = lieuDelivrance; }

    public LocalDate getDateDelivrance() { return dateDelivrance; }
    public void setDateDelivrance(LocalDate dateDelivrance) { this.dateDelivrance = dateDelivrance; }

    public String getPresidentJury() { return presidentJury; }
    public void setPresidentJury(String presidentJury) { this.presidentJury = presidentJury; }
}
