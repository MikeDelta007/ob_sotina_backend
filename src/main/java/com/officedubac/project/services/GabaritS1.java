package com.officedubac.project.services;

import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Relevé de notes — Série S1 : Mathématiques et Sciences Physiques.
 * 1er total : 540 points (27 coefficients).
 */
@Service
public class GabaritS1 extends GabaritBase {

    public byte[] genererReleveS1() {
        List<Section> sections = List.of(
                section("Ecrit",
                        ep("Français", 3),
                        ep("Philosophie", 2),
                        ep("Mathématiques", 8),
                        ep("Sciences Physiques", 8),
                        ep("Hist. et Géo.", 2),
                        ep("Sciences Naturelles", 2),
                        ep("Anglais", 2))
        );
        return genererReleveModerne("Série : Mathématiques et Sciences Physiques",
                "SERIE : S1", "20", sections);
    }
}
