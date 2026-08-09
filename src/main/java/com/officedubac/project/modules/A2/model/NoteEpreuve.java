package com.officedubac.project.modules.A2.model;

public class NoteEpreuve {

    /** Code de la matière, cf. {@link MatieresA1} */
    private String matiereCode;

    /** Note obtenue sur 20 (entier, comme sur le formulaire "en nombres entiers") */
    private Integer note;

    /** Points obtenus = note * coefficient (calculé automatiquement par le service) */
    private Integer pointsObtenus;

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
