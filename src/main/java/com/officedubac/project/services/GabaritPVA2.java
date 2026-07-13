package com.officedubac.project.services;

import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Certificat Procès-Verbal d'Examen — Option A2 : Philosophie Lettres.
 * 1er total : 320 points (16 coefficients) ; total définitif : 400
 * (report 320 + Langue vivante 2 et Mathématiques 2).
 */
@Service
public class GabaritPVA2 extends GabaritCertificatPV {

    public byte[] genererCertificatA2() {
        List<Section> sections = List.of(
                section("Français",
                        ep("écrit", 3),
                        ep("oral", 1)),
                section("Ecrit",
                        ep("Philosophie", 4),
                        ep("Latin L.V.", 3)),
                section("Oral",
                        ep("Hist. et Géo.", 3),
                        ep("L.V.", 2))
        );
        List<Epreuve> groupe2 = List.of(
                ep("Langue vivante", 2),
                ep("Mathématiques", 2)
        );
        return genererCertificat(
                "BACCALAUREAT DE L'ENSEIGNEMENT SECONDAIRE",
                "Nous, Membres du Jury d'Examen de la Série A2, Vu les Décret et Arrêté du "
                        + "5 Décembre 1969 modifiés, et attendu que le candidat désigné dans le "
                        + "cadre ci-contre a obtenu les notes portées sur le présent Certificat - "
                        + "Procès-Verbal,",
                "Série : PHILOSOPHIE LETTRES",
                "OPTION A2",
                sections, groupe2, 400,
                "BIEN ou TRES BIEN",
                false);
    }
}
