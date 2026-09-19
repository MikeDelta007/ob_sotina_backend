package com.officedubac.project.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Optional;
import java.util.Set;

@Document(collection = "regle_matiere")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegleMatiere
{
    @Id
    private String id;
    private String code;
    private Set<String> series;
    private String type;
    private String champ;
    private String valeur;
    private String groupe;
    private String date1;
    private String heure1;
    private String date2;
    private String heure2;

    /**
     * Une épreuve est retenue pour un groupe si l'une de ces deux informations l'indique :
     * - le champ groupe : 1ERGRP = 1er groupe exclusivement, 2NDGRP = 2nd groupe
     *   exclusivement, 1ER2NDGRP = les deux. Une valeur vide ou inconnue est traitée comme
     *   du 1er groupe, comme dans le calcul des effectifs (TirageJuryMatService) ;
     * - la programmation : date1 / heure1 = 1er groupe, date2 / heure2 = 2nd groupe.
     * Prendre l'une OU l'autre évite d'omettre une épreuve dont une des deux informations
     * est incomplète. Les effectifs nuls du groupe écartent ensuite les faux positifs.
     */
    public boolean concerneGroupe(String groupe) {
        boolean pourLeSecond = "2ND".equalsIgnoreCase(groupe);

        String groupeRegle = Optional.ofNullable(this.groupe)
                .map(String::trim)
                .map(String::toUpperCase)
                .orElse("");

        boolean parChamp;
        if ("1ER2NDGRP".equals(groupeRegle)) {
            parChamp = true;
        } else if ("2NDGRP".equals(groupeRegle)) {
            parChamp = pourLeSecond;
        } else {
            parChamp = !pourLeSecond;
        }

        boolean parProgrammation = pourLeSecond
                ? (!estVide(date2) || !estVide(heure2))
                : (!estVide(date1) || !estVide(heure1));

        return parChamp || parProgrammation;
    }

    private static boolean estVide(String valeur) {
        return valeur == null || valeur.isBlank();
    }

    /** Intitulé de la matière : la valeur de la règle, à défaut son code. */
    public String libelle() {
        return Optional.ofNullable(valeur)
                .filter(v -> !v.isBlank())
                .orElse(code == null ? "" : code.trim());
    }
}