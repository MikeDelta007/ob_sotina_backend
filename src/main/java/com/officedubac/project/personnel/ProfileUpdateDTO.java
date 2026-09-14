package com.officedubac.project.personnel;

import com.officedubac.project.models.Civilite;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// Champs qu'un agent peut modifier lui-même sur son propre compte.
// matricule/service/fonction restent gérés uniquement par l'ADMIN (editions-systeme/acces).
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileUpdateDTO {
    private String phone;
    private String email;
    private Civilite civilite;
    private String bank;
    private String code_bank;
    private String code_agc;
    private String num_compte;
    private String key_rib;
    private String matricule_voiture;
}
