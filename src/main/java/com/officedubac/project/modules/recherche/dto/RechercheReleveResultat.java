package com.officedubac.project.modules.recherche.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Un candidat trouvé dans une des séries de bottins, à partir d'un ou
 * plusieurs critères (N° de table, année, nom, prénom, date de naissance,
 * lieu de naissance).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RechercheReleveResultat {

    private String serieKey;
    private String id;
    private String numeroTable;
    private String nomPrenom;
    private LocalDate dateNaissance;
    private String lieuNaissance;
    private Integer annee;
}
