package com.officedubac.project.services;

import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Relevé de notes — Série G2 : Techniques Quantitatives de Gestion.
 * 1er total : 500 points (25 coefficients).
 */
@Service
public class GabaritSerieG2 extends GabaritBase {

    public byte[] genererReleveG2() {
        List<Section> sections = List.of(
                section("Français",
                        ep("Ecrit", 2),
                        ep("Orale", 1)),
                section("Ecrit",
                        ep("Philosophie", 2),
                        ep("Mathématiques", 4),
                        ep("Economie", 2),
                        ep("Etudes de Cas", 6),
                        ep("Connais. du monde", 2)),
                section("Oral",
                        ep("Correspon. dactylo.", 2),
                        ep("L.V. I", 2),
                        ep("L.V. II", 2))
        );
        return genererReleveModerne("Série : Techniques Quantitatives de Gestion",
                "SERIE : G2", "19", sections);
    }
}
