package com.officedubac.project.services;

import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Certificat Procès-Verbal d'Examen — Option F6 : Technique industrielle, option Chimie
 * (Baccalauréat de Technicien).
 * 1er total : 660 points (33 coefficients) ; total définitif : 660.
 */
@Service
public class GabaritPVF6 extends GabaritCertificatPV {

    public byte[] genererCertificatF6() {
        List<Section> sections = List.of(
                section("Français",
                        ep("Ecrit", 2),
                        ep("Oral", 1)),
                section("Ecrit",
                        ep("Mathématiques", 4),
                        ep("Physique", 4),
                        ep("Chimie", 5),
                        ep("Technique de labo. chi.", 3),
                        ep("Technologie", 2)),
                section("Oral",
                        ep("Anglais", 2),
                        ep("TP de Chimie", 4),
                        ep("TP de Physique", 3),
                        ep("Montage d'appareils", 3))
        );
        return genererCertificat(
                "BACCALAUREAT DE TECHNICIEN",
                "Nous, Membres du Jury d'Examen de la Série F6, Vu le Décret 87-914 du "
                        + "11 juillet 1987 et attendu que le candidat désigné ci-dessous a obtenu "
                        + "les notes portées sur le présent Certificat Procès-Verbal,",
                "Technique industrielle : option Chimie",
                "OPTION F6",
                sections, List.of(), 660,
                "ASSEZ BIEN et AU-DESSUS",
                true);
    }
}
