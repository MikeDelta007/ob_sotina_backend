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
@RequestMapping("/api/v1/pdf")
@RequiredArgsConstructor
@Tag(name="PDF Controller", description = "Endpoints responsables de la gestion des PDF")
public class PdfController
{


    @Autowired
    private FusionRepartitionTirageRepository repository;

    @Autowired
    private TirageJuryMatService tirageJuryMatService;



    @Operation(summary = "Génération de l'étiquette de table - Format A4 Paysage")
    @GetMapping("/generate-etiquette-paysage")
    public void generateEtiquettes(@RequestParam(value = "matieres", required = false) List<String> matieres,
            HttpServletResponse response) throws IOException, DocumentException {

        List<FusionRepartitionTirage> list = repository.findAll();
        if (list.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            return;
        }

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "inline; filename=etiquettes_bac.pdf");

        Document document = new Document(PageSize.A4.rotate(), 36f, 36f, 36f, 36f);
        PdfWriter.getInstance(document, response.getOutputStream());
        document.open();

        // Polices (fallback Helvetica)
        Font helv10 = FontFactory.getFont(FontFactory.HELVETICA, 10, Font.NORMAL);
        Font helv12Bold = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 60);
        Font helv14 = FontFactory.getFont(FontFactory.HELVETICA, 17, Font.BOLD);
        Font helv22 = FontFactory.getFont(FontFactory.HELVETICA, 22, Font.NORMAL);
        Font helv24Bold = FontFactory.getFont(FontFactory.HELVETICA, 24, Font.BOLD);
        Font helv16Bold = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
        Font helv26Bold = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 30);

        // Logo
        Image logo = Image.getInstance(new ClassPathResource("images/logo-UCAD_.png").getInputStream().readAllBytes());
        logo.scaleToFit(70f, 70f);

        // Préparer la liste des matières disponibles avec leurs champs associés
        // Définition de toutes les matières avec leurs extracteurs
        List<MatiereDefinition> matieresDefinitions = Arrays.asList(
                // Français
                new MatiereDefinition("FRANCAIS L", FusionRepartitionTirage::getFrenchL,
                        FusionRepartitionTirage::getDate1FL, FusionRepartitionTirage::getHeure1FL, "L'1 - L1A - L1B - L2"),
                new MatiereDefinition("FRANCAIS S", FusionRepartitionTirage::getFrenchS,
                        FusionRepartitionTirage::getDate1FS, FusionRepartitionTirage::getHeure1FS, "S1 - S2 - S3 - S4 - S5"),
                new MatiereDefinition("FRANCAIS L (A)", FusionRepartitionTirage::getFrenchLA,
                        FusionRepartitionTirage::getDate1FLa, FusionRepartitionTirage::getHeure1FLa, "LA"),
                new MatiereDefinition("FRANCAIS S (A)", FusionRepartitionTirage::getFrenchSA,
                        FusionRepartitionTirage::getDate1FSa, FusionRepartitionTirage::getHeure1FSa, "S1A - S2A"),

                // Anglais
                new MatiereDefinition("ANGLAIS S", FusionRepartitionTirage::getEnglishS,
                        FusionRepartitionTirage::getDate1ES, FusionRepartitionTirage::getHeure1ES, "S1 - S2 - S3 - S4 - S5"),
                new MatiereDefinition("ANGLAIS LV1", FusionRepartitionTirage::getAnglaisLV1,
                        FusionRepartitionTirage::getDate1ANG1, FusionRepartitionTirage::getHeure1ANG1, "L'1 - L1A - L1B - L2"),
                new MatiereDefinition("ANGLAIS LV2", FusionRepartitionTirage::getAnglaisLV2,
                        FusionRepartitionTirage::getDate1ANG2, FusionRepartitionTirage::getHeure1ANG2, "L'1 - L1A - L1B - L2"),

                // Mathématiques
                new MatiereDefinition("MATH L", FusionRepartitionTirage::getMathL,
                        FusionRepartitionTirage::getDate1ML, FusionRepartitionTirage::getHeure1ML, "L'1 - L1A - L1B - L2 - LA"),
                new MatiereDefinition("MATH S (SM)", FusionRepartitionTirage::getMathSM,
                        FusionRepartitionTirage::getDate1MSM, FusionRepartitionTirage::getHeure1MSM, "S1 - S3 - S1A"),
                new MatiereDefinition("MATH S (SE)", FusionRepartitionTirage::getMathSE,
                        FusionRepartitionTirage::getDate1MSE, FusionRepartitionTirage::getHeure1MSE, "S2 - S2A - S4 - S5"),

                // Physique-Chimie
                new MatiereDefinition("PC L", FusionRepartitionTirage::getPcL,
                        FusionRepartitionTirage::getDate1PCL, FusionRepartitionTirage::getHeure1PCL, "L2"),
                new MatiereDefinition("PC S (SM)", FusionRepartitionTirage::getPcSM,
                        FusionRepartitionTirage::getDate1PCSM, FusionRepartitionTirage::getHeure1PCSM, "S1 - S3 - S1A"),
                new MatiereDefinition("PC S (SE)", FusionRepartitionTirage::getPcSE,
                        FusionRepartitionTirage::getDate1PCSE, FusionRepartitionTirage::getHeure1PCSE, "S2 - S2A - S4 - S5"),

                // SVT
                new MatiereDefinition("SVT L", FusionRepartitionTirage::getSvtL,
                        FusionRepartitionTirage::getDate1SVTL, FusionRepartitionTirage::getHeure1SVTL, "L2"),
                new MatiereDefinition("SVT S (SM)", FusionRepartitionTirage::getSvtSM,
                        FusionRepartitionTirage::getDate1SVTSM, FusionRepartitionTirage::getHeure1SVTSM, "S1 - S3 - S1A"),
                new MatiereDefinition("SVT S (SE)", FusionRepartitionTirage::getSvtSE,
                        FusionRepartitionTirage::getDate1SVTSE, FusionRepartitionTirage::getHeure1SVTSE, "S2 - S2A - S4 - S5"),

                // Philosophie
                new MatiereDefinition("PHILO L", FusionRepartitionTirage::getPhiloL,
                        FusionRepartitionTirage::getDate1PHILOL, FusionRepartitionTirage::getHeure1PHILOL, "L'1 - L1A - L1B - L2"),
                new MatiereDefinition("PHILO S", FusionRepartitionTirage::getPhiloS,
                        FusionRepartitionTirage::getDate1PHILOS, FusionRepartitionTirage::getHeure1PHILOS, "S1 - S2 - S2A S3 - S4 - S5"),

                // Histoire-Géo
                new MatiereDefinition("HISTOIRE-GEO", FusionRepartitionTirage::getHg,
                        FusionRepartitionTirage::getDate1HG, FusionRepartitionTirage::getHeure1HG, "L'1 - L1A - L1B - L2 - S1 - S2 - S2A - S3 - S4 - S5"),

                // Langues diverses
                new MatiereDefinition("ALLEMAND LV1", FusionRepartitionTirage::getAllemendLV1,
                        FusionRepartitionTirage::getDate1ALL1, FusionRepartitionTirage::getHeure1ALL1, "L'1 - L1A - L1B - L2"),
                new MatiereDefinition("ALLEMAND LV2", FusionRepartitionTirage::getAllemendLV2,
                        FusionRepartitionTirage::getDate1ALL2, FusionRepartitionTirage::getHeure1ALL2, "L'1 - L1A - L1B - L2"),
                new MatiereDefinition("ARABE MODERNE LV1", FusionRepartitionTirage::getArabeModerneLV1,
                        FusionRepartitionTirage::getDate1AM1, FusionRepartitionTirage::getHeure1AM1, "L'1 - L1A - L1B - L2"),
                new MatiereDefinition("ARABE MODERNE LV2", FusionRepartitionTirage::getArabeModerneLV2,
                        FusionRepartitionTirage::getDate1AM2, FusionRepartitionTirage::getHeure1AM2, "L'1 - L1A - L1B - L2"),
                new MatiereDefinition("ESPAGNOL LV1", FusionRepartitionTirage::getEspagnolLV1,
                        FusionRepartitionTirage::getDate1ESP1, FusionRepartitionTirage::getHeure1ESP1, "L'1 - L1A - L1B - L2"),
                new MatiereDefinition("ESPAGNOL LV2", FusionRepartitionTirage::getEspagnolLV2,
                        FusionRepartitionTirage::getDate1ESP2, FusionRepartitionTirage::getHeure1ESP2, "L'1 - L1A - L1B - L2"),
                new MatiereDefinition("ITALIEN", FusionRepartitionTirage::getItalien,
                        FusionRepartitionTirage::getDate1ITA, FusionRepartitionTirage::getHeure1ITA, "L'1 - L1A - L1B - L2"),
                new MatiereDefinition("LATIN", FusionRepartitionTirage::getLatin,
                        FusionRepartitionTirage::getDate1LAT, FusionRepartitionTirage::getHeure1LAT, "L1A - L1B"),
                new MatiereDefinition("PORTUGAIS LV1", FusionRepartitionTirage::getPortugaisLV1,
                        FusionRepartitionTirage::getDate1PORT1, FusionRepartitionTirage::getHeure1PORT1, "L'1 - L1A - L1B - L2"),
                new MatiereDefinition("PORTUGAIS LV2", FusionRepartitionTirage::getPortugaisLV2,
                        FusionRepartitionTirage::getDate1PORT2, FusionRepartitionTirage::getHeure1PORT2, "L'1 - L1A - L1B - L2"),
                new MatiereDefinition("RUSSE", FusionRepartitionTirage::getRusse,
                        FusionRepartitionTirage::getDate1RUS, FusionRepartitionTirage::getHeure1RUS, "L'1 - L2"),

                // Enseignements techniques/économiques
                new MatiereDefinition("ECONOMIE", FusionRepartitionTirage::getEconomie,
                        FusionRepartitionTirage::getDate1ECO, FusionRepartitionTirage::getHeure1ECO, "L2"),
                new MatiereDefinition("SES", FusionRepartitionTirage::getSes,
                        FusionRepartitionTirage::getDate1SES, FusionRepartitionTirage::getHeure1SES, "STEG"),
                new MatiereDefinition("GELEC", FusionRepartitionTirage::getGelec,
                        FusionRepartitionTirage::getDate1GE, FusionRepartitionTirage::getHeure1GE, "STIDD"),
                new MatiereDefinition("GEMEC", FusionRepartitionTirage::getGemec,
                        FusionRepartitionTirage::getDate1GM, FusionRepartitionTirage::getHeure1GM, "STIDD"),
                new MatiereDefinition("MO", FusionRepartitionTirage::getMo,
                        FusionRepartitionTirage::getDate1MO, FusionRepartitionTirage::getHeure1MO, "STEG"),
                new MatiereDefinition("GCF", FusionRepartitionTirage::getGcf,
                        FusionRepartitionTirage::getDate1GCF, FusionRepartitionTirage::getHeure1GCF, "STEG")
        );

        // Si une liste de matières est fournie, on filtre
        List<String> matieresDemandees = matieres != null ? matieres.stream()
                .map(String::trim)
                .map(String::toUpperCase)
                .collect(Collectors.toList()) : null;

        for (FusionRepartitionTirage data : list) {
            for (MatiereDefinition def : matieresDefinitions) {
                // Vérifier le filtre
                if (matieresDemandees != null && !matieresDemandees.contains(def.libelle.toUpperCase())) {
                    continue;
                }
                long effectif = def.effectifExtractor.extract(data);
                if (effectif > 0) {
                    String date = def.dateExtractor.extract(data);
                    String horaire = def.horaireExtractor.extract(data);
                    generateEtiquettePage(document, logo, data, def.libelle, def.listeSerie, effectif, date, horaire,
                            helv10, helv12Bold, helv14, helv22, helv24Bold, helv16Bold, helv26Bold);
                    document.newPage();
                }
            }
        }

        document.close();
    }

    private void generateEtiquettePage(Document document, Image logo, FusionRepartitionTirage data,
                                       String libelleMatiere, String serie, long effectif, String date, String horaire,
                                       Font f10, Font f12Bold, Font f14, Font f22, Font f22Bold, Font f16Bold, Font f26Bold) throws DocumentException {
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

        addInfoRow(info, "ACADEMIE :", getAcademieFullName(data.getAcademia()) + "                                 PREMIER GROUPE", f14, f22);
        addInfoRow(info, "CENTRE :", data.getCentreEcrit(), f14, f22);
        addInfoRow(info, "JURY :", String.valueOf(data.getJury()), f14, f22);
        addInfoRow(info, "SERIE (S) :", serie, f14, f22); // à affiner si plusieurs séries possibles
        addInfoRow(info, "CANDIDATS :", effectif + "        NT : " + Math.round(effectif * 1.05), f14, f22); // NT = effectif par défaut

        document.add(info);
        document.add(new Paragraph("\n"));

        // --- 4. BAS DE PAGE : EPREUVE et CALENDRIER ---
        PdfPTable footer = new PdfPTable(2);
        footer.setWidthPercentage(100);
        footer.setWidths(new float[]{1.5f, 1.5f});
        footer.setSpacingBefore(10f);

        // Cellule gauche
        PdfPCell leftCell = new PdfPCell();
        leftCell.setBorder(Rectangle.BOX);
        leftCell.setPadding(15f);

        Paragraph epreuveTitle = new Paragraph("EPREUVE", f16Bold);
        epreuveTitle.setAlignment(Element.ALIGN_CENTER);
        leftCell.addElement(epreuveTitle);

        Paragraph epreuveLibelle = new Paragraph(libelleMatiere, f26Bold);
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
        calTable.setWidths(new float[]{1f, 2f});

        PdfPCell calTitle = new PdfPCell(new Phrase("CALENDRIER", f16Bold));
        calTitle.setColspan(2);
        calTitle.setBorder(Rectangle.BOX);
        calTitle.setHorizontalAlignment(Element.ALIGN_CENTER);
        calTitle.setPadding(8f);
        calTable.addCell(calTitle);

        PdfPCell dateLabel = new PdfPCell(new Phrase("DATE :", f14));
        dateLabel.setBorder(Rectangle.BOX);
        dateLabel.setPadding(8f);
        calTable.addCell(dateLabel);

        PdfPCell dateValue = new PdfPCell(new Phrase(date != null ? date : "1er JOUR", f14));
        dateValue.setBorder(Rectangle.BOX);
        dateValue.setPadding(8f);
        calTable.addCell(dateValue);

        PdfPCell horaireLabel = new PdfPCell(new Phrase("HORAIRE :", f14));
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

}
