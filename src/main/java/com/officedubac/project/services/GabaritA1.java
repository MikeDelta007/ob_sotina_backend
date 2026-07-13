package com.officedubac.project.services;

import com.officedubac.project.dto.ReleveNotePdfData;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.officedubac.project.dto.LigneNoteResponse;
import com.officedubac.project.dto.ReleveNotePdfData;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class GabaritA1
{
    private static final Font F_ENTETE_UNIV = new Font(Font.HELVETICA, 9, Font.NORMAL);
    private static final Font F_ENTETE_BOLD = new Font(Font.HELVETICA, 9, Font.BOLD);
    private static final Font F_TITRE = new Font(Font.HELVETICA, 13, Font.BOLD);
    private static final Font F_SESSION = new Font(Font.HELVETICA, 8, Font.NORMAL);
    private static final Font F_OPTION_BOX = new Font(Font.HELVETICA, 11, Font.BOLD);
    private static final Font F_NORMAL = new Font(Font.HELVETICA, 8, Font.NORMAL);
    private static final Font F_BOLD = new Font(Font.HELVETICA, 8, Font.BOLD);
    private static final Font F_PETIT = new Font(Font.HELVETICA, 6.5f, Font.NORMAL);
    private static final Font F_PETIT_ITALIC = new Font(Font.HELVETICA, 6.5f, Font.ITALIC);
    private static final Font F_TABLE_HEADER = new Font(Font.HELVETICA, 7, Font.BOLD);
    private static final Font F_TABLE_DATA = new Font(Font.HELVETICA, 7.5f, Font.NORMAL);

    // Colonnes du tableau de chaque groupe : Matière | Sous-item | Note/20 | Coeff | Points obtenus | Sur
    private static final float[] LARGEURS_GROUPE1 = {16, 22, 12, 10, 14, 10};
    private static final float[] LARGEURS_GROUPE2 = {26, 12, 10, 8, 14, 10};

    public byte[] genererReleveA1() {
        Document document = new Document(PageSize.A4, 40, 40, 40, 40);

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter.getInstance(document, baos);
            document.open();

            document.add(buildEntete());
            document.add(espace(4));
            document.add(buildEncadreCandidat());
            document.add(espace(4));
            document.add(buildTableauPrincipal());
            document.add(espace(4));
            document.add(buildPiedDePage());

            document.close();
            return baos.toByteArray();
        } catch (DocumentException | java.io.IOException e) {
            throw new RuntimeException("Erreur lors de la génération du PDF", e);
        }
    }

    private Paragraph espace(float taille) {
        Paragraph p = new Paragraph(" ");
        p.setSpacingAfter(taille);
        return p;
    }

    // ---------- En-tête ----------

    private PdfPTable buildEntete() throws DocumentException {
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
        optionBox.setWidthPercentage(60);
        optionBox.setHorizontalAlignment(Element.ALIGN_RIGHT);
        PdfPCell optionCell = new PdfPCell(new Phrase("OPTION A1", F_OPTION_BOX));
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

    // ---------- Encadré candidat ----------

    private PdfPTable buildEncadreCandidat() throws DocumentException {
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

    // ---------- Tableau principal : 1er groupe | 2ème groupe ----------

    private PdfPTable buildTableauPrincipal() throws DocumentException {
        PdfPTable racine = new PdfPTable(new float[]{50, 50});
        racine.setWidthPercentage(100);

        PdfPCell cellGroupe1 = new PdfPCell(buildGroupe1());
        cellGroupe1.setPadding(0);
        cellGroupe1.setBorder(Rectangle.NO_BORDER);
        racine.addCell(cellGroupe1);

        PdfPCell cellGroupe2 = new PdfPCell(buildGroupe2());
        cellGroupe2.setPadding(0);
        cellGroupe2.setBorder(Rectangle.NO_BORDER);
        racine.addCell(cellGroupe2);

        return racine;
    }

    // ----- Bloc "1er GROUPE D'EPREUVES" — 6 colonnes fixes -----
    // Colonne 0: Matière (rowspan groupe) | 1: Sous-item | 2: Note/20 | 3: Coeff | 4: Points obtenus | 5: Sur

    private PdfPTable buildGroupe1() throws DocumentException {
        PdfPTable t = new PdfPTable(LARGEURS_GROUPE1);
        t.setWidthPercentage(100);

        t.addCell(headerBandCell("1er GROUPE D'EPREUVES", 6));

        // Sous-en-tête : "(en nombres entiers)" chevauche col 0+1, puis 4 en-têtes de colonne
        PdfPCell entete01 = subHeader("(en nombres entiers)", Element.ALIGN_LEFT);
        entete01.setColspan(2);
        t.addCell(entete01);
        t.addCell(subHeader("Note\nsur 20", Element.ALIGN_CENTER));
        t.addCell(subHeader("Coeff.", Element.ALIGN_CENTER));
        t.addCell(subHeader("Points\nobtenus", Element.ALIGN_CENTER));
        t.addCell(subHeader("sur", Element.ALIGN_CENTER));

        ajouterLigneGroupe(t, "Français", "écrit", 3, true);
        ajouterLigneGroupe(t, null, "oral", 1, false);

        ajouterLigneGroupe(t, "Ecrit", "Philosophie", 4, true);
        ajouterLigneGroupe(t, null, "Latin ou Grec", 3, false);

        ajouterLigneGroupe(t, "Oral", "Hist. et Géo", 3, true);
        ajouterLigneGroupe(t, null, "L. V.", 2, false);

        // 1er TOTAL : colspan 4 (matière+sous-item+note+coeff) + points obtenus (vide) + sur (total)
        int totalCoeff1 = 3 + 1 + 4 + 3 + 3 + 2; // = 16
        PdfPCell total1Label = totalCell("1er TOTAL  ———>", 4);
        t.addCell(total1Label);
        t.addCell(dataCell("", Element.ALIGN_CENTER));
        t.addCell(dataCell(String.valueOf(totalCoeff1 * 20), Element.ALIGN_CENTER));

        // Bandeau "Points supplémentaires..."
        PdfPCell texteSuppl = new PdfPCell(new Phrase(
                "Points supplémentaires à ne prendre en considération que pour les candidats "
                        + "définitivement admis après les épreuves du 1er Groupe (n'entrent en ligne de "
                        + "compte que pour les mentions BIEN ou TRES BIEN)", F_PETIT_ITALIC));
        texteSuppl.setColspan(6);
        texteSuppl.setBorderWidth(0.5f);
        texteSuppl.setPadding(4f);
        t.addCell(texteSuppl);

        // "points au dessus de la moyenne" (rowspan 3, col0) + libellé (colspan 3, col1-3) + 2 cellules vides (col4-5)
        PdfPCell libellePts = new PdfPCell(new Phrase("points au\ndessus de la\nmoyenne", F_PETIT));
        libellePts.setRowspan(3);
        libellePts.setBorderWidth(0.5f);
        libellePts.setPadding(4f);
        libellePts.setVerticalAlignment(Element.ALIGN_MIDDLE);
        t.addCell(libellePts);

        ajouterLignePointsSupp(t, "Educ. Physique");
        ajouterLignePointsSupp(t, "Epr. Langue");
        ajouterLignePointsSupp(t, "Fac. Des. Mus. Couture");

        // 2e TOTAL : même structure que 1er TOTAL
        PdfPCell total2Label = totalCell("2e TOTAL  ———>", 4);
        t.addCell(total2Label);
        t.addCell(dataCell("", Element.ALIGN_CENTER));
        t.addCell(dataCell(String.valueOf(totalCoeff1 * 20), Element.ALIGN_CENTER));

        return t;
    }

    /**
     * Ajoute une ligne du groupe 1 : 6 cellules exactement.
     * Si libelleGroupe != null, il occupe la colonne 0 avec rowspan=2 (ne pas ré-ajouter à la ligne suivante).
     * Si libelleGroupe == null, la colonne 0 est déjà couverte par le rowspan précédent : on n'ajoute PAS de cellule pour elle.
     */
    private void ajouterLigneGroupe(PdfPTable t, String libelleGroupe, String sousItem, int coeff, boolean premiereDuGroupe) {
        if (premiereDuGroupe) {
            PdfPCell label = new PdfPCell(new Phrase(libelleGroupe, F_BOLD));
            label.setRowspan(2);
            label.setBorderWidth(0.5f);
            label.setPadding(4f);
            label.setVerticalAlignment(Element.ALIGN_MIDDLE);
            t.addCell(label);
        }
        // Si !premiereDuGroupe, la cellule colonne 0 est déjà consommée par le rowspan : on NE L'AJOUTE PAS.

        t.addCell(subLabelCell(sousItem));
        t.addCell(dataCell("", Element.ALIGN_CENTER));               // Note/20 : vide, à saisir
        t.addCell(dataCell(String.valueOf(coeff), Element.ALIGN_CENTER));
        t.addCell(dataCell("", Element.ALIGN_CENTER));               // Points obtenus : vide, calculé
        t.addCell(dataCell(String.valueOf(coeff * 20), Element.ALIGN_CENTER)); // Sur = coeff × 20
    }

    private void ajouterLignePointsSupp(PdfPTable t, String libelle) {
        PdfPCell label = dataCell(libelle, Element.ALIGN_LEFT);
        label.setColspan(3);
        t.addCell(label);
        t.addCell(dataCell("", Element.ALIGN_CENTER));
        t.addCell(dataCell("", Element.ALIGN_CENTER));
    }

    // ----- Bloc "2ème GROUPE D'EPREUVES" — 6 colonnes fixes -----
    // Colonne 0: Matière | 1: Note/20 | 2: Coeff | 3: X | 4: Points obtenus | 5: Sur

    private PdfPTable buildGroupe2() throws DocumentException {
        PdfPTable t = new PdfPTable(LARGEURS_GROUPE2);
        t.setWidthPercentage(100);

        t.addCell(headerBandCell("2ème GROUPE D'EPREUVES", 6));

        t.addCell(subHeader("(en nombres entiers)", Element.ALIGN_LEFT));
        t.addCell(subHeader("Note\nsur 20", Element.ALIGN_CENTER));
        t.addCell(subHeader("Coeff.", Element.ALIGN_CENTER));
        t.addCell(subHeader("X", Element.ALIGN_CENTER));
        t.addCell(subHeader("Points\nobtenus", Element.ALIGN_CENTER));
        t.addCell(subHeader("sur", Element.ALIGN_CENTER));

        // REPORT du 1er TOTAL : colspan 4 (matière+note+coeff+x) + points obtenus (vide) + sur (report)
        PdfPCell report = totalCell("REPORT du 1er TOTAL  ———>", 4);
        t.addCell(report);
        t.addCell(dataCell("", Element.ALIGN_CENTER));
        t.addCell(dataCell("320", Element.ALIGN_CENTER));

        ajouterLigneGroupe2(t, "Grec ou Latin...................", 2);
        ajouterLigneGroupe2(t, "Mathématiques...................", 2);

        // EPR. ORALES de CONTROLE - bandeau titre
        PdfPCell titreControle = new PdfPCell(new Phrase("EPR. ORALES de CONTROLE.", F_BOLD));
        titreControle.setColspan(6);
        titreControle.setBorderWidth(0.5f);
        titreControle.setPadding(4f);
        t.addCell(titreControle);

        // ligne des lettres (a)(b)(c)(d)(e) — 6 cellules exactement
        t.addCell(subHeader("(a)\nMATIERES CHOISIES", Element.ALIGN_CENTER));
        t.addCell(subHeader("(b)\nRappel\ndes pts obt.\nau 1er Gr.", Element.ALIGN_CENTER));
        t.addCell(subHeader("(c)\nNouvelle\nnote sur 20", Element.ALIGN_CENTER));
        t.addCell(subHeader("(d)\nPts. obts\nà l'épreuve\nde contrôle", Element.ALIGN_CENTER));
        PdfPCell diff = subHeader("(e)\nDifférence\nEN PLUS", Element.ALIGN_CENTER);
        diff.setColspan(2);
        t.addCell(diff);

        // 2 lignes vides à remplir (matières choisies) — 6 cellules chacune (dernière colspan=2)
        for (int i = 0; i < 2; i++) {
            t.addCell(dataCell("", Element.ALIGN_LEFT));
            t.addCell(dataCell("", Element.ALIGN_CENTER));
            t.addCell(dataCell("", Element.ALIGN_CENTER));
            t.addCell(dataCell("", Element.ALIGN_CENTER));
            PdfPCell diffVide = dataCell("", Element.ALIGN_CENTER);
            diffVide.setColspan(2);
            t.addCell(diffVide);
        }

        // EPR. FACULTATIVES — ligne 1
        PdfPCell fac = new PdfPCell(new Phrase("EPR. FACULTATIVES", F_BOLD));
        fac.setColspan(2);
        fac.setBorderWidth(0.5f);
        fac.setPadding(4f);
        t.addCell(fac);
        PdfPCell facLangue = new PdfPCell(new Phrase("Langue ...................", F_TABLE_DATA));
        facLangue.setColspan(4);
        facLangue.setBorderWidth(0.5f);
        facLangue.setPadding(4f);
        t.addCell(facLangue);

        // EPR. FACULTATIVES — ligne 2
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

        // TOTAL PROVISOIRE : colspan 4 + vide + vide
        PdfPCell totalProv = totalCell("TOTAL PROVISOIRE :", 4);
        t.addCell(totalProv);
        t.addCell(dataCell("", Element.ALIGN_CENTER));
        t.addCell(dataCell("", Element.ALIGN_CENTER));

        // Education Physique : colspan 2 + colspan 2 + colspan 2
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

        // TOTAL DEFINITIF : colspan 4 + vide + 400
        PdfPCell totalDef = totalCell("TOTAL DEFINITIF  ———>", 4);
        t.addCell(totalDef);
        t.addCell(dataCell("", Element.ALIGN_CENTER));
        t.addCell(dataCell("400", Element.ALIGN_CENTER));

        return t;
    }

    private void ajouterLigneGroupe2(PdfPTable t, String libelle, int coeff) {
        t.addCell(dataCell(libelle, Element.ALIGN_LEFT));
        t.addCell(dataCell("", Element.ALIGN_CENTER));                // Note/20 : vide
        t.addCell(dataCell(String.valueOf(coeff), Element.ALIGN_CENTER));
        t.addCell(dataCell("", Element.ALIGN_CENTER));                // X : vide
        t.addCell(dataCell("", Element.ALIGN_CENTER));                // Points obtenus : vide
        t.addCell(dataCell(String.valueOf(coeff * 20), Element.ALIGN_CENTER)); // Sur = coeff × 20
    }

    // ---------- Pied de page ----------

    private PdfPTable buildPiedDePage() throws DocumentException {
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
        droite.addElement(new Paragraph("ADMIS avec le mention PASSABLE", F_BOLD));
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

    // ---------- Helpers ----------

    private String pointilles(int longueur) {
        return ".".repeat(longueur);
    }

    private PdfPCell noBorderCell() {
        PdfPCell c = new PdfPCell();
        c.setBorder(Rectangle.NO_BORDER);
        return c;
    }

    private PdfPCell borderedCell(String texte, Font font, int align) {
        PdfPCell c = new PdfPCell(new Phrase(texte, font));
        c.setBorderWidth(0.5f);
        c.setHorizontalAlignment(align);
        c.setPadding(3f);
        return c;
    }

    private PdfPCell headerBandCell(String texte, int colspan) {
        PdfPCell c = new PdfPCell(new Phrase(texte, F_TABLE_HEADER));
        c.setColspan(colspan);
        c.setBackgroundColor(new Color(230, 230, 230));
        c.setBorderWidth(0.75f);
        c.setPadding(4f);
        c.setHorizontalAlignment(Element.ALIGN_CENTER);
        return c;
    }

    private PdfPCell subHeader(String texte, int align) {
        PdfPCell c = new PdfPCell(new Phrase(texte, F_PETIT));
        c.setBorderWidth(0.5f);
        c.setPadding(3f);
        c.setHorizontalAlignment(align);
        return c;
    }

    private PdfPCell subLabelCell(String texte) {
        PdfPCell c = new PdfPCell(new Phrase(texte, F_TABLE_DATA));
        c.setBorderWidth(0.5f);
        c.setPadding(3f);
        return c;
    }

    private PdfPCell dataCell(String texte, int align) {
        PdfPCell c = new PdfPCell(new Phrase(texte, F_TABLE_DATA));
        c.setBorderWidth(0.5f);
        c.setPadding(3f);
        c.setFixedHeight(16f);
        c.setHorizontalAlignment(align);
        c.setVerticalAlignment(Element.ALIGN_MIDDLE);
        return c;
    }

    private PdfPCell totalCell(String texte, int colspan) {
        PdfPCell c = new PdfPCell(new Phrase(texte, F_BOLD));
        c.setColspan(colspan);
        c.setBorderWidth(0.75f);
        c.setPadding(4f);
        c.setBackgroundColor(new Color(245, 245, 245));
        return c;
    }
}
