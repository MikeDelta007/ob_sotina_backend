package com.officedubac.project.services;

import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Relevé de notes — Série S3 : Sciences et Techniques.
 * 1er total : 720 points (36 coefficients).
 */
@Service
public class GabaritS3 extends GabaritBase {

    public byte[] genererReleveS3() {
        List<Section> sections = List.of(
                section("Ecrit",
                        ep("Français", 3),
                        ep("Philosophie", 2),
                        ep("Mathématiques", 8),
                        ep("Sciences Physiques", 8),
                        ep("Construction Mécanique", 8),
                        ep("Analyse de fabrication Techno - Automatisme", 2),
                        ep("Anglais", 2)),
                section("Pratique",
                        ep("Epreuve Pratique Atelier", 3))
        );
        return genererReleveModerne("Série : Sciences et Techniques",
                "SERIE : S3", "19", sections);
    }
}
