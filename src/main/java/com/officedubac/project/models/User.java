package com.officedubac.project.models;

import com.officedubac.project.personnel.Personnel;
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
    private String login;
    private String password;
    private boolean state_account;
    private boolean first_connexion;
    private String sessionId = null;

    private Acteurs acteur;
    private Profil profil;

    // Rôles supplémentaires, en plus du rôle principal (profil) : un même agent peut cumuler
    // plusieurs rôles (ex. agent informatique ET ADMIN, chef de la pédagogie ET PEDAGOGIE +
    // CHEF_SERVICE). Ils donnent accès aux menus/pages/API de ces rôles ; le rôle principal
    // reste celui affiché et utilisé pour le circuit de départ des demandes de congé.
    private List<String> droitsSupplementaires;

    // Identité + informations personnel/RH, portées par Personnel (embarqué, pas de @DBRef —
    // même convention qu'acteur). Permet à un chauffeur externe sans compte d'exister comme un
    // Personnel autonome (voir com.officedubac.project.personnel.PersonnelRepository).
    private Personnel personnel;

    // Fiche Personnel d'origine de ce compte (une personne = un seul compte)
    private String personnelId;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new java.util.ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(profil.getName().name()));
        if (droitsSupplementaires != null) {
            droitsSupplementaires.stream().distinct()
                    .filter(d -> !d.equals(profil.getName().name()))
                    .forEach(d -> authorities.add(new SimpleGrantedAuthority(d)));
        }
        return authorities;
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
