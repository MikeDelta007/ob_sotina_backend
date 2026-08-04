package com.officedubac.project.modules.B.model;

import com.officedubac.project.modules.B.model.Enums.TypeFacultative;

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
