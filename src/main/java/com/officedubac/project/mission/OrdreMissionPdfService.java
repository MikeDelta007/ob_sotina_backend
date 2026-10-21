package com.officedubac.project.mission;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.text.Normalizer;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

// Génère le document "ORDRE DE MISSION" — reproduction fidèle (mots, lignes, polices, cadre) du
// modèle Word officiel fourni (ORDRE DE MISSION-OB-Delivrance-diplome.docx) : un bloc complet par
// agent (une ligne de mission), avec l'en-tête République/drapeau/Ministère répété sur chaque page.
@Slf4j
@Service
public class OrdreMissionPdfService {

    private static final DateTimeFormatter FORMAT_DATE = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.FRENCH);
    private static final Pattern DIACRITIQUES = Pattern.compile("\\p{M}");

    // Tahoma est la police du modèle Word pour l'en-tête ; non redistribuable (police Microsoft),
    // on la charge donc depuis le système si disponible (poste Windows), avec repli sur Helvetica.
    private static final String TAHOMA_REGULIER = "C:/Windows/Fonts/tahoma.ttf";
    private static final String TAHOMA_GRAS = "C:/Windows/Fonts/tahomabd.ttf";

    public byte[] genererPdf(List<OrdreMission> ordres) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            // Marges du modèle Word (section) : haut 720, droite 849, bas 720, gauche 1411 twips.
            Document doc = new Document(PageSize.A4, 70.5f, 42.5f, 36f, 60f);
            PdfWriter writer = PdfWriter.getInstance(doc, baos);
            writer.setPageEvent(new PiedDePageEvent());
            doc.open();

            Font fTitre = FontFactory.getFont(FontFactory.TIMES_BOLD, 26);
            Font fNorm = FontFactory.getFont(FontFactory.TIMES_ROMAN, 12);
            Font fBold = FontFactory.getFont(FontFactory.TIMES_BOLD, 12);
            Font fHeaderNorm = policeEntete(false);
            Font fHeaderBold = policeEntete(true);

            boolean premier = true;
            for (OrdreMission ordre : ordres) {
                for (LigneMission ligne : ordre.getLignes()) {
                    if (!premier) doc.newPage();
                    premier = false;
                    genererEntete(doc, fHeaderNorm, fHeaderBold);
                    genererBloc(doc, ordre, ligne, fTitre, fNorm, fBold);
                }
            }

            doc.close();
            return baos.toByteArray();
        } catch (Exception e) {
            log.error("Erreur génération PDF ordre de mission", e);
            throw new RuntimeException("Erreur génération PDF ordre de mission", e);
        }
    }

    private Font policeEntete(boolean gras) {
        try {
            String chemin = gras ? TAHOMA_GRAS : TAHOMA_REGULIER;
            BaseFont bf = BaseFont.createFont(chemin, BaseFont.CP1252, BaseFont.EMBEDDED);
            return new Font(bf, 13);
        } catch (Exception e) {
            log.warn("Tahoma indisponible, repli sur Helvetica pour l'en-tête", e);
            return FontFactory.getFont(gras ? FontFactory.HELVETICA_BOLD : FontFactory.HELVETICA, 11);
        }
    }

    // ══════════════════════
    // EN-TÊTE — identique sur chaque page : République/drapeau/Ministère à gauche,
    // référence + date à droite (tableau 2 colonnes, comme le modèle Word).
    // ══════════════════════
    private void genererEntete(Document doc, Font fNorm, Font fBold) throws DocumentException {
        PdfPTable entete = new PdfPTable(new float[]{55f, 45f});
        entete.setWidthPercentage(100);

        Paragraph gauche = new Paragraph();
        gauche.setAlignment(Element.ALIGN_CENTER);
        gauche.add(new Chunk("REPUBLIQUE DU SENEGAL\n", fBold));
        gauche.add(new Chunk("________\n", fBold));
        try {
            ClassPathResource drapeauFile = new ClassPathResource("images/drapeau.png");
            if (drapeauFile.exists()) {
                Image drapeau = Image.getInstance(drapeauFile.getInputStream().readAllBytes());
                drapeau.scaleToFit(50f, 33f);
                drapeau.setAlignment(Image.ALIGN_CENTER);
                gauche.add(drapeau);
                gauche.add(Chunk.NEWLINE);
            }
        } catch (Exception e) {
            log.warn("Drapeau non trouvé (images/drapeau.png)", e);
        }
        gauche.add(new Chunk("Un Peuple – Un But – Une Foi\n", fNorm));
        gauche.add(new Chunk("Ministère de l'Enseignement supérieur, de la Recherche et de l'Innovation\n", fNorm));
        gauche.add(new Chunk("________\n", fBold));
        gauche.add(new Chunk("OFFICE DU BACCALAUREAT", fBold));

        PdfPCell celluleGauche = new PdfPCell(gauche);
        celluleGauche.setBorder(Rectangle.NO_BORDER);
        celluleGauche.setVerticalAlignment(Element.ALIGN_MIDDLE);
        entete.addCell(celluleGauche);

        Paragraph droite = new Paragraph();
        droite.setAlignment(Element.ALIGN_RIGHT);
        droite.add(new Chunk("N°………………MESRI/SG/OB/DOB/kt\n", fNorm));
        droite.add(new Chunk("Dakar, le " + FORMAT_DATE.format(LocalDate.now()), fNorm));

        PdfPCell celluleDroite = new PdfPCell(droite);
        celluleDroite.setBorder(Rectangle.NO_BORDER);
        celluleDroite.setVerticalAlignment(Element.ALIGN_TOP);
        entete.addCell(celluleDroite);

        doc.add(entete);
        doc.add(new Paragraph(" "));
    }

    private void genererBloc(Document doc, OrdreMission ordre, LigneMission ligne,
                              Font fTitre, Font fNorm, Font fBold) throws DocumentException {
        String[] civiliteEtNom = separerCiviliteEtNom(ligne.getAgentNom());
        String civilite = civiliteEtNom[0];
        String nomSeul = civiliteEtNom[1];

        Paragraph titre = new Paragraph("ORDRE DE MISSION", fTitre);
        titre.setAlignment(Element.ALIGN_CENTER);
        doc.add(titre);
        doc.add(new Paragraph(" "));

        // "Il est ordonné à [Civilité] [Nom (gras)]" — bordure haut + gauche
        Phrase ordonneA = new Phrase();
        ordonneA.add(new Chunk("Il est ordonné à " + civilite + " ", fNorm));
        ordonneA.add(new Chunk(nomSeul, fBold));
        paragrapheBorde(doc, ordonneA, true, true, false, 2.0f);

        // "De Nationalité SENEGALAISE " — bordure haut + gauche
        Phrase nationalite = new Phrase();
        nationalite.add(new Chunk("De Nationalité ", fNorm));
        nationalite.add(new Chunk("SENEGALAISE ", fBold));
        paragrapheBorde(doc, nationalite, true, true, false, 2.0f);

        // "Qualité et grade :" + valeur (gras) — sans bordure
        ligneChamp(doc, "Qualité et grade :", ligne.getAgentQualite(), fNorm, fBold, 2.0f);

        // "Indice" ... "Groupe" — libellés statiques du modèle, sans valeur
        Paragraph indiceGroupe = new Paragraph("Indice\t\t\t\t\t\tGroupe", fNorm);
        indiceGroupe.setMultipliedLeading(2.0f);
        doc.add(indiceGroupe);

        // "De se rendre à (VILLES) :" + trajet (gras)
        String villes = String.join("––", ordre.getVilleNoms());
        ligneChamp(doc, "De se rendre à (VILLES) :", villes, fNorm, fBold, 2.0f);

        // "Objet de la mission : " + motif (gras)
        ligneChamp(doc, "Objet de la mission : ", ordre.getMotifLibelle(), fNorm, fBold, 2.0f);

        // "Moyen de transport :" + immatriculation ou "Transport en commun" (gras)
        String moyenTransport = ligne.getMoyenTransport() == MoyenTransport.TRANSPORT_COMMUN
                ? "Transport en commun"
                : (ligne.getVoitureImmatriculation() != null ? ligne.getVoitureImmatriculation() : "—");
        ligneChamp(doc, "Moyen de transport :", moyenTransport, fNorm, fBold, 2.0f);

        // "Date de départ :" + date (gras)
        ligneChamp(doc, "Date de départ :", dateEnMajuscules(ordre.getDateDebut()), fNorm, fBold, 2.0f);

        // "Date de retour :" + date (gras)
        ligneChamp(doc, "Date de retour :", dateEnMajuscules(ordre.getDateFin()), fNorm, fBold, 2.0f);

        // Champs laissés vides dans le modèle — reproduits tels quels
        Paragraph frais = new Paragraph("Frais de transport : ", fNorm);
        frais.setMultipliedLeading(2.0f);
        doc.add(frais);

        Paragraph imputation = new Paragraph("Imputation Budgétaire :", fNorm);
        imputation.setMultipliedLeading(1.5f);
        doc.add(imputation);

        Paragraph indemnite = new Paragraph("Indemnité de déplacement : Office du Baccalauréat", fNorm);
        indemnite.setMultipliedLeading(1.5f);
        doc.add(indemnite);

        doc.add(new Paragraph(" "));

        // "Les autorités civiles et militaires des départements traversés sont priées de faciliter
        // à [Civilité] [Nom (gras)] l'accomplissement de son voyage." — bordure gauche + bas
        Phrase facilite = new Phrase();
        facilite.add(new Chunk("\tLes autorités civiles et militaires des ", fNorm));
        facilite.add(new Chunk("départements ", fBold));
        facilite.add(new Chunk("traversés sont priées de faciliter à " + civilite + " ", fNorm));
        facilite.add(new Chunk(nomSeul, fBold));
        facilite.add(new Chunk(" l'accomplissement de son voyage.", fNorm));
        paragrapheBorde(doc, facilite, false, true, true, 2.0f);

        doc.add(new Paragraph(" "));

        // Signature : "Le Directeur" puis le nom, centrés dans la moitié droite de la page
        Paragraph directeurLabel = new Paragraph("Le Directeur", fBold);
        directeurLabel.setAlignment(Element.ALIGN_RIGHT);
        directeurLabel.setIndentationLeft(doc.getPageSize().getWidth() * 0.4f);
        doc.add(directeurLabel);
        doc.add(new Paragraph(" "));

        Paragraph directeurNom = new Paragraph("Cheikh Ahmadou Bamba GUEYE", fBold);
        directeurNom.setAlignment(Element.ALIGN_RIGHT);
        directeurNom.setIndentationLeft(doc.getPageSize().getWidth() * 0.4f);
        doc.add(directeurNom);
    }

    // Découpe "Monsieur Fallou DIOUF" en {"Monsieur", "Fallou DIOUF"} — la civilité n'est jamais
    // en gras dans le modèle, le nom l'est toujours.
    private String[] separerCiviliteEtNom(String agentNom) {
        if (agentNom == null) return new String[]{"", ""};
        int espace = agentNom.indexOf(' ');
        if (espace < 0) return new String[]{"", agentNom};
        return new String[]{agentNom.substring(0, espace), agentNom.substring(espace + 1)};
    }

    // "17 août 2026" -> "17 AOUT 2026" (majuscules sans accents, comme le modèle).
    private String dateEnMajuscules(LocalDate date) {
        String formate = FORMAT_DATE.format(date).toUpperCase(Locale.FRENCH);
        String sansAccents = Normalizer.normalize(formate, Normalizer.Form.NFD);
        return DIACRITIQUES.matcher(sansAccents).replaceAll("");
    }

    private void ligneChamp(Document doc, String libelle, String valeur, Font fNorm, Font fBold, float interligne) throws DocumentException {
        Paragraph p = new Paragraph();
        p.add(new Chunk(libelle + "\t", fNorm));
        p.add(new Chunk(valeur != null ? valeur : "—", fBold));
        p.setMultipliedLeading(interligne);
        doc.add(p);
    }

    // Reproduit les bordures par paragraphe (pBdr) du modèle Word — une seule cellule de tableau
    // avec les côtés voulus, OpenPDF ne gérant pas les bordures de paragraphe directement.
    private void paragrapheBorde(Document doc, Phrase contenu, boolean haut, boolean gauche, boolean bas, float interligne) throws DocumentException {
        Paragraph p = new Paragraph();
        p.add(contenu);
        p.setMultipliedLeading(interligne);

        PdfPTable t = new PdfPTable(1);
        t.setWidthPercentage(100);
        PdfPCell cell = new PdfPCell();
        cell.addElement(p);
        cell.setUseVariableBorders(true);
        cell.setBorderWidthTop(haut ? 1.2f : 0f);
        cell.setBorderWidthLeft(gauche ? 1.2f : 0f);
        cell.setBorderWidthBottom(bas ? 1.2f : 0f);
        cell.setBorderWidthRight(0f);
        cell.setPaddingLeft(6f);
        cell.setPaddingTop(2f);
        cell.setPaddingBottom(2f);
        t.addCell(cell);
        doc.add(t);
    }

    private static class PiedDePageEvent extends PdfPageEventHelper {
        private static final Font POLICE = FontFactory.getFont(FontFactory.HELVETICA, 8);
        private static final String TEXTE = "Office du Baccalauréat – Université Cheikh Anta Diop – BP : 5005, "
                + "Dakar, Fann Sénégal – Email : officedubac@ucad.edu.sn - Site internet : "
                + "www.officedubac.sn / https://extrantsbac.ucad.sn/";

        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            try {
                ColumnText ct = new ColumnText(writer.getDirectContent());
                ct.setSimpleColumn(document.left(), document.bottom() - 40, document.right(), document.bottom() - 5);
                ct.setAlignment(Element.ALIGN_CENTER);
                ct.setLeading(9f);
                ct.addText(new Phrase(TEXTE, POLICE));
                ct.go();
            } catch (DocumentException e) {
                log.warn("Erreur pied de page PDF", e);
            }
        }
    }
}
