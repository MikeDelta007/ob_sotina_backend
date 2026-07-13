package com.officedubac.project.services;

import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Relevé de notes — Série L'1 : Langues et Civilisations Modernes.
 * 1er total : 480 points (24 coefficients).
 */
@Service
public class GabaritL1 extends GabaritBase {

    public byte[] genererReleveL1() {
        List<Section> sections = List.of(
                section("Ecrit",
                        ep("Français", 6),
                        ep("Philosophie", 4),
                        ep("Hist. et Géo.", 2),
                        ep("L.V. I", 4),
                        ep("Mathématiques", 2),
                        ep("L.V. II", 4)),
                section("Oral",
                        ep("L.V. I", 2))
        );
        return genererReleveModerne("Série : Langues et Civilisations Modernes",
                "L'1", "20", sections);
    }
}
