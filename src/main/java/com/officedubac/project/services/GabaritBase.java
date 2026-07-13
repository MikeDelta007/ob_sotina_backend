package com.officedubac.project.services;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.List;

/**
 * Classe de base des gabarits de relevés de notes de l'Office du Baccalauréat (UCAD).
 *
 * Fournit les polices, les helpers de cellules et trois générateurs génériques :
 *  - {@link #genererReleveModerne}  : relevé "grand format" (2 groupes d'épreuves côte à côte)
 *  - {@link #genererReleveAncien}   : relevé "ancien format" type certificat (A1, A4, D, F3...)
 *  - {@link #genererDeuxiemePartie} : relevé "1ère/2ème partie" petit format (écrites / orales)
 *
 * Chaque gabarit concret (GabaritS1, GabaritL2, ...) ne déclare que sa structure
 * (matières, coefficients, badge de série) et délègue le rendu à cette classe.
 */
public abstract class GabaritBase {

    protected static final Font F_ENTETE_UNIV = new Font(Font.HELVETICA, 9, Font.NORMAL);
    protected static final Font F_ENTETE_BOLD = new Font(Font.HELVETICA, 9, Font.BOLD);
    protected static final Font F_TITRE = new Font(Font.HELVETICA, 13, Font.BOLD);
    protected static final Font F_SESSION = new Font(Font.HELVETICA, 8, Font.NORMAL);
    protected static final Font F_OPTION_BOX = new Font(Font.HELVETICA, 11, Font.BOLD);
    protected static final Font F_NORMAL = new Font(Font.HELVETICA, 8, Font.NORMAL);
    protected static final Font F_BOLD = new Font(Font.HELVETICA, 8, Font.BOLD);
    protected static final Font F_PETIT = new Font(Font.HELVETICA, 6.5f, Font.NORMAL);
    protected static final Font F_PETIT_ITALIC = new Font(Font.HELVETICA, 6.5f, Font.ITALIC);
    protected static final Font F_TABLE_HEADER = new Font(Font.HELVETICA, 7, Font.BOLD);
    protected static final Font F_TABLE_DATA = new Font(Font.HELVETICA, 7.5f, Font.NORMAL);

    // Colonnes ancien format : Matière | Sous-item | Note/20 | Coeff | Points obtenus | Sur
    protected static final float[] LARGEURS_ANCIEN_G1 = {16, 22, 12, 10, 14, 10};
    protected static final float[] LARGEURS_ANCIEN_G2 = {26, 12, 10, 8, 14, 10};

    // Colonnes format moderne : Section | Matière | Note sur 20 | Coeff | Pts Obts | Sur
    protected static final float[] LARGEURS_MODERNE_G1 = {13, 33, 12, 10, 12, 10};
    protected static final float[] LARGEURS_MODERNE_G2 = {34, 12, 10, 8, 13, 10};

    // ==================================================================
    // Modèle
    // ==================================================================

    /** Une épreuve : libellé + coefficient (sur = coeff x 20). */
    protected static class Epreuve {
        final String libelle;
        final int coeff;

        Epreuve(String libelle, int coeff) {
            this.libelle = libelle;
            this.coeff = coeff;
        }
    }

    /** Une section du 1er groupe (Français, Ecrit, Oral, Pratique...). */
    protected static class Section {
        final String libelle; // peut être vide ("") si la série n'a pas de regroupement
        final List<Epreuve> epreuves;

        Section(String libelle, List<Epreuve> epreuves) {
            this.libelle = libelle;
            this.epreuves = epreuves;
        }
    }

    protected static Epreuve ep(String libelle, int coeff) {
        return new Epreuve(libelle, coeff);
    }

    protected static Section section(String libelle, Epreuve... epreuves) {
        return new Section(libelle, Arrays.asList(epreuves));
    }

    protected static int sommeCoeffs(List<Section> sections) {
        int somme = 0;
        for (Section s : sections) {
            for (Epreuve e : s.epreuves) {
                somme += e.coeff;
            }
        }
        return somme;
    }

    // ==================================================================
    // Helpers communs
    // ==================================================================

    protected Paragraph espace(float taille) {
        Paragraph p = new Paragraph(" ");
        p.setSpacingAfter(taille);
        return p;
    }

    protected String pointilles(int longueur) {
        return ".".repeat(longueur);
    }

    protected PdfPCell noBorderCell() {
        PdfPCell c = new PdfPCell();
        c.setBorder(Rectangle.NO_BORDER);
        return c;
    }

    protected PdfPCell borderedCell(String texte, Font font, int align) {
        PdfPCell c = new PdfPCell(new Phrase(texte, font));
        c.setBorderWidth(0.5f);
        c.setHorizontalAlignment(align);
        c.setPadding(3f);
        return c;
    }

    protected PdfPCell headerBandCell(String texte, int colspan) {
        PdfPCell c = new PdfPCell(new Phrase(texte, F_TABLE_HEADER));
        c.setColspan(colspan);
        c.setBackgroundColor(new Color(230, 230, 230));
        c.setBorderWidth(0.75f);
        c.setPadding(4f);
        c.setHorizontalAlignment(Element.ALIGN_CENTER);
        return c;
    }

    protected PdfPCell subHeader(String texte, int align) {
        PdfPCell c = new PdfPCell(new Phrase(texte, F_PETIT));
        c.setBorderWidth(0.5f);
        c.setPadding(3f);
        c.setHorizontalAlignment(align);
        return c;
    }

    protected PdfPCell subLabelCell(String texte) {
        PdfPCell c = new PdfPCell(new Phrase(texte, F_TABLE_DATA));
        c.setBorderWidth(0.5f);
        c.setPadding(3f);
        return c;
    }

    protected PdfPCell dataCell(String texte, int align) {
        PdfPCell c = new PdfPCell(new Phrase(texte, F_TABLE_DATA));
        c.setBorderWidth(0.5f);
        c.setPadding(3f);
        c.setFixedHeight(16f);
        c.setHorizontalAlignment(align);
        c.setVerticalAlignment(Element.ALIGN_MIDDLE);
        return c;
    }

