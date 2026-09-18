package com.officedubac.project.dto;

import com.officedubac.project.models.Acteurs;
import com.officedubac.project.models.Profil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// Création d'un compte à partir d'une fiche Personnel existante (déjà saisie manuellement ou
// importée par Excel) : aucune information d'identité/RH n'est ressaisie ici, seulement les
// informations de connexion.
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class CreerCompteDepuisPersonnelDTO {
    private String personnelId;
    private String login;
    private String password;
    private boolean state_account;
    private Profil profil;
    private Acteurs acteur;
}
