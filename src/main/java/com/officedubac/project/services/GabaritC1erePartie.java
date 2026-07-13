package com.officedubac.project.services;

import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Relevé de notes — Série C, 1ère partie, première session (petit format).
 * Ecrit : 17 coeff / 340 ; Oral : 9 coeff / 180 ; Total général : /520.
 */
@Service
public class GabaritC1erePartie extends GabaritBase {

    public byte[] genererReleveC1erePartie() {
        List<Epreuve> ecrites = List.of(
                ep("Franç.", 4),
                ep("Maths", 5),
                ep("Sc. Phy.", 5),
                ep("L.V. 1", 3)
        );
        List<Epreuve> orales = List.of(
                ep("Franç.", 2),
                ep("Maths", 2),
                ep("Sc. Nat.", 3),
                ep("H. et G.", 2)
        );
        return genererDeuxiemePartie("Série C", "1re PARTIE", "PREMIERE SESSION",
                ecrites, orales, false, "ECRIT + ORAL", "ADMIS - AJOURNE - 2me SESSION");
    }
}
