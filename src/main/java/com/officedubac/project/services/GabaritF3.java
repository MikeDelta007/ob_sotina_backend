package com.officedubac.project.services;

import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Relevé des notes — Série F3 : Mécanique Automobile (Baccalauréat de Technicien).
 * 1er total : 460 points (23 coefficients) ; total définitif : 640
 * (report 460 + Electricité 2, Métallurgie 1, Etude ou Projet 4, Technologie 2).
 */
@Service
public class GabaritF3 extends GabaritBase {

    public byte[] genererReleveF3() {
        List<Section> sections = List.of(
                section("Français",
                        ep("Epr. écrite", 2),
                        ep("Epr. orale", 2)),
                section("",
                        ep("Mathématiques", 5),
                        ep("Anglais (oral)", 2),
                        ep("Mécanique appliquée", 4),
                        ep("Notice technique", 3),
                        ep("Epreuve pratique d'at.", 5))
        );
        List<Epreuve> groupe2 = List.of(
                ep("Sc. Phys. : Electricité", 2),
                ep("Sc. Phys. : Métallurgie", 1),
                ep("ETUDE ou PROJET", 4),
                ep("Technologie", 2)
        );
        return genererReleveAncien("SERIE : F3 MECANIQUE AUTOMOBILE", sections, groupe2, 640,
                "B. ou T.B.");
    }
}
