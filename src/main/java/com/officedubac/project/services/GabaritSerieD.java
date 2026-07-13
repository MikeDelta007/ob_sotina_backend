package com.officedubac.project.services;

import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Relevé de notes — Série D.
 * 1er total : 460 points (23 coefficients).
 */
@Service
public class GabaritSerieD extends GabaritBase {

    public byte[] genererReleveD() {
        List<Section> sections = List.of(
                section("Français",
                        ep("Epr. écrite", 2),
                        ep("Epr. orale", 1)),
                section("E",
                        ep("Philosophie", 2),
                        ep("Mathématiques", 4),
                        ep("Sciences Physiques", 5),
                        ep("Sciences Naturelles", 5),
                        ep("Histoire et Géographie", 2)),
                section("O",
                        ep("L.V.", 2))
        );
        return genererReleveModerne("Série : D", "Série : D", "19", sections);
    }
}
