package com.officedubac.project.modules.recherche;

import com.officedubac.project.modules.recherche.dto.RechercheReleveResultat;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

/**
 * Point d'entrée du module Bottins côté client : retrouver un ou plusieurs
 * candidats à travers les 29 séries, à partir de n'importe quelle
 * combinaison de critères (N° de table, année, nom, prénom, date de
 * naissance, lieu de naissance), sans que l'utilisateur ait besoin de
 * connaître sa série à l'avance.
 */
@RestController
@RequestMapping("/api/v1/releves")
public class RechercheReleveController {

    private final RechercheReleveService service;

    public RechercheReleveController(RechercheReleveService service) {
        this.service = service;
    }

    @GetMapping("/rechercher")
    public List<RechercheReleveResultat> rechercher(
            @RequestParam(required = false) String numeroTable,
            @RequestParam(required = false) Integer annee,
            @RequestParam(required = false) String nom,
            @RequestParam(required = false) String prenom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateNaissance,
            @RequestParam(required = false) String lieuNaissance
    ) {
        return service.rechercher(numeroTable, annee, nom, prenom, dateNaissance, lieuNaissance);
    }
}
