package com.officedubac.project.controllers;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.*;
import com.officedubac.project.models.*;
import com.officedubac.project.repository.*;
import com.officedubac.project.services.CandidatService;
import com.officedubac.project.services.TirageJuryMatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.*;

import java.awt.*;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@CrossOrigin("*")
@RestController
@Slf4j
@RequestMapping("/api/v1/pdf")
@RequiredArgsConstructor
@Tag(name="PDF Controller", description = "Endpoints responsables de la gestion des PDF")
public class PdfController
{


    @Autowired
    private FusionRepartitionTirageRepository repository;

    @Autowired
    private RegleMatiereRepository repo;


    @Operation(summary = "Génération de l'étiquette de table - Format A4 Paysage")
    @GetMapping("/generate-etiquette-paysage")
    public void generateEtiquettes(
            @RequestParam(value = "matiere") String matiere,
            @RequestParam(value = "groupe", required = false) String groupe,
            HttpServletResponse response) throws IOException, DocumentException {

        // ================= NORMALISATION MATIERE =================
        if (matiere == null || matiere.trim().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        log.info("MATIERE DEMANDEE = {}", matiere);

        List<FusionRepartitionTirage> list = repository.findAll();
        if (list.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            return;
        }

        // ================= REGLES =================
        List<RegleMatiere> regles = repo.findAll();
        Map<String, RegleMatiere> regleParCode = regles.stream()
                .collect(Collectors.toMap(
                        r -> r.getCode().toUpperCase(),
                        r -> r,
                        (r1, r2) -> r1
                ));

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "inline; filename=etiquettes_bac.pdf");

        Document document = new Document(PageSize.A4.rotate(), 36f, 36f, 36f, 36f);
        PdfWriter.getInstance(document, response.getOutputStream());
        document.open();

        // ================= POLICES =================
        Font helv10 = FontFactory.getFont(FontFactory.HELVETICA, 10, Font.NORMAL);
        Font helv12Bold = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 60);
        Font helv14 = FontFactory.getFont(FontFactory.HELVETICA, 17, Font.BOLD);
        Font helv22 = FontFactory.getFont(FontFactory.HELVETICA, 22, Font.NORMAL);
        Font helv24Bold = FontFactory.getFont(FontFactory.HELVETICA, 24, Font.BOLD);
        Font helv16Bold = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
        Font helv16 = FontFactory.getFont(FontFactory.HELVETICA, 16);
        Font helv26Bold = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 30);

        Image logo = Image.getInstance(
                new ClassPathResource("images/logo-UCAD_.png")
                        .getInputStream().readAllBytes());
        logo.scaleToFit(70f, 70f);

        // ================= PARCOURS OPTIMISÉ =================
        for (FusionRepartitionTirage data : list) {

            if (data.getMatieres() == null) {
                continue;
            }

            // ✅ ACCÈS DIRECT À LA MATIÈRE (ULTRA IMPORTANT)
            GroupeMatiere gm = data.getMatieres().get(matiere);

            if (gm == null) {
                continue;
            }

            // ================= EFFECTIF =================
            Double effectif = null;
            String grp = null;

            if ("1ERGRP".equalsIgnoreCase(groupe)) {
                effectif = gm.getPremierGroupe();
                grp = "PREMIER GROUPE";
            } else if ("2NDGRP".equalsIgnoreCase(groupe)) {
                effectif = gm.getSecondGroupe();
                grp = "SECOND GROUPE";
            }

            if (effectif == null || effectif <= 0) {
                continue;
            }

            // ================= REGLE =================
            RegleMatiere regle = regleParCode.get(matiere);
            if (regle == null) {
                log.warn("Aucune règle trouvée pour {}", matiere);
                continue;
            }

            String series = (regle.getSeries() != null && !regle.getSeries().isEmpty())
                    ? String.join(" - ", regle.getSeries())
                    : "";

            String date = Optional.ofNullable(regle.getDate1()).orElse("");
            String horaire = Optional.ofNullable(regle.getHeure1()).orElse("");

            RegleMatiere regle_ = repo.findByCode(matiere);

            String libelleNormalise = Optional.ofNullable(regle_)
                    .map(RegleMatiere::getValeur)
                    .orElse(matiere);

            log.info("ICI" + matiere + " - " + libelleNormalise);

            // ================= GENERATION =================
            generateEtiquettePage(
                    document,
                    logo,
                    data,
                    libelleNormalise,
                    series,
                    effectif.longValue(),
                    grp,
                    date,
                    horaire,
                    helv10, helv12Bold, helv14, helv22,
                    helv24Bold, helv16Bold, helv26Bold, helv16
            );

            document.newPage();
        }

        document.close();
    }


    private void generateEtiquettePage(Document document, Image logo, FusionRepartitionTirage data,
                                       String libelleMatiere, String serie, long effectif, String grp, String date, String horaire,
                                       Font f10, Font f12Bold, Font f14, Font f22, Font f22Bold, Font f16Bold, Font f26Bold, Font f16) throws DocumentException {
        // --- 1. EN-TÊTE ---
        PdfPTable header = new PdfPTable(3);
        header.setWidthPercentage(100);
        header.setWidths(new float[]{0.55f, 4f, 1f});

        PdfPCell imageCell = new PdfPCell(logo);
        imageCell.setBorder(Rectangle.NO_BORDER);
        imageCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        imageCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        header.addCell(imageCell);

        Paragraph headerText = new Paragraph(
                "REPUBLIQUE DU SENEGAL\nUn Peuple – Un But – Une Foi\n" +
                        "UNIVERSITE CHEIKH ANTA DIOP DE DAKAR\nOFFICE DU BACCALAUREAT",
                f10
        );
        headerText.setLeading(14f, 0);
        headerText.setAlignment(Element.ALIGN_LEFT);
        PdfPCell textCell = new PdfPCell(headerText);
        textCell.setBorder(Rectangle.NO_BORDER);
        textCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        textCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        header.addCell(textCell);

        PdfPCell codeAcademie = new PdfPCell(new Phrase(data.getAcademia(), f12Bold));
        codeAcademie.setBorder(Rectangle.NO_BORDER);
        codeAcademie.setHorizontalAlignment(Element.ALIGN_CENTER);
        codeAcademie.setVerticalAlignment(Element.ALIGN_MIDDLE);
        header.addCell(codeAcademie);

        document.add(header);

        // --- 2. TITRES CENTRAUX ---
        Paragraph office = new Paragraph("OFFICE DU BACCALAUREAT", f16Bold);
        office.setAlignment(Element.ALIGN_CENTER);
        office.setSpacingBefore(10f);
        document.add(office);

        Paragraph session = new Paragraph("BACCALAUREAT SESSION NORMALE " + data.getSession(), f22Bold);
        session.setAlignment(Element.ALIGN_CENTER);
        session.setSpacingAfter(20f);
        document.add(session);

        // --- 3. TABLEAU DES INFORMATIONS ---
        PdfPTable info = new PdfPTable(2);
        info.setWidthPercentage(100);
        info.setWidths(new float[]{1.2f, 4f});

        addInfoRow(info, "ACADEMIE :", getAcademieFullName(data.getAcademia()) + "                                 " + grp, f14, f22);
        addInfoRow(info, "CENTRE :", data.getCentreEcrit(), f14, f22);
        addInfoRow(info, "JURY :", String.valueOf(data.getJury()), f14, f22);
        addInfoRow(info, "SERIE (S) :", serie, f14, f22); // à affiner si plusieurs séries possibles
        addInfoRow(info, "CANDIDATS :", effectif + "        NT : " + Math.round(effectif * 1.05), f14, f22); // NT = effectif par défaut

        document.add(info);
        document.add(new Paragraph("\n"));

        // --- 4. BAS DE PAGE : EPREUVE et CALENDRIER ---
        PdfPTable footer = new PdfPTable(2);
        footer.setWidthPercentage(100);
        footer.setWidths(new float[]{2.55f, 1.5f});
        footer.setSpacingBefore(10f);

        // Cellule gauche
        PdfPCell leftCell = new PdfPCell();
        leftCell.setBorder(Rectangle.BOX);
        leftCell.setPadding(15f);

        Paragraph epreuveTitle = new Paragraph("EPREUVE", f16);
        epreuveTitle.setAlignment(Element.ALIGN_CENTER);
        leftCell.addElement(epreuveTitle);

        Paragraph epreuveLibelle = new Paragraph(libelleMatiere.toUpperCase(), f26Bold);
        epreuveLibelle.setAlignment(Element.ALIGN_CENTER);
        epreuveLibelle.setSpacingBefore(8f);
        leftCell.addElement(epreuveLibelle);

        footer.addCell(leftCell);

        // Cellule droite
        PdfPCell rightCell = new PdfPCell();
        rightCell.setBorder(Rectangle.BOX);
        rightCell.setPadding(15f);

        PdfPTable calTable = new PdfPTable(2);
        calTable.setWidthPercentage(100);
        calTable.setWidths(new float[]{1.25f, 2f});

        PdfPCell calTitle = new PdfPCell(new Phrase("CALENDRIER", f16));
        calTitle.setColspan(2);
        calTitle.setBorder(Rectangle.BOX);
        calTitle.setHorizontalAlignment(Element.ALIGN_CENTER);
        calTitle.setPadding(8f);
        calTable.addCell(calTitle);

        PdfPCell dateLabel = new PdfPCell(new Phrase("DATE :", f16));
        dateLabel.setBorder(Rectangle.BOX);
        dateLabel.setPadding(8f);
        calTable.addCell(dateLabel);

        PdfPCell dateValue = new PdfPCell(new Phrase(date != null ? date : "1er JOUR", f14));
        dateValue.setBorder(Rectangle.BOX);
        dateValue.setPadding(8f);
        calTable.addCell(dateValue);

        PdfPCell horaireLabel = new PdfPCell(new Phrase("HORAIRE :", f16));
        horaireLabel.setBorder(Rectangle.BOX);
        horaireLabel.setPadding(8f);
        calTable.addCell(horaireLabel);

        PdfPCell horaireValue = new PdfPCell(new Phrase(horaire != null ? horaire : "14H30-17h 30", f14));
        horaireValue.setBorder(Rectangle.BOX);
        horaireValue.setPadding(8f);
        calTable.addCell(horaireValue);

        rightCell.addElement(calTable);
        footer.addCell(rightCell);

        document.add(footer);
    }

    public String getAcademieFullName(String code) {
        if (code == null) return "";
        switch (code.toUpperCase()) {
            case "PK": return "PIKINE - GUEDIAWAYE";
            case "DK": return "DAKAR";
            case "RF": return "RUFISQUE";
            case "DL": return "DIOURBEL";
            case "FK": return "FATICK";
            case "KF": return "KAFFRINE";
            case "KL": return "KAOLACK";
            case "KG": return "KEDOUGOU";
            case "KD": return "KOLDA";
            case "LG": return "LOUGA";
            case "MT": return "MATAM";
            case "SL": return "SAINT LOUIS";
            case "SD": return "SEDHIOU";
            case "TA": return "TAMBACOUNDA";
            case "TB": return "TAMBACOUNDA"; // TA et TB tous deux TAMBACOUNDA
            case "TH": return "THIES";
            case "ZG": return "ZIGUINCHOR";
            default: return code; // si inconnu, on retourne le code
        }
    }

    private void addInfoRow(PdfPTable table, String label, String value, Font labelFont, Font valueFont) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, labelFont));
        labelCell.setBorder(Rectangle.NO_BORDER);
        labelCell.setPaddingBottom(8f);
        labelCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.addCell(labelCell);

        PdfPCell valueCell = new PdfPCell(new Phrase(value, valueFont));
        valueCell.setBorder(Rectangle.NO_BORDER);
        valueCell.setPaddingBottom(8f);
        labelCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.addCell(valueCell);
    }

    // Classes internes pour définir les matières
    @FunctionalInterface
    private interface EffectifExtractor {
        long extract(FusionRepartitionTirage data);
    }

    @FunctionalInterface
    private interface DateExtractor {
        String extract(FusionRepartitionTirage data);
    }

    @FunctionalInterface
    private interface HoraireExtractor {
        String extract(FusionRepartitionTirage data);
    }

    private static class MatiereDefinition {
        String libelle;
        EffectifExtractor effectifExtractor;
        DateExtractor dateExtractor;
        HoraireExtractor horaireExtractor;

        String listeSerie;

        MatiereDefinition(String libelle, EffectifExtractor effExt, DateExtractor dateExt, HoraireExtractor horExt, String listeSerie) {
            this.libelle = libelle;
            this.effectifExtractor = effExt;
            this.dateExtractor = dateExt;
            this.horaireExtractor = horExt;
            this.listeSerie = listeSerie;

        }

        // Ajoutez cette méthode dans votre contrôleur

    }


    @Operation(summary = "Génération du document BDR LS 2025")
    @GetMapping("/generate-bdr")
    public void generateBDRDocument(HttpServletResponse response) throws IOException, DocumentException {

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "inline; filename=BDR_LS_2025.pdf");

        Document document = new Document(PageSize.A4, 50f, 50f, 50f, 50f);
        PdfWriter.getInstance(document, response.getOutputStream());
        document.open();

        try {
            // Initialisation des polices
            FontConfiguration fonts = initializeFonts();

            // Chargement et configuration du logo
            Image logo = loadAndScaleLogo();

            // Construction du document
            buildDocument(document, fonts, logo);

        } catch (Exception e) {
            // Log l'erreur et la relancer
            throw new RuntimeException("Erreur lors de la génération du PDF", e);
        } finally {
            if (document != null && document.isOpen()) {
                document.close();
            }
        }
    }

    /**
     * Configuration des polices utilisées dans le document
     */
    private FontConfiguration initializeFonts() {
        FontConfiguration fonts = new FontConfiguration();
        fonts.titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
        fonts.normalFont = FontFactory.getFont(FontFactory.HELVETICA, 11);
        fonts.boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11);
        fonts.headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
        fonts.smallFont = FontFactory.getFont(FontFactory.HELVETICA, 9);
        return fonts;
    }

    /**
     * Charge et redimensionne le logo
     */
    private Image loadAndScaleLogo() throws IOException, BadElementException {
        Image logo = Image.getInstance(
                new ClassPathResource("images/logo-UCAD_.png").getInputStream().readAllBytes()
        );
        logo.scaleToFit(60f, 60f);
        return logo;
    }

    /**
     * Construit l'intégralité du document
     */
    private void buildDocument(Document document, FontConfiguration fonts, Image logo) throws DocumentException {
        // En-tête avec logo
        addHeader(document, fonts, logo);

        // Titre académie
        addAcademieTitle(document, fonts);

        // Informations principales
        addMainInfo(document, fonts);

        // Tableau des disciplines
        addDisciplinesTable(document, fonts);
    }

    /**
     * Ajoute l'en-tête avec logo, texte républicain et code
     */
    private void addHeader(Document document, FontConfiguration fonts, Image logo) throws DocumentException {
        PdfPTable header = new PdfPTable(3);
        header.setWidthPercentage(100);
        header.setWidths(new float[]{0.8f, 2.75f, 3f});

        // Cellule logo
        PdfPCell imageCell = new PdfPCell(logo);
        imageCell.setBorder(Rectangle.NO_BORDER);
        imageCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        imageCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        header.addCell(imageCell);

        // Cellule texte
        Paragraph headerText = new Paragraph(
                "UNIVERSITE CHEIKH ANTA DIOP DE DAKAR\nOFFICE DU BACCALAUREAT\n" +
                        "- - - - - - - - -\nDIVISION PEDAGOGIE",
                fonts.smallFont
        );
        headerText.setLeading(14f, 0);
        headerText.setAlignment(Element.ALIGN_LEFT);

        PdfPCell textCell = new PdfPCell(headerText);
        textCell.setBorder(Rectangle.NO_BORDER);
        textCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        textCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        header.addCell(textCell);

        // Cellule code académie
        PdfPCell codeAcademie = new PdfPCell(new Phrase("BORDEREAU DE CONVOYAGE DE SUJETS", fonts.smallFont));
        codeAcademie.setBorder(Rectangle.NO_BORDER);
        codeAcademie.setHorizontalAlignment(Element.ALIGN_CENTER);
        codeAcademie.setVerticalAlignment(Element.ALIGN_MIDDLE);
        header.addCell(codeAcademie);

        document.add(header);
    }

    /**
     * Ajoute le titre ACADEMIE : DAKAR
     */
    private void addAcademieTitle(Document document, FontConfiguration fonts) throws DocumentException {
        Paragraph academie = new Paragraph("ACADEMIE : DAKAR", fonts.boldFont);
        academie.setAlignment(Element.ALIGN_LEFT);
        academie.setSpacingBefore(10f);
        academie.setSpacingAfter(15f);
        document.add(academie);
    }

    /**
     * Ajoute le tableau des informations principales
     */
    /**
     * Ajoute le tableau des informations principales avec JURY, EFF, NBR DE SERIES et SERIE(S) sur une même ligne
     */
    private void addMainInfo(Document document, FontConfiguration fonts) throws DocumentException {
        // Tableau principal pour organiser Centre d'écrit et la ligne groupée
        PdfPTable mainInfoTable = new PdfPTable(1);
        mainInfoTable.setWidthPercentage(100);

        // Première ligne : Centre d'écrit
        PdfPTable centreTable = new PdfPTable(2);
        centreTable.setWidthPercentage(100);
        centreTable.setWidths(new float[]{1f, 3f});

        PdfPCell centreLabel = new PdfPCell(new Phrase("Centre d'écrit :", fonts.boldFont));
        centreLabel.setBorder(Rectangle.NO_BORDER);
        centreLabel.setPaddingBottom(8f);
        centreTable.addCell(centreLabel);

        PdfPCell centreValue = new PdfPCell(new Phrase("LYCEE DES PARCELLES ASSAINIES U13", fonts.normalFont));
        centreValue.setBorder(Rectangle.NO_BORDER);
        centreValue.setPaddingBottom(8f);
        centreTable.addCell(centreValue);

        PdfPCell centreRowCell = new PdfPCell(centreTable);
        centreRowCell.setBorder(Rectangle.NO_BORDER);
        centreRowCell.setPaddingBottom(5f);
        mainInfoTable.addCell(centreRowCell);

        // Deuxième ligne : JURY, EFF, NBR DE SERIES, SERIE(S) groupés
        PdfPTable groupedTable = new PdfPTable(4);
        groupedTable.setWidthPercentage(100);
        groupedTable.setWidths(new float[]{1.5f, 1f, 1.5f, 1.5f});

        // JURY
        PdfPCell juryLabel = new PdfPCell(new Phrase("JURY :", fonts.boldFont));
        juryLabel.setBorder(Rectangle.NO_BORDER);
        juryLabel.setHorizontalAlignment(Element.ALIGN_LEFT);
        groupedTable.addCell(juryLabel);

        PdfPCell juryValue = new PdfPCell(new Phrase("1153", fonts.normalFont));
        juryValue.setBorder(Rectangle.NO_BORDER);
        juryValue.setHorizontalAlignment(Element.ALIGN_LEFT);
        groupedTable.addCell(juryValue);

        // EFF
        PdfPCell effLabel = new PdfPCell(new Phrase("EFF. :", fonts.boldFont));
        effLabel.setBorder(Rectangle.NO_BORDER);
        effLabel.setHorizontalAlignment(Element.ALIGN_LEFT);
        groupedTable.addCell(effLabel);

        PdfPCell effValue = new PdfPCell(new Phrase("296", fonts.normalFont));
        effValue.setBorder(Rectangle.NO_BORDER);
        effValue.setHorizontalAlignment(Element.ALIGN_LEFT);
        groupedTable.addCell(effValue);

        // NBR DE SERIES
        PdfPCell nbrLabel = new PdfPCell(new Phrase("NBR DE SERIE (S) :", fonts.boldFont));
        nbrLabel.setBorder(Rectangle.NO_BORDER);
        nbrLabel.setHorizontalAlignment(Element.ALIGN_LEFT);
        groupedTable.addCell(nbrLabel);

        PdfPCell nbrValue = new PdfPCell(new Phrase("1", fonts.normalFont));
        nbrValue.setBorder(Rectangle.NO_BORDER);
        nbrValue.setHorizontalAlignment(Element.ALIGN_LEFT);
        groupedTable.addCell(nbrValue);

        // SERIE(S)
        PdfPCell serieLabel = new PdfPCell(new Phrase("SERIE (S) :", fonts.boldFont));
        serieLabel.setBorder(Rectangle.NO_BORDER);
        serieLabel.setHorizontalAlignment(Element.ALIGN_LEFT);
        groupedTable.addCell(serieLabel);

        PdfPCell serieValue = new PdfPCell(new Phrase("L2", fonts.normalFont));
        serieValue.setBorder(Rectangle.NO_BORDER);
        serieValue.setHorizontalAlignment(Element.ALIGN_LEFT);
        groupedTable.addCell(serieValue);

        PdfPCell groupedRowCell = new PdfPCell(groupedTable);
        groupedRowCell.setBorder(Rectangle.NO_BORDER);
        groupedRowCell.setPaddingBottom(15f);
        mainInfoTable.addCell(groupedRowCell);

        document.add(mainInfoTable);
        document.add(new Paragraph(" ")); // Espace avant le tableau
    }
    /**
     * Ajoute une ligne d'information dans le tableau
     */
    private void addInfoRow(PdfPTable table, String label, String value, FontConfiguration fonts) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, fonts.boldFont));
        labelCell.setBorder(Rectangle.NO_BORDER);
        labelCell.setPaddingBottom(8f);
        table.addCell(labelCell);

        PdfPCell valueCell = new PdfPCell(new Phrase(value, fonts.normalFont));
        valueCell.setBorder(Rectangle.NO_BORDER);
        valueCell.setPaddingBottom(8f);
        table.addCell(valueCell);
    }

    /**
     * Ajoute le tableau des disciplines
     */
    private void addDisciplinesTable(Document document, FontConfiguration fonts) throws DocumentException {
        PdfPTable mainTable = new PdfPTable(4);
        mainTable.setWidthPercentage(100);
        mainTable.setWidths(new float[]{3f, 1f, 1.5f, 1.5f});

        // En-têtes du tableau
        addTableHeaders(mainTable, fonts);

        // Lignes de données
        addAllDisciplineRows(mainTable, fonts);

        // Ligne total
        addTotalRow(mainTable, fonts);

        document.add(mainTable);
    }

    /**
     * Ajoute les en-têtes du tableau
     */
    private void addTableHeaders(PdfPTable table, FontConfiguration fonts) {
        String[] headers = {"DISCIPLINES", "EFFECTIF", "Groupe 1", "Groupe 2"};
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, fonts.boldFont));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(8f);
            table.addCell(cell);
        }
    }

    /**
     * Ajoute toutes les lignes de disciplines
     */
    private void addAllDisciplineRows(PdfPTable table, FontConfiguration fonts) {
        DisciplineRow[] rows = {
                new DisciplineRow("LV1 Allemand", "0", "0", "0"),
                new DisciplineRow("LV1 Anglais", "97", "1", "1"),
                new DisciplineRow("LV1 ARABE", "0", "0", "0"),
                new DisciplineRow("LV1 Espagnol", "199", "1", "1"),
                new DisciplineRow("LV1 PORTUGAIS", "0", "0", "0"),
                new DisciplineRow("LV2 Allemand", "0", "0", "0"),
                new DisciplineRow("LV2 Anglais", "199", "1", "1"),
                new DisciplineRow("LV2 ARABE", "0", "0", "0"),
                new DisciplineRow("LV2 Espagnol", "0", "0", "0"),
                new DisciplineRow("LV2 PORTUGAIS", "0", "0", "0"),
                new DisciplineRow("LV2 RUSSE", "0", "0", "0"),
                new DisciplineRow("LV2 ITALIEN", "0", "0", "0"),
                new DisciplineRow("SCIENCES PHYSIQUE (L2)", "296", "1", "1"),
                new DisciplineRow("SVT (L2)", "0", "0", "0"),
                new DisciplineRow("ECONOMIE(L2)", "97", "1", "1"),
                new DisciplineRow("Français (L)", "296", "1", "1"),
                new DisciplineRow("Français (S)", "0", "0", "0"),
                new DisciplineRow("Anglais (S)", "0", "0", "0"),
                new DisciplineRow("Maths (L)", "296", "1", "1"),
                new DisciplineRow("Maths (S1)", "0", "0", "0"),
                new DisciplineRow("SCIENCES PHYSIQUES (S1)", "0", "0", "0"),
                new DisciplineRow("Maths (S2)", "0", "0", "0"),
                new DisciplineRow("SCIENCES PHYSIQUES (S2)", "0", "0", "0"),
                new DisciplineRow("SVT(S2)", "0", "0", "0"),
                new DisciplineRow("PHILO (L)", "296", "1", "1"),
                new DisciplineRow("PHILO(S)", "0", "0", "0"),
                new DisciplineRow("HISTO GEO (L S)", "296", "1", "1"),
                new DisciplineRow("SVT(S1S1A)", "0", "0", "0")
        };

        for (DisciplineRow row : rows) {
            addDisciplineRow(table, row.discipline, row.effectif, row.groupe1, row.groupe2, fonts.normalFont);
        }
    }

    /**
     * Ajoute une ligne de discipline au tableau
     */
    private void addDisciplineRow(PdfPTable table, String discipline, String effectif,
                                  String groupe1, String groupe2, Font font) {
        // Discipline
        PdfPCell discCell = new PdfPCell(new Phrase(discipline, font));
        discCell.setPadding(5f);
        table.addCell(discCell);

        // Effectif
        PdfPCell effCell = new PdfPCell(new Phrase(effectif, font));
        effCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        effCell.setPadding(5f);
        table.addCell(effCell);

        // Groupe 1
        PdfPCell g1Cell = new PdfPCell(new Phrase(groupe1, font));
        g1Cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        g1Cell.setPadding(5f);
        table.addCell(g1Cell);

        // Groupe 2
        PdfPCell g2Cell = new PdfPCell(new Phrase(groupe2, font));
        g2Cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        g2Cell.setPadding(5f);
        table.addCell(g2Cell);
    }

    /**
     * Ajoute la ligne TOTAL ENV
     */
    private void addTotalRow(PdfPTable table, FontConfiguration fonts) {
        PdfPCell totalLabel = new PdfPCell(new Phrase("TOTAL ENV:", fonts.boldFont));
        totalLabel.setColspan(3);
        totalLabel.setHorizontalAlignment(Element.ALIGN_RIGHT);
        totalLabel.setPadding(8f);
        table.addCell(totalLabel);

        PdfPCell totalValue = new PdfPCell(new Phrase("1899", fonts.boldFont));
        totalValue.setHorizontalAlignment(Element.ALIGN_CENTER);
        totalValue.setPadding(8f);
        table.addCell(totalValue);
    }

    /**
     * Classes internes pour organiser les données
     */
    private static class FontConfiguration {
        Font titleFont;
        Font normalFont;
        Font boldFont;
        Font headerFont;
        Font smallFont;
    }

    private static class InfoItem {
        String label;
        String value;
        InfoItem(String label, String value) {
            this.label = label;
            this.value = value;
        }
    }

    private static class DisciplineRow {
        String discipline;
        String effectif;
        String groupe1;
        String groupe2;
        DisciplineRow(String discipline, String effectif, String groupe1, String groupe2) {
            this.discipline = discipline;
            this.effectif = effectif;
            this.groupe1 = groupe1;
            this.groupe2 = groupe2;
        }
    }

}
