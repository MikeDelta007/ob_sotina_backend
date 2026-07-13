package com.officedubac.project.services;

import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Relevé de notes — Série S4 : Sciences et Technologie de l'Agriculture
 * et de l'Environnement.
 * 1er total : 680 points (34 coefficients).
 */
@Service
public class GabaritS4 extends GabaritBase {

    public byte[] genererReleveS4() {
        List<Section> sections = List.of(
                section("Ecrit",
                        ep("Philosophie", 2),
                        ep("Mathématiques", 5),
                        ep("Sc. Physiques", 5),
                        ep("Sc. Vie et Terre", 5),
                        ep("Français", 3),
                        ep("Histoire - Géographie", 2),
                        ep("Anglais", 2),
                        ep("Ecologie / Environnement", 2),
                        ep("Zootechnique", 2)),
                section("Pratique",
                        ep("Phytotechnique", 6))
        );
        return genererReleveModerne("Série : Sciences et Technologie, de l'Agriculture et de l'environnement",
                "S 4", "20", sections);
    }
}
