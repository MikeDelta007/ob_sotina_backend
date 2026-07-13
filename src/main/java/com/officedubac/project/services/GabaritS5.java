package com.officedubac.project.services;

import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Relevé de notes — Série S5 : Sciences et Technologie du Produit Agro-Alimentaire.
 * 1er total : 680 points (34 coefficients).
 */
@Service
public class GabaritS5 extends GabaritBase {

    public byte[] genererReleveS5() {
        List<Section> sections = List.of(
                section("Ecrit",
                        ep("Philosophie", 2),
                        ep("Mathématiques", 5),
                        ep("Sc. Physiques", 5),
                        ep("Sc. Vie et Terre", 5),
                        ep("Français", 3),
                        ep("Histoire - Géographie", 2),
                        ep("Anglais", 2),
                        ep("Tech. Transf. Conserv.", 6),
                        ep("Microbiologie", 2)),
                section("Pratique",
                        ep("Biochimie", 2))
        );
        return genererReleveModerne("Série : Sciences et Technologie, du Produit Agro-Alimentaire",
                "S 5", "20", sections);
    }
}
