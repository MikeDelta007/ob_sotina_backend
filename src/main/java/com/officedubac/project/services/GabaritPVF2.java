package com.officedubac.project.services;

import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Certificat Procès-Verbal d'Examen — Option F2 : Electronique - Electrotechnique
 * (Baccalauréat de Technicien).
 * 1er total : 580 points (29 coefficients) ; total définitif : 580.
 */
@Service
public class GabaritPVF2 extends GabaritCertificatPV {

    public byte[] genererCertificatF2() {
        List<Section> sections = List.of(
                section("Français",
                        ep("Ecrit", 2),
                        ep("Oral", 1)),
                section("Ecrit",
                        ep("Mathématiques", 4),
                        ep("Electrotechni. - Electr.", 4),
                        ep("Constr. Elec et Méca.", 4),
                        ep("Shéma et Automatis.", 4),
                        ep("Etude d'Equipement", 2)),
                section("Oral",
                        ep("Anglais", 2),
                        ep("Cablage et Techno.", 3),
                        ep("Essais et Mesures", 3))
        );
        return genererCertificat(
                "BACCALAUREAT DE TECHNICIEN",
                "Nous, Membres du Jury d'Examen de la série F2, Vu le Décret 87-914 du "
                        + "11 juillet 1987 et attendu que le candidat désigné ci-dessous a obtenu "
                        + "les notes portées sur le présent Certificat Procès-Verbal,",
                "Electronique - Electrotechnique",
                "OPTION F2",
                sections, List.of(), 580,
                "ASSEZ BIEN et AU-DESSUS",
                true);
    }
}
