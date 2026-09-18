package com.officedubac.project.models;

public enum Role
{
    ADMIN,
    PEDAGOGIE,
    PLANIFICATION,
    CHEF_SERVICE,
    CSA,
    DIRECTEUR,
    CHEF_COMPTABLE,
    AGENT_COMPTABLE,
    // Personnel sans fonction de gestion particulière : accès au module personnel en
    // libre-service (Mon profil, Absences, Missions) uniquement.
    AGENT,
}
