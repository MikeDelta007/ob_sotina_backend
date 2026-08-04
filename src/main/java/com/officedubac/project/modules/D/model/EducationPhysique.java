package com.officedubac.project.modules.D.model;

public class EducationPhysique {

    private Integer note; // sur 20
    private Boolean inapteOuControleAssidu;
    private Integer pointsPositifs;
    private Integer pointsNegatifs;

    public Integer getNote() { return note; }
    public void setNote(Integer note) { this.note = note; }

    public Boolean getInapteOuControleAssidu() { return inapteOuControleAssidu; }
    public void setInapteOuControleAssidu(Boolean inapteOuControleAssidu) { this.inapteOuControleAssidu = inapteOuControleAssidu; }

    public Integer getPointsPositifs() { return pointsPositifs; }
    public void setPointsPositifs(Integer pointsPositifs) { this.pointsPositifs = pointsPositifs; }

    public Integer getPointsNegatifs() { return pointsNegatifs; }
    public void setPointsNegatifs(Integer pointsNegatifs) { this.pointsNegatifs = pointsNegatifs; }
}
