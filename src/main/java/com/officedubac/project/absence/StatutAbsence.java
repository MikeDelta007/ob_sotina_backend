package com.officedubac.project.absence;

public enum StatutAbsence {
    EN_ATTENTE_CHEF,
    EN_ATTENTE_CSA,
    EN_ATTENTE_DIRECTEUR,
    VALIDEE,
    REJETEE,
    // Une demande validée annulée par le Directeur (cf. DemandeAbsenceService.annuler) — le
    // solde qu'elle avait décompté est restitué à l'agent. Statut final, comme REJETEE.
    ANNULEE
}
