package com.officedubac.project.services;

import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Relevé de notes — Série L2 : Sciences Sociales - Sciences Humaines.
 * 1er total : 540 points (27 coefficients).
 */
@Service
public class GabaritL2 extends GabaritBase {

    public byte[] genererReleveL2() {
        List<Section> sections = List.of(
                section("",
                        ep("Français", 5),
                        ep("Philosophie", 6),
                        ep("Hist. et Géo.", 6),
                        ep("Mathématiques", 2),
                        ep("L.V. I", 4),
                        ep("L.V. II ou Economie (1)", 2),
                        ep("Sciences de la nature (1) (Sciences Phys. ou Sc. Nat.)", 2))
        );
        return genererReleveModerne("Série : Sciences Sociales - Sciences Humaines",
                "L 2", "20", sections);
    }
}
