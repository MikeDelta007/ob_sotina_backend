package com.officedubac.project.services;

import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Relevé de notes — Série G1.
 * 1er total : 440 points (22 coefficients).
 */
@Service
public class GabaritSerieG1 extends GabaritBase {

    public byte[] genererReleveG1() {
        List<Section> sections = List.of(
                section("Français",
                        ep("Epr. écrite", 2),
                        ep("Epr. orale", 1)),
                section("E",
                        ep("Philosophie", 2),
                        ep("Economie", 2),
                        ep("L.V. I", 3),
                        ep("Etude de Cas", 6),
                        ep("Connaissance du monde", 2),
                        ep("Organisation adm.", 2)),
                section("O",
                        ep("L.V. II", 2))
        );
        return genererReleveModerne("Série : G1", "Série : G1", "19", sections);
    }
}