    protected PdfPCell totalCell(String texte, int colspan) {
        PdfPCell c = new PdfPCell(new Phrase(texte, F_BOLD));
        c.setColspan(colspan);
        c.setBorderWidth(0.75f);
        c.setPadding(4f);
        c.setBackgroundColor(new Color(245, 245, 245));
        return c;
    }

    protected PdfPCell sectionCell(String texte, int rowspan) {
        PdfPCell c = new PdfPCell(new Phrase(texte, F_BOLD));
        c.setRowspan(rowspan);
        c.setBorderWidth(0.5f);
        c.setPadding(4f);
        c.setVerticalAlignment(Element.ALIGN_MIDDLE);
        return c;
    }

    // ==================================================================
    // Générateur 1 : relevé grand format "moderne"
    // ==================================================================

    /**
     * Relevé de notes grand format à deux groupes d'épreuves côte à côte.
     *
     * @param serieLibelle sous-titre de série, ex. "Série : Sciences Expérimentales"
     * @param badge        texte du cadre de série, ex. "SERIE : S2"
     * @param siecle       préfixe d'année du pied de page, "19" ou "20"
     * @param sections     sections du 1er groupe (le total = somme des coeffs x 20)
     */
    protected byte[] genererReleveModerne(String serieLibelle, String badge, String siecle, List<Section> sections) {
        int total = sommeCoeffs(sections) * 20;
        Document document = new Document(PageSize.A4, 40, 40, 40, 40);

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter.getInstance(document, baos);
            document.open();

            document.add(buildEnteteModerne(serieLibelle, badge));
            document.add(espace(4));
            document.add(buildEncadreCandidat());
            document.add(espace(4));

            PdfPTable corps = new PdfPTable(new float[]{50, 50});
            corps.setWidthPercentage(100);

            PdfPCell gauche = new PdfPCell(buildGroupe1Moderne(sections, total, siecle));
            gauche.setPadding(0);
            gauche.setBorder(Rectangle.NO_BORDER);
            corps.addCell(gauche);

            PdfPCell droite = new PdfPCell(buildGroupe2Moderne(total, siecle));
            droite.setPadding(0);
            droite.setBorder(Rectangle.NO_BORDER);
            corps.addCell(droite);

            document.add(corps);
            document.close();
            return baos.toByteArray();
        } catch (DocumentException | java.io.IOException e) {
            throw new RuntimeException("Erreur lors de la génération du PDF", e);
        }
    }

    private PdfPTable buildEnteteModerne(String serieLibelle, String badge) {
        PdfPTable racine = new PdfPTable(new float[]{40, 35, 25});
        racine.setWidthPercentage(100);

        PdfPCell gauche = noBorderCell();
        gauche.addElement(new Paragraph("UNIVERSITE CHEIKH ANTA DIOP", F_ENTETE_UNIV));
        Paragraph deDakar = new Paragraph("DE DAKAR", F_ENTETE_UNIV);
        deDakar.setAlignment(Element.ALIGN_CENTER);
        gauche.addElement(deDakar);
        Paragraph office = new Paragraph("OFFICE DU BACCALAUREAT", F_ENTETE_BOLD);
        office.setSpacingBefore(14);
        gauche.addElement(office);
        racine.addCell(gauche);

        PdfPCell centre = noBorderCell();
        Paragraph bac = new Paragraph("BACCALAUREAT DE L'ENSEIGNEMENT SECONDAIRE", F_ENTETE_BOLD);
        bac.setAlignment(Element.ALIGN_CENTER);
        centre.addElement(bac);
        Paragraph titre = new Paragraph("RELEVE DE NOTES", F_TITRE);
        titre.setAlignment(Element.ALIGN_CENTER);
        titre.setSpacingBefore(8);
        centre.addElement(titre);
        Paragraph serie = new Paragraph(serieLibelle, F_BOLD);
        serie.setAlignment(Element.ALIGN_CENTER);
        serie.setSpacingBefore(10);
        centre.addElement(serie);
        racine.addCell(centre);

        PdfPCell droite = noBorderCell();
        PdfPTable sessionTable = new PdfPTable(new float[]{40, 60});
        sessionTable.setWidthPercentage(100);
        PdfPCell sessionLabel = borderedCell("SESSION (1) :", F_SESSION, Element.ALIGN_LEFT);
        sessionLabel.setBorder(Rectangle.RIGHT);
        sessionLabel.setBorderWidthRight(0.5f);
        sessionTable.addCell(sessionLabel);
        PdfPCell sessionValeurs = noBorderCell();
        sessionValeurs.addElement(new Paragraph("NORMALE", F_SESSION));
        Paragraph remp = new Paragraph("DE REMPLACEMENT", F_SESSION);
        remp.setSpacingBefore(2);
        sessionValeurs.addElement(remp);
        sessionTable.addCell(sessionValeurs);
        droite.addElement(sessionTable);

        Paragraph jury = new Paragraph("JURY N° ..............", F_NORMAL);
        jury.setSpacingBefore(6);
        droite.addElement(jury);
        Paragraph annee = new Paragraph("ANNEE : ..............", F_NORMAL);
        annee.setSpacingBefore(4);
        droite.addElement(annee);

        PdfPTable optionBox = new PdfPTable(1);
        optionBox.setWidthPercentage(80);
        optionBox.setHorizontalAlignment(Element.ALIGN_RIGHT);
        PdfPCell optionCell = new PdfPCell(new Phrase(badge, F_OPTION_BOX));
        optionCell.setBorderWidth(1.2f);
        optionCell.setPadding(5f);
        optionCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        optionBox.addCell(optionCell);
        optionBox.setSpacingBefore(8);
        droite.addElement(optionBox);

        racine.addCell(droite);
        return racine;
    }

    protected PdfPTable buildEncadreCandidat() {
        PdfPTable racine = new PdfPTable(new float[]{62, 38});
        racine.setWidthPercentage(100);

        PdfPCell gauche = new PdfPCell();
        gauche.setBorderWidth(0.75f);
        gauche.setPadding(6f);
        gauche.addElement(new Paragraph("M. " + pointilles(70), F_NORMAL));
        Paragraph naiss = new Paragraph("né (e) le " + pointilles(20) + " à " + pointilles(30), F_NORMAL);
        naiss.setSpacingBefore(8);
        gauche.addElement(naiss);
        Paragraph notes = new Paragraph("a obtenu les notes suivantes : " + pointilles(15), F_NORMAL);
        notes.setSpacingBefore(8);
        gauche.addElement(notes);
        racine.addCell(gauche);

        PdfPCell droite = new PdfPCell();
        droite.setBorderWidth(0.75f);
        droite.setPadding(6f);
        droite.addElement(new Paragraph("Etab. " + pointilles(12) + " Ind. " + pointilles(10), F_NORMAL));
        Paragraph options = new Paragraph("Options " + pointilles(25), F_NORMAL);
        options.setSpacingBefore(8);
        droite.addElement(options);
        Paragraph nf = new Paragraph("(N) " + pointilles(12) + " (F) " + pointilles(12), F_NORMAL);
        nf.setSpacingBefore(8);
        droite.addElement(nf);
        racine.addCell(droite);

        return racine;
    }

    private PdfPTable buildGroupe1Moderne(List<Section> sections, int total, String siecle) {
        PdfPTable t = new PdfPTable(LARGEURS_MODERNE_G1);
        t.setWidthPercentage(100);

        t.addCell(headerBandCell("1er GROUPE D'EPREUVES", 6));

        PdfPCell entete01 = subHeader("(en nombres entiers)", Element.ALIGN_LEFT);
        entete01.setColspan(2);
        t.addCell(entete01);
        t.addCell(subHeader("Note\nSur 20", Element.ALIGN_CENTER));
        t.addCell(subHeader("Coeff.", Element.ALIGN_CENTER));
        t.addCell(subHeader("Pts\nObts", Element.ALIGN_CENTER));
        t.addCell(subHeader("Sur", Element.ALIGN_CENTER));

        for (Section s : sections) {
            boolean premiere = true;
            for (Epreuve e : s.epreuves) {
                if (premiere) {
                    t.addCell(sectionCell(s.libelle, s.epreuves.size()));
                    premiere = false;
                }
                t.addCell(subLabelCell(e.libelle));
                t.addCell(dataCell("", Element.ALIGN_CENTER));
                t.addCell(dataCell(String.valueOf(e.coeff), Element.ALIGN_CENTER));
                t.addCell(dataCell("", Element.ALIGN_CENTER));
                t.addCell(dataCell(String.valueOf(e.coeff * 20), Element.ALIGN_CENTER));
            }
        }

        // Education physique
        PdfPCell edPhys = new PdfPCell(new Phrase("Education Physique\nNote sur 20 " + pointilles(10)
                + "\nInapte ou C. ASS.", F_PETIT));
        edPhys.setColspan(2);
        edPhys.setRowspan(2);
        edPhys.setBorderWidth(0.5f);
        edPhys.setPadding(4f);
        t.addCell(edPhys);
        PdfPCell ptsPos = subLabelCell("Points positifs :");
        ptsPos.setColspan(2);
        t.addCell(ptsPos);
        t.addCell(dataCell("", Element.ALIGN_CENTER));
        t.addCell(dataCell("", Element.ALIGN_CENTER));
        PdfPCell ptsNeg = subLabelCell("Points négatifs :");
        ptsNeg.setColspan(2);
        t.addCell(ptsNeg);
        t.addCell(dataCell("", Element.ALIGN_CENTER));
        t.addCell(dataCell("", Element.ALIGN_CENTER));

        // 1er TOTAL
        t.addCell(totalCell("1er TOTAL  ———>", 4));
        t.addCell(dataCell("", Element.ALIGN_CENTER));
        t.addCell(dataCell(String.valueOf(total), Element.ALIGN_CENTER));

        // EPR. FACULTATIVE
        PdfPCell fac = new PdfPCell(new Phrase("EPR. FACULTATIVE\n(Pts au-dessus de la moy.)", F_PETIT));
        fac.setColspan(2);
        fac.setRowspan(2);
        fac.setBorderWidth(0.5f);
        fac.setPadding(4f);
        fac.setVerticalAlignment(Element.ALIGN_MIDDLE);
        t.addCell(fac);
        PdfPCell langue = subLabelCell("Langue");
        langue.setColspan(2);
        t.addCell(langue);
        t.addCell(dataCell("", Element.ALIGN_CENTER));
        t.addCell(dataCell("", Element.ALIGN_CENTER));
        PdfPCell dessin = subLabelCell("Des. Cout. ou Mus.");
        dessin.setColspan(2);
        t.addCell(dessin);
        t.addCell(dataCell("", Element.ALIGN_CENTER));
        t.addCell(dataCell("", Element.ALIGN_CENTER));

        // 2ème TOTAL
        t.addCell(totalCell("2ème TOTAL  ———>", 4));
        t.addCell(dataCell("", Element.ALIGN_CENTER));
        t.addCell(dataCell(String.valueOf(total), Element.ALIGN_CENTER));

        // Pied gauche
        PdfPCell pied = new PdfPCell();
        pied.setColspan(6);
        pied.setBorderWidth(0.5f);
        pied.setPadding(5f);
        pied.addElement(new Paragraph("A l'issue du 1er Groupe d'épreuves : " + pointilles(30), F_NORMAL));
        pied.addElement(new Paragraph("Le candidat a été déclaré : " + pointilles(35), F_NORMAL));
        pied.addElement(new Paragraph("ADMIS avec la mention :", F_BOLD));
        pied.addElement(new Paragraph("AUTORISE à subir le 2ème groupe d'épreuves", F_NORMAL));
        pied.addElement(new Paragraph("AJOURNE", F_BOLD));
        Paragraph fait = new Paragraph("fait à " + pointilles(20) + " le " + pointilles(25) + " " + siecle + "........", F_NORMAL);
        fait.setSpacingBefore(8);
        pied.addElement(fait);
        pied.addElement(new Paragraph("Prénom (S), Nom et signature du Président du Jury.", F_PETIT));
        Paragraph cachet = new Paragraph("Cachet obligatoire", F_BOLD);
        cachet.setSpacingBefore(10);
        pied.addElement(cachet);
        t.addCell(pied);

        return t;
    }

    private PdfPTable buildGroupe2Moderne(int total, String siecle) {
        PdfPTable t = new PdfPTable(LARGEURS_MODERNE_G2);
        t.setWidthPercentage(100);

        t.addCell(headerBandCell("2ème GROUPE D'EPREUVES", 6));

        t.addCell(subHeader("(en nombres entiers)", Element.ALIGN_LEFT));
        t.addCell(subHeader("Note\nSur 20", Element.ALIGN_CENTER));
        t.addCell(subHeader("Coeff.", Element.ALIGN_CENTER));
        t.addCell(subHeader("X", Element.ALIGN_CENTER));
        t.addCell(subHeader("Pts\nObts", Element.ALIGN_CENTER));
        t.addCell(subHeader("Sur", Element.ALIGN_CENTER));

        // REPORT du 1er TOTAL
        t.addCell(totalCell("REPORT du 1er TOTAL  ———>", 4));
        t.addCell(dataCell("", Element.ALIGN_CENTER));
        t.addCell(dataCell(String.valueOf(total), Element.ALIGN_CENTER));

        // EPR. DE CONTROLE
        PdfPCell controle = new PdfPCell(new Phrase("EPR. DE CONTROLE", F_BOLD));
        controle.setColspan(6);
        controle.setBorderWidth(0.5f);
        controle.setPadding(4f);
        t.addCell(controle);

        // Grille matières choisies
        PdfPCell matieres = new PdfPCell(new Phrase("MATIERES CHOISIES", F_TABLE_DATA));
        matieres.setRowspan(5);
        matieres.setBorderWidth(0.5f);
        matieres.setPadding(4f);
        matieres.setVerticalAlignment(Element.ALIGN_MIDDLE);
        t.addCell(matieres);
        t.addCell(subHeader("Rappel\ndes points\nobtenus\nau 1er Gr.", Element.ALIGN_CENTER));
        t.addCell(subHeader("Nouv.\nnote\nsur\n20", Element.ALIGN_CENTER));
        t.addCell(subHeader("Coeff.", Element.ALIGN_CENTER));
        t.addCell(subHeader("Pts\nau\nCont.", Element.ALIGN_CENTER));
        t.addCell(subHeader("Diff.\nen\nplus", Element.ALIGN_CENTER));
        for (int i = 0; i < 4; i++) {
            t.addCell(dataCell("", Element.ALIGN_CENTER));
            t.addCell(dataCell("", Element.ALIGN_CENTER));
            t.addCell(dataCell("", Element.ALIGN_CENTER));
            t.addCell(dataCell("", Element.ALIGN_CENTER));
            t.addCell(dataCell("", Element.ALIGN_CENTER));
        }

        // EPR. FACULTATIVE
        PdfPCell fac = new PdfPCell(new Phrase("EPR. FACULTATIVE\n(Points au-dessus\nde la moyenne)", F_PETIT));
        fac.setColspan(2);
        fac.setRowspan(2);
        fac.setBorderWidth(0.5f);
        fac.setPadding(4f);
        fac.setVerticalAlignment(Element.ALIGN_MIDDLE);
        t.addCell(fac);
        PdfPCell langue = subLabelCell("Langue");
        langue.setColspan(2);
        t.addCell(langue);
        t.addCell(dataCell("", Element.ALIGN_CENTER));
        t.addCell(dataCell("", Element.ALIGN_CENTER));
        PdfPCell dessin = subLabelCell("Des. Cout. ou Mus.");
        dessin.setColspan(2);
        t.addCell(dessin);
        t.addCell(dataCell("", Element.ALIGN_CENTER));
        t.addCell(dataCell("", Element.ALIGN_CENTER));

        // TOTAL DEFINITIF
        t.addCell(totalCell("TOTAL DEFINITIF  ———>", 4));
        t.addCell(dataCell("", Element.ALIGN_CENTER));
        t.addCell(dataCell(String.valueOf(total), Element.ALIGN_CENTER));

        // Pied droit
        PdfPCell pied = new PdfPCell();
        pied.setColspan(6);
        pied.setBorderWidth(0.5f);
        pied.setPadding(5f);
        pied.addElement(new Paragraph("A l'issue du 2ème Groupe d'épreuves : " + pointilles(30), F_NORMAL));
        pied.addElement(new Paragraph("Le candidat a été déclaré : " + pointilles(35), F_NORMAL));
        pied.addElement(new Paragraph("ADMIS avec la mention PASSABLE", F_BOLD));
        pied.addElement(new Paragraph("AJOURNE", F_BOLD));
        Paragraph fait = new Paragraph("fait à " + pointilles(20) + " le " + pointilles(25) + " " + siecle + "........", F_NORMAL);
        fait.setSpacingBefore(8);
        pied.addElement(fait);
        pied.addElement(new Paragraph("Prénom (S), Nom et signature du Président du Jury.", F_PETIT));
        Paragraph cachet = new Paragraph("Cachet obligatoire", F_BOLD);
        cachet.setSpacingBefore(10);
        pied.addElement(cachet);
        Paragraph note = new Paragraph("(1) Rayer la mention inutile.", F_PETIT_ITALIC);
        note.setSpacingBefore(8);
        pied.addElement(note);
        t.addCell(pied);

        return t;
    }

    // ==================================================================
    // Générateur 2 : relevé "ancien format" type certificat (A1, A4, D, F3...)
    // ==================================================================

    /**
     * Relevé ancien format (machine à écrire) : sections à sous-items dans le 1er groupe,
     * points supplémentaires, lignes de matières dans le 2ème groupe, total provisoire
     * puis total définitif.
     *
     * @param badge          texte du cadre en haut à droite, ex. "OPTION A2"
     * @param sections       sections du 1er groupe (Français écrit/oral, Ecrit, Oral...)
     * @param lignesGroupe2  matières propres au 2ème groupe (ex. "Langue vivante", coeff 3)
     * @param totalDefinitif total du 2ème groupe (report + lignes groupe 2)
     * @param texteMentions  mentions concernées par les points supplémentaires,
     *                       ex. "BIEN ou TRES BIEN" ou "ASSEZ BIEN et AU-DESSUS"
     */
    protected byte[] genererReleveAncien(String badge, List<Section> sections,
                                         List<Epreuve> lignesGroupe2, int totalDefinitif,
                                         String texteMentions) {
        int total1 = sommeCoeffs(sections) * 20;
        Document document = new Document(PageSize.A4, 40, 40, 40, 40);

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter.getInstance(document, baos);
            document.open();

            document.add(buildEnteteAncien(badge));
            document.add(espace(4));
            document.add(buildEncadreCandidat());
            document.add(espace(4));

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
            document.add(espace(4));
            document.add(buildPiedAncien());

            document.close();
            return baos.toByteArray();
        } catch (DocumentException | java.io.IOException e) {
            throw new RuntimeException("Erreur lors de la génération du PDF", e);
        }
    }

    private PdfPTable buildEnteteAncien(String badge) {
        PdfPTable racine = new PdfPTable(new float[]{40, 35, 25});
        racine.setWidthPercentage(100);

        PdfPCell gauche = noBorderCell();
        gauche.addElement(new Paragraph("UNIVERSITE CHEIKH ANTA DIOP", F_ENTETE_UNIV));
        Paragraph deDakar = new Paragraph("DE DAKAR", F_ENTETE_UNIV);
        deDakar.setAlignment(Element.ALIGN_CENTER);
        gauche.addElement(deDakar);
        Paragraph office = new Paragraph("OFFICE DU BACCALAUREAT", F_ENTETE_BOLD);
        office.setSpacingBefore(14);
        gauche.addElement(office);
        racine.addCell(gauche);

        PdfPCell centre = noBorderCell();
        PdfPTable sessionTable = new PdfPTable(new float[]{40, 60});
        sessionTable.setWidthPercentage(100);
        PdfPCell sessionLabel = borderedCell("SESSION (1) :", F_SESSION, Element.ALIGN_LEFT);
        sessionLabel.setBorder(Rectangle.RIGHT);
        sessionLabel.setBorderWidthRight(0.5f);
        sessionTable.addCell(sessionLabel);
        PdfPCell sessionValeurs = noBorderCell();
        sessionValeurs.addElement(new Paragraph("NORMALE", F_SESSION));
        Paragraph remp = new Paragraph("DE REMPLACEMENT", F_SESSION);
        remp.setSpacingBefore(2);
        sessionValeurs.addElement(remp);
        sessionTable.addCell(sessionValeurs);
        centre.addElement(sessionTable);

        Paragraph titre = new Paragraph("RELEVE DE NOTES", F_TITRE);
        titre.setAlignment(Element.ALIGN_CENTER);
        titre.setSpacingBefore(16);
        centre.addElement(titre);
        racine.addCell(centre);

        PdfPCell droite = noBorderCell();
        droite.setHorizontalAlignment(Element.ALIGN_RIGHT);
        PdfPTable optionBox = new PdfPTable(1);
        optionBox.setWidthPercentage(70);
        optionBox.setHorizontalAlignment(Element.ALIGN_RIGHT);
        PdfPCell optionCell = new PdfPCell(new Phrase(badge, F_OPTION_BOX));
        optionCell.setBorderWidth(1.2f);
        optionCell.setPadding(5f);
        optionCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        optionBox.addCell(optionCell);
        droite.addElement(optionBox);
        Paragraph jury = new Paragraph("Jury n° ..............", F_NORMAL);
        jury.setAlignment(Element.ALIGN_RIGHT);
        jury.setSpacingBefore(8);
        droite.addElement(jury);
        Paragraph annee = new Paragraph("Année ..............", F_NORMAL);
        annee.setAlignment(Element.ALIGN_RIGHT);
        annee.setSpacingBefore(6);
        droite.addElement(annee);
        racine.addCell(droite);

        return racine;
    }

    protected PdfPTable buildGroupe1Ancien(List<Section> sections, int total1, String texteMentions) {
        PdfPTable t = new PdfPTable(LARGEURS_ANCIEN_G1);
        t.setWidthPercentage(100);

        t.addCell(headerBandCell("1er GROUPE D'EPREUVES", 6));

        PdfPCell entete01 = subHeader("(en nombres entiers)", Element.ALIGN_LEFT);
        entete01.setColspan(2);
        t.addCell(entete01);
        t.addCell(subHeader("Note\nsur 20", Element.ALIGN_CENTER));
        t.addCell(subHeader("Coeff.", Element.ALIGN_CENTER));
        t.addCell(subHeader("Points\nobtenus", Element.ALIGN_CENTER));
        t.addCell(subHeader("sur", Element.ALIGN_CENTER));

        for (Section s : sections) {
            boolean premiere = true;
            for (Epreuve e : s.epreuves) {
                if (premiere) {
                    t.addCell(sectionCell(s.libelle, s.epreuves.size()));
                    premiere = false;
                }
                t.addCell(subLabelCell(e.libelle));
                t.addCell(dataCell("", Element.ALIGN_CENTER));
                t.addCell(dataCell(String.valueOf(e.coeff), Element.ALIGN_CENTER));
                t.addCell(dataCell("", Element.ALIGN_CENTER));
                t.addCell(dataCell(String.valueOf(e.coeff * 20), Element.ALIGN_CENTER));
            }
        }

        // 1er TOTAL
        t.addCell(totalCell("1er TOTAL  ———>", 4));
        t.addCell(dataCell("", Element.ALIGN_CENTER));
        t.addCell(dataCell(String.valueOf(total1), Element.ALIGN_CENTER));

        // Points supplémentaires
        PdfPCell texteSuppl = new PdfPCell(new Phrase(
                "Points supplémentaires à ne prendre en considération que pour les candidats "
                        + "définitivement admis après les épreuves du 1er Groupe (n'entrent en ligne de "
                        + "compte que pour les mentions " + texteMentions + ")", F_PETIT_ITALIC));
        texteSuppl.setColspan(6);
        texteSuppl.setBorderWidth(0.5f);
        texteSuppl.setPadding(4f);
        t.addCell(texteSuppl);

        PdfPCell libellePts = new PdfPCell(new Phrase("points au\ndessus de la\nmoyenne", F_PETIT));
        libellePts.setRowspan(3);
        libellePts.setBorderWidth(0.5f);
        libellePts.setPadding(4f);
        libellePts.setVerticalAlignment(Element.ALIGN_MIDDLE);
        t.addCell(libellePts);
        ajouterLignePointsSupp(t, "Educ. Physique");
        ajouterLignePointsSupp(t, "Epr. Langue");
        ajouterLignePointsSupp(t, "Fac. Des. Mus. Couture");

        // 2e TOTAL
        t.addCell(totalCell("2e TOTAL  ———>", 4));
        t.addCell(dataCell("", Element.ALIGN_CENTER));
        t.addCell(dataCell(String.valueOf(total1), Element.ALIGN_CENTER));

        return t;
    }

    private void ajouterLignePointsSupp(PdfPTable t, String libelle) {
        PdfPCell label = dataCell(libelle, Element.ALIGN_LEFT);
        label.setColspan(3);
        t.addCell(label);
        t.addCell(dataCell("", Element.ALIGN_CENTER));
        t.addCell(dataCell("", Element.ALIGN_CENTER));
    }

    protected PdfPTable buildGroupe2Ancien(List<Epreuve> lignesGroupe2, int total1, int totalDefinitif) {
        PdfPTable t = new PdfPTable(LARGEURS_ANCIEN_G2);
        t.setWidthPercentage(100);

        t.addCell(headerBandCell("2ème GROUPE D'EPREUVES", 6));

        t.addCell(subHeader("(en nombres entiers)", Element.ALIGN_LEFT));
        t.addCell(subHeader("Note\nsur 20", Element.ALIGN_CENTER));
        t.addCell(subHeader("Coeff.", Element.ALIGN_CENTER));
        t.addCell(subHeader("X", Element.ALIGN_CENTER));
        t.addCell(subHeader("Points\nobtenus", Element.ALIGN_CENTER));
        t.addCell(subHeader("sur", Element.ALIGN_CENTER));

        // REPORT du 1er TOTAL
        t.addCell(totalCell("REPORT du 1er TOTAL  ———>", 4));
        t.addCell(dataCell("", Element.ALIGN_CENTER));
        t.addCell(dataCell(String.valueOf(total1), Element.ALIGN_CENTER));

        // Lignes de matières propres au 2ème groupe
        for (Epreuve e : lignesGroupe2) {
            t.addCell(dataCell(e.libelle + pointilles(15), Element.ALIGN_LEFT));
            t.addCell(dataCell("", Element.ALIGN_CENTER));
            t.addCell(dataCell(String.valueOf(e.coeff), Element.ALIGN_CENTER));
            t.addCell(dataCell("", Element.ALIGN_CENTER));
            t.addCell(dataCell("", Element.ALIGN_CENTER));
            t.addCell(dataCell(String.valueOf(e.coeff * 20), Element.ALIGN_CENTER));
        }

        // EPR. ORALES de CONTROLE
        PdfPCell titreControle = new PdfPCell(new Phrase("EPR. ORALES de CONTROLE.", F_BOLD));
        titreControle.setColspan(6);
        titreControle.setBorderWidth(0.5f);
        titreControle.setPadding(4f);
        t.addCell(titreControle);

        t.addCell(subHeader("(a)\nMATIERES CHOISIES", Element.ALIGN_CENTER));
        t.addCell(subHeader("(b)\nRappel\ndes pts obt.\nau 1er Gr.", Element.ALIGN_CENTER));
        t.addCell(subHeader("(c)\nNouvelle\nnote sur 20", Element.ALIGN_CENTER));
        t.addCell(subHeader("(d)\nPts. obts\nà l'épreuve\nde contrôle", Element.ALIGN_CENTER));
        PdfPCell diff = subHeader("(e)\nDifférence\nEN PLUS", Element.ALIGN_CENTER);
        diff.setColspan(2);
        t.addCell(diff);

        for (int i = 0; i < 2; i++) {
            t.addCell(dataCell("", Element.ALIGN_LEFT));
            t.addCell(dataCell("", Element.ALIGN_CENTER));
            t.addCell(dataCell("", Element.ALIGN_CENTER));
            t.addCell(dataCell("", Element.ALIGN_CENTER));
            PdfPCell diffVide = dataCell("", Element.ALIGN_CENTER);
            diffVide.setColspan(2);
            t.addCell(diffVide);
        }

        // EPR. FACULTATIVES
        PdfPCell fac = new PdfPCell(new Phrase("EPR. FACULTATIVES", F_BOLD));
        fac.setColspan(2);
        fac.setBorderWidth(0.5f);
        fac.setPadding(4f);
        t.addCell(fac);
        PdfPCell facLangue = new PdfPCell(new Phrase("Langue " + pointilles(20), F_TABLE_DATA));
        facLangue.setColspan(4);
        facLangue.setBorderWidth(0.5f);
        facLangue.setPadding(4f);
        t.addCell(facLangue);

        PdfPCell facMoy = new PdfPCell(new Phrase("(points au dessus de la moyenne)", F_PETIT));
        facMoy.setColspan(2);
        facMoy.setBorderWidth(0.5f);
        facMoy.setPadding(4f);
        t.addCell(facMoy);
        PdfPCell facDessin = new PdfPCell(new Phrase("Dessin, Musique ou Couture", F_TABLE_DATA));
        facDessin.setColspan(4);
        facDessin.setBorderWidth(0.5f);
        facDessin.setPadding(4f);
        t.addCell(facDessin);

        // TOTAL PROVISOIRE
        t.addCell(totalCell("TOTAL PROVISOIRE :", 4));
        t.addCell(dataCell("", Element.ALIGN_CENTER));
        t.addCell(dataCell("", Element.ALIGN_CENTER));

        // Education physique
        PdfPCell edPhys = new PdfPCell(new Phrase("Education Physique\nNote sur 20........", F_TABLE_DATA));
        edPhys.setColspan(2);
        edPhys.setBorderWidth(0.5f);
        edPhys.setPadding(4f);
        t.addCell(edPhys);
        PdfPCell ptsPos = new PdfPCell(new Phrase("Points positifs :", F_TABLE_DATA));
        ptsPos.setColspan(2);
        ptsPos.setBorderWidth(0.5f);
        ptsPos.setPadding(4f);
        t.addCell(ptsPos);
        PdfPCell ptsNeg = new PdfPCell(new Phrase("Points négatifs :", F_TABLE_DATA));
        ptsNeg.setColspan(2);
        ptsNeg.setBorderWidth(0.5f);
        ptsNeg.setPadding(4f);
        t.addCell(ptsNeg);

        // TOTAL DEFINITIF
        t.addCell(totalCell("TOTAL DEFINITIF  ———>", 4));
        t.addCell(dataCell("", Element.ALIGN_CENTER));
        t.addCell(dataCell(String.valueOf(totalDefinitif), Element.ALIGN_CENTER));

        return t;
    }

    private PdfPTable buildPiedAncien() {
        PdfPTable pied = new PdfPTable(new float[]{50, 50});
        pied.setWidthPercentage(100);

        PdfPCell gauche = noBorderCell();
        gauche.setPadding(6f);
        gauche.addElement(new Paragraph("A l'issue du 1er groupe d'épreuves :", F_NORMAL));
        gauche.addElement(new Paragraph("Le candidat a été déclaré :", F_NORMAL));
        gauche.addElement(new Paragraph("ADMIS avec la mention :", F_BOLD));
        gauche.addElement(new Paragraph("AUTORISE à subir le 2ème groupe d'épreuves", F_NORMAL));
        gauche.addElement(new Paragraph("AJOURNE", F_BOLD));
        Paragraph fait1 = new Paragraph("Fait à .............. le .............. 19......", F_NORMAL);
        fait1.setSpacingBefore(8);
        gauche.addElement(fait1);
        gauche.addElement(new Paragraph("Cachet obligatoire Prénom(s), Nom et signature", F_PETIT));
        gauche.addElement(new Paragraph("du Président du Jury.", F_PETIT));
        pied.addCell(gauche);

        PdfPCell droite = noBorderCell();
        droite.setPadding(6f);
        droite.addElement(new Paragraph("A l'issue du 2ème Groupe d'épreuves :", F_NORMAL));
        droite.addElement(new Paragraph("Le candidat a été déclaré :", F_NORMAL));
        droite.addElement(new Paragraph("ADMIS avec la mention PASSABLE", F_BOLD));
        droite.addElement(new Paragraph("AJOURNE", F_BOLD));
        Paragraph fait2 = new Paragraph("Fait à .............. le ..............", F_NORMAL);
        fait2.setSpacingBefore(8);
        droite.addElement(fait2);
        droite.addElement(new Paragraph("Prénom(s), Nom et signature du Président du Jury.", F_PETIT));
        Paragraph cachet2 = new Paragraph("Cachet obligatoire", F_PETIT);
        cachet2.setSpacingBefore(6);
        droite.addElement(cachet2);
        Paragraph note = new Paragraph("(1) Rayer la mention inutile.", F_PETIT_ITALIC);
        note.setSpacingBefore(10);
        droite.addElement(note);
        pied.addCell(droite);

        return pied;
    }

    // ==================================================================
    // Générateur 3 : relevé "1ère / 2ème partie" petit format
    // ==================================================================

    /**
     * Relevé petit format : épreuves écrites et orales côte à côte.
     *
     * @param serie               ex. "Série A 1"
     * @param partie              ex. "2me PARTIE" ou "1re PARTIE"
     * @param sessionTitre        ex. "DEUXIEME SESSION" ou "PREMIERE SESSION"
     * @param ecrites             épreuves écrites
     * @param orales              épreuves orales
     * @param epsOral             true pour ajouter la ligne EPS (±) dans les orales
     * @param totalGeneralFormule ex. "ECRIT + ORAL" ou "ECRIT + ORAL + EPS" (null pour omettre)
     * @param mentionBas          ex. "ADMIS - AJOURNE" ou "ADMIS - AJOURNE - 2me SESSION"
     */
    protected byte[] genererDeuxiemePartie(String serie, String partie, String sessionTitre,
                                           List<Epreuve> ecrites, List<Epreuve> orales,
                                           boolean epsOral, String totalGeneralFormule,
                                           String mentionBas) {
        Document document = new Document(PageSize.A4, 120, 120, 60, 60);

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter.getInstance(document, baos);
            document.open();

            // En-tête
            PdfPTable entete = new PdfPTable(new float[]{60, 40});
            entete.setWidthPercentage(100);
            PdfPCell gauche = noBorderCell();
            gauche.addElement(new Paragraph("UNIVERSITE DE DAKAR", F_ENTETE_UNIV));
            Paragraph office = new Paragraph("OFFICE DU BACCALAUREAT", F_ENTETE_BOLD);
            office.setSpacingBefore(8);
            gauche.addElement(office);
            entete.addCell(gauche);
            PdfPCell droite = noBorderCell();
            Paragraph pSerie = new Paragraph(serie, F_ENTETE_BOLD);
            pSerie.setAlignment(Element.ALIGN_RIGHT);
            droite.addElement(pSerie);
            Paragraph pPartie = new Paragraph(partie, F_ENTETE_BOLD);
            pPartie.setAlignment(Element.ALIGN_RIGHT);
            droite.addElement(pPartie);
            Paragraph pJury = new Paragraph("Jury n° ..............", F_NORMAL);
            pJury.setAlignment(Element.ALIGN_RIGHT);
            pJury.setSpacingBefore(4);
            droite.addElement(pJury);
            entete.addCell(droite);
            document.add(entete);

            Paragraph session = new Paragraph(sessionTitre, F_ENTETE_BOLD);
            session.setAlignment(Element.ALIGN_CENTER);
            session.setSpacingBefore(12);
            document.add(session);
            Paragraph titre = new Paragraph("R E L E V E   D E   N O T E S", F_TITRE);
            titre.setAlignment(Element.ALIGN_CENTER);
            titre.setSpacingAfter(10);
            document.add(titre);

            // Bloc candidat + encadré Etab/Options
            PdfPTable candidat = new PdfPTable(new float[]{55, 45});
            candidat.setWidthPercentage(100);
            PdfPCell cGauche = noBorderCell();
            cGauche.addElement(new Paragraph("M " + pointilles(45), F_NORMAL));
            Paragraph ne = new Paragraph("né.... le " + pointilles(40), F_NORMAL);
            ne.setSpacingBefore(6);
            cGauche.addElement(ne);
            Paragraph a = new Paragraph("à " + pointilles(48), F_NORMAL);
            a.setSpacingBefore(6);
            cGauche.addElement(a);
            Paragraph obtenu = new Paragraph("a obtenu les notes suivantes :", F_NORMAL);
            obtenu.setSpacingBefore(6);
            cGauche.addElement(obtenu);
            candidat.addCell(cGauche);
            PdfPCell cDroite = new PdfPCell();
            cDroite.setBorderWidth(0.75f);
            cDroite.setPadding(5f);
            cDroite.addElement(new Paragraph("Etab. " + pointilles(10) + " Ind. " + pointilles(8), F_NORMAL));
            Paragraph opt = new Paragraph("Options " + pointilles(20), F_NORMAL);
            opt.setSpacingBefore(5);
            cDroite.addElement(opt);
            Paragraph nf = new Paragraph("(N) " + pointilles(10) + " (F) " + pointilles(10), F_NORMAL);
            nf.setSpacingBefore(5);
            cDroite.addElement(nf);
            candidat.addCell(cDroite);
            document.add(candidat);
            document.add(espace(6));

            // Tableau écrit / oral
            int nbLignes = Math.max(ecrites.size(), orales.size() + (epsOral ? 1 : 0));
            float[] largeurs = {16, 9, 9, 9, 7, 16, 9, 9, 9, 7};
            PdfPTable t = new PdfPTable(largeurs);
            t.setWidthPercentage(100);

            t.addCell(headerBandCell("EPREUVES ECRITES", 5));
            t.addCell(headerBandCell("EPREUVES ORALES", 5));

            for (int k = 0; k < 2; k++) {
                t.addCell(subHeader("", Element.ALIGN_CENTER));
                t.addCell(subHeader("Note\n/20", Element.ALIGN_CENTER));
                t.addCell(subHeader("Coeff.", Element.ALIGN_CENTER));
                t.addCell(subHeader("Pts\nobte.", Element.ALIGN_CENTER));
                t.addCell(subHeader("sur", Element.ALIGN_CENTER));
            }

            int totalCoeffEcrit = 0;
            int totalCoeffOral = 0;
            for (Epreuve e : ecrites) totalCoeffEcrit += e.coeff;
            for (Epreuve e : orales) totalCoeffOral += e.coeff;

            for (int i = 0; i < nbLignes; i++) {
                // Colonne écrit
                if (i < ecrites.size()) {
                    Epreuve e = ecrites.get(i);
                    t.addCell(subLabelCell(e.libelle));
                    t.addCell(dataCell("", Element.ALIGN_CENTER));
                    t.addCell(dataCell(String.valueOf(e.coeff), Element.ALIGN_CENTER));
                    t.addCell(dataCell("", Element.ALIGN_CENTER));
                    t.addCell(dataCell(String.valueOf(e.coeff * 20), Element.ALIGN_CENTER));
                } else {
                    for (int j = 0; j < 5; j++) {
                        t.addCell(dataCell("", Element.ALIGN_CENTER));
                    }
                }
                // Colonne oral
                if (i < orales.size()) {
                    Epreuve e = orales.get(i);
                    t.addCell(subLabelCell(e.libelle));
                    t.addCell(dataCell("", Element.ALIGN_CENTER));
                    t.addCell(dataCell(String.valueOf(e.coeff), Element.ALIGN_CENTER));
                    t.addCell(dataCell("", Element.ALIGN_CENTER));
                    t.addCell(dataCell(String.valueOf(e.coeff * 20), Element.ALIGN_CENTER));
                } else if (epsOral && i == orales.size()) {
                    t.addCell(subLabelCell("EPS"));
                    t.addCell(dataCell("", Element.ALIGN_CENTER));
                    t.addCell(dataCell("±", Element.ALIGN_CENTER));
                    t.addCell(dataCell("", Element.ALIGN_CENTER));
                    t.addCell(dataCell("", Element.ALIGN_CENTER));
                } else {
                    for (int j = 0; j < 5; j++) {
                        t.addCell(dataCell("", Element.ALIGN_CENTER));
                    }
                }
            }

            // Totaux
            t.addCell(totalCell("TOTAL EC.", 1));
            t.addCell(dataCell("", Element.ALIGN_CENTER));
            t.addCell(dataCell(String.valueOf(totalCoeffEcrit), Element.ALIGN_CENTER));
            t.addCell(dataCell("", Element.ALIGN_CENTER));
            t.addCell(dataCell(String.valueOf(totalCoeffEcrit * 20), Element.ALIGN_CENTER));
            t.addCell(totalCell("TOTAL OR.", 1));
            t.addCell(dataCell("", Element.ALIGN_CENTER));
            t.addCell(dataCell(String.valueOf(totalCoeffOral), Element.ALIGN_CENTER));
            t.addCell(dataCell("", Element.ALIGN_CENTER));
            t.addCell(dataCell(String.valueOf(totalCoeffOral * 20), Element.ALIGN_CENTER));

            if (totalGeneralFormule != null) {
                int totalGeneral = (totalCoeffEcrit + totalCoeffOral) * 20;
                PdfPCell tg = new PdfPCell(new Phrase(
                        "TOTAL GENERAL : " + totalGeneralFormule + " = " + pointilles(10)
                                + " / " + totalGeneral, F_BOLD));
                tg.setColspan(10);
                tg.setBorderWidth(0.75f);
                tg.setPadding(5f);
                tg.setHorizontalAlignment(Element.ALIGN_CENTER);
                t.addCell(tg);
            }
            document.add(t);

            // Pied de page
            Paragraph mention = new Paragraph(mentionBas, F_BOLD);
            mention.setAlignment(Element.ALIGN_CENTER);
            mention.setSpacingBefore(10);
            document.add(mention);
            Paragraph fait = new Paragraph("Fait à " + pointilles(20) + ", le " + pointilles(20) + " 19........", F_NORMAL);
            fait.setAlignment(Element.ALIGN_CENTER);
            fait.setSpacingBefore(10);
            document.add(fait);
            Paragraph signature = new Paragraph("Prénom (s), Nom et signature du Président du Jury", F_PETIT);
            signature.setAlignment(Element.ALIGN_CENTER);
            signature.setSpacingBefore(4);
            document.add(signature);
            Paragraph cachet = new Paragraph("Cachet obligatoire", F_NORMAL);
            cachet.setSpacingBefore(8);
            document.add(cachet);

            document.close();
            return baos.toByteArray();
        } catch (DocumentException | java.io.IOException e) {
            throw new RuntimeException("Erreur lors de la génération du PDF", e);
        }
    }
}
