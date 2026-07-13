package com.officedubac.project.services;

import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Relevé de notes — Série A3, 2ème partie, première session (petit format).
 * Ecrit : 12 coeff / 240 ; Oral : 4 coeff / 80.
 */
@Service
public class GabaritA3DeuxiemePartie extends GabaritBase {

    public byte[] genererReleveA3DeuxiemePartie() {
        List<Epreuve> ecrites = List.of(
                ep("Philo.", 4),
                ep("L.V. 1", 3),
                ep("H. et G.", 3),
                ep("L.V. 2", 2)
        );
        List<Epreuve> orales = List.of(
                ep("L.V. 1", 2),
                ep("Maths", 2)
        );
        return genererDeuxiemePartie("Série A 3", "2me PARTIE", "PREMIERE SESSION",
                ecrites, orales, false, null, "ADMIS - AJOURNE - 2me SESSION");
    }
}
