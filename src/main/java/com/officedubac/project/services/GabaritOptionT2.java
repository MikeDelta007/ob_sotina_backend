package com.officedubac.project.services;

import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Relevé de notes — Option T2 : Electrotechnique / Electronique.
 * 1er total : 640 points (32 coefficients).
 */
@Service
public class GabaritOptionT2 extends GabaritBase {

    public byte[] genererReleveT2() {
        List<Section> sections = List.of(
                section("Ecrit",
                        ep("Tech. d'expression et de Communicat°", 3),
                        ep("Mathématiques", 4),
                        ep("Electro Technique / Electronique", 6),
                        ep("Construct° Electromécanique", 3),
                        ep("Schéma - Automatique - Informatique", 4),
                        ep("Analyse des systèmes électriques", 2),
                        ep("Sciences Physiques", 2),
                        ep("Anglais", 2)),
                section("Pratique",
                        ep("Construction Electrique", 3),
                        ep("Essais et mesures", 3))
        );
        return genererReleveModerne("Série : Electrotechnique / Electronique",
                "Option T2", "19", sections);
    }
}
