package com.officedubac.project.services;

import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Relevé de notes — Option T1 : Fabrication Mécanique.
 * 1er total : 680 points (34 coefficients).
 */
@Service
public class GabaritOptionT1 extends GabaritBase {

    public byte[] genererReleveT1() {
        List<Section> sections = List.of(
                section("Ecrit",
                        ep("Tech. d'expression et de Communicat°", 3),
                        ep("Mathématiques", 5),
                        ep("Mécanique", 4),
                        ep("Construct° Mécanique", 4),
                        ep("Analyse de Fabric / Outillage", 6),
                        ep("Electricité", 1),
                        ep("Métallurgie", 1),
                        ep("Sciences Physiques", 2),
                        ep("Anglais", 2),
                        ep("Technologie et Automatisme", 2)),
                section("Pratique",
                        ep("Epreuve pratique d'Atelier", 4))
        );
        return genererReleveModerne("Série : Fabrication Mécanique",
                "Option T1", "20", sections);
    }
}
