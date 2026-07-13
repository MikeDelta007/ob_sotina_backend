package com.officedubac.project.services;

import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Relevé de notes — Série D (ancien format type certificat).
 * 1er total : 420 points (21 coefficients) ; total définitif : 420.
 */
@Service
public class GabaritDCertificat extends GabaritBase {

    public byte[] genererReleveDCertificat() {
        List<Section> sections = List.of(
                section("Français",
                        ep("écrit", 2),
                        ep("oral", 1)),
                section("Ecrit",
                        ep("Philosophie", 2),
                        ep("Mathématiques", 4),
                        ep("Sciences Phys.", 4),
                        ep("Sciences Nat.", 4),
                        ep("Hist. et Géo.", 2)),
                section("Oral",
                        ep("L.V.", 2))
        );
        return genererReleveAncien("SERIE D", sections, List.of(), 420,
                "ASSEZ BIEN et AU-DESSUS");
    }
}
