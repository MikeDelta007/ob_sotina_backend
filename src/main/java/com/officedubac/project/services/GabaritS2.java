package com.officedubac.project.services;

import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Relevé de notes — Série S2 : Sciences Expérimentales.
 * 1er total : 520 points (26 coefficients).
 */
@Service
public class GabaritS2 extends GabaritBase {

    public byte[] genererReleveS2() {
        List<Section> sections = List.of(
                section("Ecrit",
                        ep("Français", 3),
                        ep("Philosophie", 2),
                        ep("Mathématiques", 5),
                        ep("Sciences Physiques", 6),
                        ep("Sciences Naturelles", 6),
                        ep("Hist. et Géo.", 2),
                        ep("Anglais", 2))
        );
        return genererReleveModerne("Série : Sciences Expérimentales",
                "SERIE : S2", "20", sections);
    }
}
