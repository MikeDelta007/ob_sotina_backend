package com.officedubac.project.services;

import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Relevé de notes — Série G2, 1ère partie, première session (petit format).
 * Ecrit : 15 coeff / 300 ; Oral : 6 coeff / 120 ; Total général : /420.
 */
@Service
public class GabaritG2PremierePartie extends GabaritBase {

    public byte[] genererReleveG2PremierePartie() {
        List<Epreuve> ecrites = List.of(
                ep("Franç.", 3),
                ep("Econo. Génér.", 2),
                ep("Maths", 2),
                ep("L.V. 1", 2),
                ep("Etude de cas", 6)
        );
        List<Epreuve> orales = List.of(
                ep("Franç.", 2),
                ep("Connai. Monde", 2),
                ep("L.V. 2", 2)
        );
        return genererDeuxiemePartie("Série G 2", "1re PARTIE", "PREMIERE SESSION",
                ecrites, orales, false, "ECRIT + ORAL", "ADMIS - AJOURNE - 2me SESSION");
    }
}
