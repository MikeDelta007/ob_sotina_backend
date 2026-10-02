package com.officedubac.project.models;

public enum Role
{
    ADMIN,
    PEDAGOGIE,
    PLANIFICATION,
    // Agents de la Division Scolarité, avec leur propre chef de service — même logique que
    // PEDAGOGIE/PLANIFICATION.
    SCOLARITE,
    CHEF_SERVICE,
    CSA,
    DIRECTEUR,
    // Gère les validations du Directeur en son absence (ex. expressions de besoin, tickets
    // restaurant/carburant, congés/autorisations d'absence).
    ASSISTANTE_DIRECTEUR,
    CHEF_COMPTABLE,
    AGENT_COMPTABLE,
    // Personnel sans fonction de gestion particulière : accès au module personnel en
    // libre-service (Mon profil, Absences, Missions) uniquement.
    AGENT,
}
