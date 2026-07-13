package com.officedubac.project.services;

import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Relevé de notes — Série F7.
 * 1er total : 660 points (33 coefficients).
 */
@Service
public class GabaritSerieF7 extends GabaritBase {

    public byte[] genererReleveF7() {
        List<Section> sections = List.of(
                section("Français",
                        ep("Epr. écrite", 2),
                        ep("Epr. orale", 1)),
                section("E",
                        ep("Mathématiques", 2),
                        ep("Biologie", 4),
                        ep("Biochimie", 4),
                        ep("Microbiologie et Imm.", 5),
                        ep("Physiologie", 3)),
                section("O",
                        ep("Langue Vivante", 2),
                        ep("TP de Biologie", 6),
                        ep("TP de Biochimie", 4))
        );
        return genererReleveModerne("Série : F7", "Série : F7", "19", sections);
    }
}
