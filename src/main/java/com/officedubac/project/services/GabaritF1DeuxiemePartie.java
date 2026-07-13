package com.officedubac.project.services;

import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Relevé de notes — Série F1, 2ème partie, première session (petit format).
 * Ecrit : 21 coeff / 420 ; Oral : 12 coeff / 240.
 */
@Service
public class GabaritF1DeuxiemePartie extends GabaritBase {

    public byte[] genererReleveF1DeuxiemePartie() {
        List<Epreuve> ecrites = List.of(
                ep("Maths", 4),
                ep("Electric. Métallu.", 2),
                ep("Mécan.", 3),
                ep("Etude ou Proj.", 6),
                ep("A. Fabr. E. d'out.", 6)
        );
        List<Epreuve> orales = List.of(
                ep("Autom.", 2),
                ep("Techno.", 2),
                ep("L.V.", 2),
                ep("Epreu. d'atelier", 6)
        );
        return genererDeuxiemePartie("Série F 1", "2me PARTIE", "PREMIERE SESSION",
                ecrites, orales, false, null, "ADMIS - AJOURNE - 2me SESSION");
    }
}
