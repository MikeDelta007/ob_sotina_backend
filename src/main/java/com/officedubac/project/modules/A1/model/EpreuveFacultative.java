package com.officedubac.project.modules.A1.model;

/**
 * Bloc "EPR. FACULTATIVES" du formulaire : Langue / Dessin, Musique ou Couture.
 * Seuls les points AU-DESSUS de la moyenne (10/20) comptent, et uniquement
 * en cas de mention BIEN ou TRES BIEN (règle imprimée sur le formulaire).
 */
public class EpreuveFacultative {

    private TypeFacultative type;
    private Integer note; // sur 20
    private Integer pointsAuDessusMoyenne; // = max(0, note - 10)

    public TypeFacultative getType() { return type; }
    public void setType(TypeFacultative type) { this.type = type; }

    public Integer getNote() { return note; }
    public void setNote(Integer note) { this.note = note; }

    public Integer getPointsAuDessusMoyenne() { return pointsAuDessusMoyenne; }
    public void setPointsAuDessusMoyenne(Integer pointsAuDessusMoyenne) { this.pointsAuDessusMoyenne = pointsAuDessusMoyenne; }
}
