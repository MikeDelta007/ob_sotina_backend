package com.officedubac.project.services;

import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Relevé de notes — Série G : Techniques Quantitatives de Gestion et Economie.
 * 1er total : 620 points (31 coefficients).
 */
@Service
public class GabaritSerieG extends GabaritBase {

    public byte[] genererReleveG() {
        List<Section> sections = List.of(
                section("Ecrit",
                        ep("Techniques d'expression et de Communication", 3),
                        ep("Anglais", 3),
                        ep("Philosophie", 2),
                        ep("Mathématiques", 5),
                        ep("Economie Générale", 6),
                        ep("Etude de cas", 6),
                        ep("Connais du monde", 2)),
                section("Pratique",
                        ep("Correspondance Commerciale", 2),
                        ep("Traitement inf. des Gestions", 2))
        );
        return genererReleveModerne("Série : Techniques Quantitatives de Gestion et Economie",
                "SERIE : G", "19", sections);
    }
}
