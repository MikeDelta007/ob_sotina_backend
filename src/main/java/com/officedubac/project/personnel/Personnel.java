package com.officedubac.project.personnel;

import com.officedubac.project.models.Civilite;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

// Identité + informations RH d'une personne, indépendamment de tout compte applicatif.
// Un User interne embarque son propre Personnel (copie complète, pas de @DBRef, comme Acteurs
// l'est déjà sur User). Un chauffeur externe (sans compte) est un Personnel autonome, persisté
// dans la collection "personnels" via PersonnelRepository, jamais rattaché à un User.
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "personnels")
public class Personnel {

    @Id
    private String id;

    private String firstname;
    private String lastname;
    private String phone;
    private String email;

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
    // Jours d'AUTORISATION (absence ponctuelle) pris depuis le dernier congé validé (ou la
    // dernière remise à zéro annuelle) : pas décomptés immédiatement du solde, mais déduits
    // automatiquement à la validation du prochain congé.
    private Integer joursAutorisationCumules;

    private boolean actif = true;

    // Solde réellement disponible pour un nouveau congé (solde nominal moins les jours
    // d'autorisation déjà pris et pas encore régularisés). Calculé, non persisté.
    public Integer getSoldeDisponible() {
        if (soldeConges == null) return null;
        int cumul = joursAutorisationCumules != null ? joursAutorisationCumules : 0;
        return Math.max(0, soldeConges - cumul);
    }
}
