package com.officedubac.project.services;

import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Relevé de notes — Série A2, 2ème partie, deuxième session (petit format).
 * Ecrit : 11 coeff / 220 ; Oral : 6 coeff / 120 ; Total général : /340.
 */
@Service
public class GabaritA2DeuxiemePartie extends GabaritBase {

    public byte[] genererReleveA2DeuxiemePartie() {
        List<Epreuve> ecrites = List.of(
                ep("Philo.", 5),
                ep("L.V. 1", 2),
                ep("H. et G", 2),
                ep("Lat.-Ar.", 2)
        );
        List<Epreuve> orales = List.of(
                ep("L.V. 1", 2),
                ep("L.V. 2", 2),
                ep("Maths", 2)
        );
        return genererDeuxiemePartie("Série A 2", "2me PARTIE", "DEUXIEME SESSION",
                ecrites, orales, false, "ECRIT + ORAL", "ADMIS - AJOURNE");
    }
}
