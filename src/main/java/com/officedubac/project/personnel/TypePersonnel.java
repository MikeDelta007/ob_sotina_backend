package com.officedubac.project.personnel;

public enum TypePersonnel {
    PERMANENT,
    PERSONNEL_APPUI;

    // Jours de congés alloués par an selon le type de personnel
    public int joursConges() {
        return this == PERMANENT ? 30 : 10;
    }
}
