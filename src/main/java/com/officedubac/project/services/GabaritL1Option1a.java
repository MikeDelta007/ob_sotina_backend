package com.officedubac.project.services;

import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Relevé de notes — Série L1 Option 1a : Langues et Civilisations Anciennes.
 * 1er total : 560 points (28 coefficients).
 */
@Service
public class GabaritL1Option1a extends GabaritBase {

    public byte[] genererReleveL1Option1a() {
        List<Section> sections = List.of(
                section("Ecrit",
                        ep("Français", 6),
                        ep("Philosophie", 6),
                        ep("Hist. et Géo.", 2),
                        ep("L.V. I", 2),
                        ep("Mathématiques", 2),
                        ep("Grec", 5),
                        ep("Latin ou Arabe Classique", 5))
        );
        return genererReleveModerne("Série : Langues et Civilisations Anciennes",
                "L1 Option 1a", "19", sections);
    }
}
