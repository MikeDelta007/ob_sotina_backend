package com.officedubac.project.services;

import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Relevé de notes — Série D, 2ème partie, première session (petit format).
 * Ecrit : 14 coeff / 280 ; Oral : 6 coeff / 120 + EPS ; Total général : /400.
 */
@Service
public class GabaritD2emePartie extends GabaritBase {

    public byte[] genererReleveD2emePartie() {
        List<Epreuve> ecrites = List.of(
                ep("Philo.", 2),
                ep("Maths", 4),
                ep("Sc. Phy.", 4),
                ep("Sc. Nat.", 4)
        );
        List<Epreuve> orales = List.of(
                ep("L.V. 1", 2),
                ep("H. et G.", 2),
                ep("Sc. Phy. / Sc. Nat.", 2)
        );
        return genererDeuxiemePartie("Série D", "2me PARTIE", "PREMIERE SESSION",
                ecrites, orales, true, "ECRIT + ORAL + EPS", "ADMIS - AJOURNE");
    }
}
