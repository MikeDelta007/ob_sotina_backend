package com.officedubac.project.services;

import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Relevé de notes — Série A3 (ancien format machine à écrire).
 * 1er total : 480 points (24 coefficients).
 */
@Service
public class GabaritSerieA3Ancien extends GabaritBase {

    public byte[] genererReleveA3Ancien() {
        List<Section> sections = List.of(
                section("Français",
                        ep("Epr. écrite", 3),
                        ep("Epr. orale", 2)),
                section("E",
                        ep("Philosophie", 4),
                        ep("Histoire et Géo.", 4),
                        ep("L.V. I", 3),
                        ep("Mathématiques", 3),
                        ep("L.V. II", 3)),
                section("O",
                        ep("L.V. I", 2))
        );
        return genererReleveModerne("Série : A3", "Série : A3", "19", sections);
    }
}
