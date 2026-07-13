package com.officedubac.project.services;

import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Relevé de notes — Série A4 (ancien format type certificat).
 * 1er total : 340 points (17 coefficients) ; total définitif : 400
 * (report 340 + 2e Langue vivante coeff 3 = 60).
 */
@Service
public class GabaritA4 extends GabaritBase {

    public byte[] genererReleveA4() {
        List<Section> sections = List.of(
                section("Français",
                        ep("écrit", 3),
                        ep("oral", 1)),
                section("Ecrit",
                        ep("Philosophie", 4),
                        ep("1° L.V.", 3),
                        ep("Hist. et Géo.", 3)),
                section("Oral",
                        ep("Mathématiques", 3))
        );
        List<Epreuve> groupe2 = List.of(
                ep("2° Langue vivante", 3)
        );
        return genererReleveAncien("SERIE A4", sections, groupe2, 400,
                "ASSEZ BIEN et AU-DESSUS");
    }
}
