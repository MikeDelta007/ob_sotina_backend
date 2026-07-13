package com.officedubac.project.services;

import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Relevé de notes — Série F1.
 * 1er total : 580 points (29 coefficients).
 */
@Service
public class GabaritSerieF1 extends GabaritBase {

    public byte[] genererReleveF1() {
        List<Section> sections = List.of(
                section("Français",
                        ep("Epr. écrite", 2),
                        ep("Epr. orale", 1)),
                section("E",
                        ep("Mathématiques", 4),
                        ep("Mécanique", 4),
                        ep("Construction méca.", 4),
                        ep("Analyse fabrication", 4),
                        ep("Electricité - Métal", 2)),
                section("O",
                        ep("Technologie - Autom.", 2),
                        ep("Anglais", 2),
                        ep("Epreuve pratique", 4))
        );
        return genererReleveModerne("Série : F1", "Série : F1", "19", sections);
    }
}
