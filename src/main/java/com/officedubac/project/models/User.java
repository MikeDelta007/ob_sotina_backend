package com.officedubac.project.models;

import com.officedubac.project.personnel.Division;
import com.officedubac.project.personnel.Fonction;
import com.officedubac.project.personnel.TypePersonnel;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Document(collection = "user")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User implements UserDetails
{
    @Id
    private String id;
    private String firstname;
    private String lastname;
    private String login;
    private String password;
    private String phone;
    private String email;
    private boolean state_account;
    private boolean first_connexion;
    private String sessionId = null;

    private Acteurs acteur;
    private Profil profil;

    // ── Informations personnel/RH (indépendantes d'Acteurs, dédié scolarité/examens) ──
    private String bank;
    private String matricule;
    private Civilite civilite;
    private Division division;
    private Fonction fonction;
    private String code_bank;
    private String matricule_voiture;
    private String code_agc;
    private String num_compte;
    private String key_rib;

    // ── Congés ──
    // PERMANENT (30j/an) ou PERSONNEL_APPUI (10j/an) ; détermine l'allocation annuelle
    private TypePersonnel typePersonnel;
    // Solde de jours de congés restants ; décrémenté à la validation finale d'une demande
    // d'absence, réinitialisé chaque 1er janvier selon typePersonnel (voir CongesResetScheduler)
    private Integer soldeConges;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(profil.getName().name()));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return login;
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }

}
