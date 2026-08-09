package com.officedubac.project.modules.A2.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Candidat
{
    private String nomPrenom;
    private LocalDate dateNaissance;
    private String lieuNaissance;
    private String etablissement; // Etab.
    private String indicatif;     // Ind.
    private String options;       // Options

    /** N° de table (nouveau champ, imprimé en haut du formulaire) */
    private String numeroTable;

    /** (N) — nationalité du candidat */
    private String nationalite;

    /** (F) — nombre de fois que le candidat se présente à l'examen */
    private String nombreDeFois;


}
