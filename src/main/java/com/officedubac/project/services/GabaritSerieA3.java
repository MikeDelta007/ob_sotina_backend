package com.officedubac.project.services;

import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Relevé de notes — Série A3 : Lettres - Sciences Humaines (format moderne).
 * 1er total : 480 points (24 coefficients).
 */
@Service
public class GabaritSerieA3 extends GabaritBase {

    public byte[] genererReleveA3() {
        List<Section> sections = List.of(
                section("Français",
                        ep("Ecrit", 3),
                        ep("Orale", 2)),
                section("Ecrit",
                        ep("Philosophie", 4),
                        ep("Histo. et Géo.", 4),
                        ep("L.V. I", 3),
                        ep("Matématiques", 3),
                        ep("L.V. II", 3)),
                section("Oral",
                        ep("L.V. I", 2))
        );
        return genererReleveModerne("Série : Lettres - Sciences Humaines",
                "SERIE : A3", "19", sections);
    }
}
