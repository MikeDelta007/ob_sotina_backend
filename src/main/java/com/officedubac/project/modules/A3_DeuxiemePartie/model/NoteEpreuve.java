package com.officedubac.project.modules.A3_DeuxiemePartie.model;

public class NoteEpreuve {

    private String matiereCode;
    private Integer note;           // sur 20
    private Integer pointsObtenus;  // note * coefficient

    public NoteEpreuve() { }

    public NoteEpreuve(String matiereCode, Integer note) {
        this.matiereCode = matiereCode;
        this.note = note;
    }

    public String getMatiereCode() { return matiereCode; }
    public void setMatiereCode(String matiereCode) { this.matiereCode = matiereCode; }

    public Integer getNote() { return note; }
    public void setNote(Integer note) { this.note = note; }

    public Integer getPointsObtenus() { return pointsObtenus; }
    public void setPointsObtenus(Integer pointsObtenus) { this.pointsObtenus = pointsObtenus; }
}
