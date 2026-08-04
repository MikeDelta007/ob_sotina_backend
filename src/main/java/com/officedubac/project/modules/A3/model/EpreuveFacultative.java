package com.officedubac.project.modules.A3.model;

import com.officedubac.project.modules.A3.model.Enums.TypeFacultative;

/**
 * Bloc "EPR. FACULTATIVE(S)" du formulaire : Langue / Dessin, Couture ou
 * Musique. Seuls les points au-dessus de la moyenne (10/20) comptent.
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
