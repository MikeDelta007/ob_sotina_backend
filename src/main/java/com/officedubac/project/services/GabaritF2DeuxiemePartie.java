package com.officedubac.project.services;

import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Relevé de notes — Série F2, 2ème partie, deuxième session (petit format).
 * Ecrit : 19 coeff / 380 ; Oral : 14 coeff / 280 + EPS ; Total général : /660.
 */
@Service
public class GabaritF2DeuxiemePartie extends GabaritBase {

    public byte[] genererReleveF2DeuxiemePartie() {
        List<Epreuve> ecrites = List.of(
                ep("Maths", 4),
                ep("Mécan. Métallu.", 2),
                ep("El-tech. Electro.", 3),
                ep("Dessin Schéma", 6),
                ep("Etude d'Equip.", 4)
        );
        List<Epreuve> orales = List.of(
                ep("Autom.", 2),
                ep("Techno", 2),
                ep("L.V.", 2),
                ep("Mesures Essais", 4),
                ep("Epreu. d'atelier EPS", 4)
        );
        return genererDeuxiemePartie("Série F 2", "2me PARTIE", "DEUXIEME SESSION",
                ecrites, orales, false, "ECRIT + ORAL + EPS", "ADMIS - AJOURNE");
    }
}
