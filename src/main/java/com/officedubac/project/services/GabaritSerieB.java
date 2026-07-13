package com.officedubac.project.services;

import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Relevé de notes — Série B : Sciences Economiques et Sociales.
 * 1er total : 500 points (25 coefficients).
 */
@Service
public class GabaritSerieB extends GabaritBase {

    public byte[] genererReleveB() {
        List<Section> sections = List.of(
                section("Français",
                        ep("Ecrit", 3),
                        ep("Orale", 1)),
                section("Ecrit",
                        ep("Philosophie", 3),
                        ep("Histo. et Géo.", 3),
                        ep("Sciences Eco. et Soc.", 5),
                        ep("Matématiques", 5),
                        ep("L.V. I", 3)),
                section("Oral",
                        ep("L.V. II", 2))
        );
        return genererReleveModerne("Série : Sciences Economiques et Sociales",
                "SERIE : B", "19", sections);
    }
}
