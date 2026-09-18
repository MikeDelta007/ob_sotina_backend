package com.officedubac.project.dto;

import com.officedubac.project.models.Acteurs;
import com.officedubac.project.models.Civilite;
import com.officedubac.project.models.Profil;
import com.officedubac.project.personnel.Division;
import com.officedubac.project.personnel.Fonction;
import com.officedubac.project.personnel.TypePersonnel;
import com.officedubac.project.personnel.Voiture;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class UserDTO {
    private String firstname;
    private String lastname;
    private String login;
    private String password;
    private String phone;
    private String email;
    private boolean state_account;
    private Profil profil;
    private Acteurs acteur;

    // ── Informations personnel/RH ──
    private String bank;
    private String matricule;
    private Civilite civilite;
    private Division division;
    private Fonction fonction;
    private String code_bank;
    private Voiture voiture;
    private String code_agc;
    private String num_compte;
    private String key_rib;

    // ── Congés ──
    private TypePersonnel typePersonnel;
    private Integer soldeConges;
}
