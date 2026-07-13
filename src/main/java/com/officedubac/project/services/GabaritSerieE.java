package com.officedubac.project.services;

import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Relevé de notes — Série E.
 * 1er total : 640 points (32 coefficients).
 */
@Service
public class GabaritSerieE extends GabaritBase {

    public byte[] genererReleveE() {
        List<Section> sections = List.of(
                section("Français",
                        ep("Epr. écrite", 2),
                        ep("Epr. orale", 1)),
                section("E",
                        ep("Philosophie", 2),
                        ep("Mathématiques", 7),
                        ep("Sciences Physiques", 7),
                        ep("Constr. Mécanique", 6),
                        ep("An. de Fabr. Techno et A", 2)),
                section("O",
                        ep("Technique Pratique", 3),
                        ep("L.V", 2))
        );
        return genererReleveModerne("Série : E", "Série : E", "19", sections);
    }
}
