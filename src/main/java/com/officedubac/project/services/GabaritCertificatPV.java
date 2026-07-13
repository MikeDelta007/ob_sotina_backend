package com.officedubac.project.services;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.util.List;

/**
 * Classe intermédiaire pour les CERTIFICATS PROCES-VERBAL D'EXAMEN
 * (options A2, F2, F6...).
 *
 * Le certificat reprend la structure des relevés anciens (1er / 2ème groupe)
 * avec un en-tête légal, un cadre candidat à 4 lignes et un pied de page
 * spécifique (déclaration du jury + consignes de remplissage).
 */
public abstract class GabaritCertificatPV extends GabaritBase {

    /**
     * @param titreBac       ex. "BACCALAUREAT DE L'ENSEIGNEMENT SECONDAIRE"
     *                       ou "BACCALAUREAT DE TECHNICIEN"
     * @param texteLegal     paragraphe légal (décret / arrêté) sous l'avertissement
     * @param serieTexte     ex. "Série : PHILOSOPHIE LETTRES"
     *                       ou "Technique industrielle : option Chimie"
     * @param badge          ex. "OPTION A2"
     * @param sections       sections du 1er groupe d'épreuves
     * @param lignesGroupe2  matières propres au 2ème groupe (peut être vide)
     * @param totalDefinitif total du 2ème groupe
     * @param texteMentions  mentions des points supplémentaires
     * @param technicien     true pour le pied de page F2/F6 (consignes a-e + TRES IMPORTANT),
     *                       false pour le pied de page A2
     */
    protected byte[] genererCertificat(String titreBac, String texteLegal, String serieTexte,
                                       String badge, List<Section> sections,
                                       List<Epreuve> lignesGroupe2, int totalDefinitif,
                                       String texteMentions, boolean technicien) {
        int total1 = sommeCoeffs(sections) * 20;
        Document document = new Document(PageSize.A4, 36, 36, 30, 30);

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter.getInstance(document, baos);
            document.open();

            document.add(buildEnteteCertificat(titreBac, texteLegal, serieTexte, badge));
            document.add(espace(3));
            document.add(buildCadreCandidatQuatreLignes());
            document.add(espace(3));

            PdfPTable corps = new PdfPTable(new float[]{50, 50});
            corps.setWidthPercentage(100);

            PdfPCell gauche = new PdfPCell(buildGroupe1Ancien(sections, total1, texteMentions));
            gauche.setPadding(0);
            gauche.setBorder(Rectangle.NO_BORDER);
            corps.addCell(gauche);

            PdfPCell droite = new PdfPCell(buildGroupe2Ancien(lignesGroupe2, total1, totalDefinitif));
            droite.setPadding(0);
            droite.setBorder(Rectangle.NO_BORDER);
            corps.addCell(droite);

            document.add(corps);
            document.add(espace(3));
            document.add(buildDeclarationJury());
            document.add(espace(3));
            document.add(technicien ? buildConsignesTechnicien() : buildConsignesA2());

            document.close();
            return baos.toByteArray();
        } catch (DocumentException | java.io.IOException e) {
            throw new RuntimeException("Erreur lors de la génération du PDF", e);
        }
    }

    private PdfPTable buildEnteteCertificat(String titreBac, String texteLegal,
                                            String serieTexte, String badge) {
        PdfPTable racine = new PdfPTable(new float[]{35, 65});
        racine.setWidthPercentage(100);

        PdfPCell gauche = noBorderCell();
        gauche.addElement(new Paragraph("UNIVERSITE DE DAKAR", F_ENTETE_UNIV));
        Paragraph office = new Paragraph("OFFICE DU BACCALAUREAT", F_ENTETE_BOLD);
        office.setSpacingBefore(6);
        gauche.addElement(office);
        racine.addCell(gauche);

        PdfPCell droite = noBorderCell();
        Paragraph bac = new Paragraph(titreBac, F_ENTETE_BOLD);
        bac.setAlignment(Element.ALIGN_CENTER);
        droite.addElement(bac);
        Paragraph titre = new Paragraph("CERTIFICAT  PROCES - VERBAL  D'EXAMEN", F_TITRE);
        titre.setAlignment(Element.ALIGN_CENTER);
        titre.setSpacingBefore(4);
        droite.addElement(titre);
        racine.addCell(droite);

        // Avertissement pleine largeur
        PdfPCell avert = noBorderCell();
        avert.setColspan(2);
        Paragraph avertP = new Paragraph(
                "(Le présent Certificat ne doit jamais être remis entre les mains du candidat "
                        + "et reviendra à l'Office dès la fin des épreuves)", F_BOLD);
        avertP.setSpacingBefore(6);
        avert.addElement(avertP);
        racine.addCell(avert);

        // Texte légal (gauche) + session / série / badge (droite)
        PdfPCell legal = noBorderCell();
        Paragraph legalP = new Paragraph(texteLegal, F_PETIT);
        legalP.setSpacingBefore(4);
        legal.addElement(legalP);
        racine.addCell(legal);

        PdfPCell session = noBorderCell();
        PdfPTable sessionTable = new PdfPTable(new float[]{25, 35, 20, 20});
        sessionTable.setWidthPercentage(100);
        PdfPCell sLabel = borderedCell("SESSION :", F_SESSION, Element.ALIGN_LEFT);
        sLabel.setBorder(Rectangle.NO_BORDER);
        sessionTable.addCell(sLabel);
        PdfPCell sVal = noBorderCell();
        sVal.addElement(new Paragraph("NORMALE", F_SESSION));
        sVal.addElement(new Paragraph("DE REMPLACEMENT", F_SESSION));
        sessionTable.addCell(sVal);
        PdfPCell jury = noBorderCell();
        jury.addElement(new Paragraph("JURY N° " + pointilles(10), F_SESSION));
        sessionTable.addCell(jury);
        PdfPCell badgeCell = new PdfPCell(new Phrase(badge, F_OPTION_BOX));
        badgeCell.setBorderWidth(1.2f);
        badgeCell.setPadding(4f);
        badgeCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        sessionTable.addCell(badgeCell);
        session.addElement(sessionTable);
        Paragraph serieP = new Paragraph(serieTexte, F_BOLD);
        serieP.setSpacingBefore(6);
        session.addElement(serieP);
        racine.addCell(session);

        return racine;
    }

    private PdfPTable buildCadreCandidatQuatreLignes() {
        PdfPTable racine = new PdfPTable(new float[]{58, 42});
        racine.setWidthPercentage(100);

        PdfPCell legende = new PdfPCell();
        legende.setBorderWidth(0.75f);
        legende.setPadding(5f);
        legende.addElement(new Paragraph(
                "1e ligne :  Mr. Mme ou Mlle — Prénom — NOM — Etabl. d'orig. ou Ind. (code 3 lett.)", F_PETIT));
        legende.addElement(new Paragraph(
                "2e ligne :  Date et lieu de naissance — Nationalité (abrégé 3 lett.)", F_PETIT));
        legende.addElement(new Paragraph(
                "3e ligne :  Série — Options et Langues choisies (2 lett.) — Epr. fac. Educ. phys. "
                        + "(abrégé) Apte/Inapte — se prés. pour la ....e fois", F_PETIT));
        legende.addElement(new Paragraph(
                "4e ligne :  EAF : subies en 19.. — Centre de.... — Notes/20 (écrit-oral) — "
                        + "était élève de (Etabl. ou Ind.)", F_PETIT));
        legende.addElement(new Paragraph(
                "(Français) ou bien : \"subira le français en 19....\"", F_PETIT_ITALIC));
        racine.addCell(legende);

        PdfPCell cadre = new PdfPCell();
        cadre.setBorderWidth(1f);
        cadre.setPadding(6f);
        for (int i = 0; i < 4; i++) {
            Paragraph ligne = new Paragraph(pointilles(60), F_NORMAL);
            ligne.setSpacingBefore(i == 0 ? 0 : 8);
            cadre.addElement(ligne);
        }
        racine.addCell(cadre);

        return racine;
    }

    private PdfPTable buildDeclarationJury() {
        PdfPTable pied = new PdfPTable(new float[]{50, 50});
        pied.setWidthPercentage(100);

        for (int i = 0; i < 2; i++) {
            PdfPCell c = new PdfPCell();
            c.setBorderWidth(0.5f);
            c.setPadding(6f);
            c.addElement(new Paragraph("l'avons déclaré " + pointilles(45), F_NORMAL));
            Paragraph mention = new Paragraph("avec mention " + pointilles(45), F_NORMAL);
            mention.setSpacingBefore(6);
            c.addElement(mention);
            Paragraph fait = new Paragraph("Fait à " + pointilles(15) + " le " + pointilles(15) + " 19......", F_NORMAL);
            fait.setSpacingBefore(6);
            c.addElement(fait);
            PdfPTable sig = new PdfPTable(new float[]{50, 50});
            sig.setWidthPercentage(100);
            PdfPCell pres = noBorderCell();
            pres.addElement(new Paragraph("Le Président du Jury :", F_PETIT_ITALIC));
            sig.addCell(pres);
            PdfPCell membres = noBorderCell();
            Paragraph m = new Paragraph("Les Membres du Jury :", F_PETIT_ITALIC);
            m.setAlignment(Element.ALIGN_RIGHT);
            membres.addElement(m);
            sig.addCell(membres);
            c.addElement(sig);
            pied.addCell(c);
        }
        return pied;
    }

    private PdfPTable buildConsignesA2() {
        PdfPTable bas = new PdfPTable(new float[]{50, 50});
        bas.setWidthPercentage(100);

        PdfPCell gauche = new PdfPCell();
        gauche.setBorderWidth(0.5f);
        gauche.setPadding(6f);
        gauche.addElement(new Paragraph(
                "Je soussigné déclare choisir comme épreuves de contrôle les 2 matières suivantes :", F_NORMAL));
        Paragraph l1 = new Paragraph("1 " + pointilles(40), F_NORMAL);
        l1.setSpacingBefore(6);
        gauche.addElement(l1);
        Paragraph l2 = new Paragraph("2 " + pointilles(40), F_NORMAL);
        l2.setSpacingBefore(4);
        gauche.addElement(l2);
        bas.addCell(gauche);

        PdfPCell droite = new PdfPCell();
        droite.setBorderWidth(0.5f);
        droite.setPadding(6f);
        droite.addElement(new Paragraph(
                "Pour remplir ci-dessus la grille EPREUVES ORALES DE CONTROLE :", F_BOLD));
        Paragraph a = new Paragraph(
                "a) : inscrire les matières choisies ci-contre par le candidat parmi celles "
                        + "qu'il a subies à l'ECRIT", F_PETIT);
        a.setSpacingBefore(4);
        droite.addElement(a);
        bas.addCell(droite);

        return bas;
    }

    private PdfPTable buildConsignesTechnicien() {
        PdfPTable bas = new PdfPTable(new float[]{45, 55});
        bas.setWidthPercentage(100);

        PdfPCell gauche = new PdfPCell();
        gauche.setBorderWidth(0.5f);
        gauche.setPadding(6f);
        gauche.addElement(new Paragraph(
                "Je soussigné, admis à subir le 2e Groupe d'épreuves déclare choisir comme "
                        + "épreuve de contrôle les matières suivantes :", F_NORMAL));
        Paragraph l1 = new Paragraph("1 " + pointilles(25) + " /", F_NORMAL);
        l1.setSpacingBefore(6);
        gauche.addElement(l1);
        Paragraph l2 = new Paragraph("2 " + pointilles(25) + " /   DOMINANTES (Ecrit de contrôle)", F_NORMAL);
        l2.setSpacingBefore(4);
        gauche.addElement(l2);
        Paragraph l3 = new Paragraph("3 " + pointilles(25) + " /   (Oral)", F_NORMAL);
        l3.setSpacingBefore(4);
        gauche.addElement(l3);
        bas.addCell(gauche);

        PdfPCell droite = new PdfPCell();
        droite.setBorderWidth(0.5f);
        droite.setPadding(6f);
        droite.addElement(new Paragraph(
                "Pour remplir ci-dessus la grille EPREUVES DE CONTROLE :", F_BOLD));
        droite.addElement(new Paragraph(
                "a) : inscrire les matières choisies ci-contre par le candidat parmi celles "
                        + "qu'il a subies à l'Ecrit", F_PETIT));
        droite.addElement(new Paragraph(
                "b) : s'il s'agit du FRANÇAIS, porter le total ECRIT + ORAL", F_PETIT));
        droite.addElement(new Paragraph(
                "c) : note obtenue à l'épreuve de contrôle", F_PETIT));
        droite.addElement(new Paragraph(
                "d) : même coefficient qu'à l'écrit ; en FRANÇAIS : ECRIT + ORAL", F_PETIT));
        droite.addElement(new Paragraph(
                "e) : si la note est la même qu'à l'écrit ou inférieure à l'écrit, "
                        + "porter ici un DOUBLE ZERO.", F_PETIT));
        Paragraph imp = new Paragraph(
                "TRES IMPORTANT : Ce document étant destiné à faire foi en cas de contestation, "
                        + "sa présentation ne doit comporter aucune ambiguïté ; en particulier, "
                        + "si des notes sont modifiées au cours de la délibération, ces modifications "
                        + "doivent être inscrites clairement et le total corrigé devra être inscrit "
                        + "en toutes lettres, accompagné du paraphe du Président du Jury.", F_PETIT_ITALIC);
        imp.setSpacingBefore(6);
        droite.addElement(imp);
        bas.addCell(droite);

        return bas;
    }
}
