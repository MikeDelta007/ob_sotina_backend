package com.officedubac.project.personnel;

public enum TypePersonnel {
    PERMANENT,
    PERSONNEL_SECURITE,
    PERSONNEL_APPUI,
    EXTERNE;

    // Jours de congés alloués par an selon le type de personnel
    public int joursConges() {
        return switch (this) {
            case PERMANENT, PERSONNEL_SECURITE -> 30;
            case PERSONNEL_APPUI -> 10;
            case EXTERNE -> 0;
        };
    }
}
