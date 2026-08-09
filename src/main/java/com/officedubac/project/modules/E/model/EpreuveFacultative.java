package com.officedubac.project.modules.E.model;

/**
 * Bloc "EPR. FACULTATIVES" : Langue / Dessin, Musique ou Couture.
 * Seuls les points AU-DESSUS de la moyenne (10/20) comptent, et uniquement
 * en cas de mention BIEN ou TRES BIEN (règle imprimée sur le formulaire).
 */
public class EpreuveFacultative {

    private TypeFacultative type;
    private Integer note;
    private Integer pointsAuDessusMoyenne;

    public TypeFacultative getType() { return type; }
    public void setType(TypeFacultative type) { this.type = type; }

    public Integer getNote() { return note; }
    public void setNote(Integer note) { this.note = note; }

    public Integer getPointsAuDessusMoyenne() { return pointsAuDessusMoyenne; }
    public void setPointsAuDessusMoyenne(Integer pointsAuDessusMoyenne) { this.pointsAuDessusMoyenne = pointsAuDessusMoyenne; }
}
