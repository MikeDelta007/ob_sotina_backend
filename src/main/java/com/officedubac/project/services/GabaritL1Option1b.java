package com.officedubac.project.services;

import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Relevé de notes — Série L1 Option 1b : Langues et Civilisations Anciennes.
 * 1er total : 520 points (26 coefficients).
 */
@Service
public class GabaritL1Option1b extends GabaritBase {

    public byte[] genererReleveL1Option1b() {
        List<Section> sections = List.of(
                section("Ecrit",
                        ep("Français", 6),
                        ep("Philosophie", 6),
                        ep("Hist. et Géo.", 2),
                        ep("L.V. I", 3),
                        ep("Mathématiques", 2),
                        ep("L.V. II", 2),
                        ep("Latin ou Arabe Classique", 5))
        );
        return genererReleveModerne("Série : Langues et Civilisations Anciennes",
                "L1 Option 1b", "19", sections);
    }
}
