package com.officedubac.project.controllers;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.*;
import com.officedubac.project.dto.*;
import com.officedubac.project.models.*;
import com.officedubac.project.repository.*;
import com.officedubac.project.services.DecompteFeuilleJuryService;
import com.officedubac.project.services.TirageJuryMatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.format.datetime.DateFormatter;
import org.springframework.web.bind.annotation.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.text.DecimalFormat;

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
    private RepartitionTirageCGSRepository repositorycgs;

    @Autowired
    private RegleMatiereRepository repo;

    @Autowired
    private RegleMatiereCGSRepository repocgs;

    @Autowired
    private FusionRepartitionTirageRepository ftr;

    @Autowired
    private FusionRepartitionFeuilleRepository ffeuil;

    @Autowired
    private TirageJuryMatService tirageJuryMatService;

    @Autowired
    private DecompteFeuilleJuryService decompteFeuilleJuryService;


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

        //log.info("MATIERE DEMANDEE = {}", matiere);

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
                new ClassPathResource("images/sn.png")
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
            String date = "";
            String horaire = "";

            // ================= REGLE =================
            RegleMatiere regle = regleParCode.get(matiere);
            if (regle == null) {
                log.warn("Aucune règle trouvée pour {}", matiere);
                continue;
            }

            String series = (regle.getSeries() != null && !regle.getSeries().isEmpty())
                    ? String.join(" - ", regle.getSeries())
                    : "";

            if ("1ER".equalsIgnoreCase(groupe))
            {
                effectif = gm.getPremierGroupe();
                grp = "PREMIER GROUPE";
                date = Optional.ofNullable(regle.getDate1()).orElse("");
                horaire = Optional.ofNullable(regle.getHeure1()).orElse("");
            }
            else if ("2ND".equalsIgnoreCase(groupe))
            {
                effectif = gm.getSecondGroupe();
                grp = "SECOND GROUPE";
                date = Optional.ofNullable(regle.getDate2()).orElse("");
                horaire = Optional.ofNullable(regle.getHeure2()).orElse("");
            }


            if (effectif == null || effectif <= 0) {
                continue;
            }

            RegleMatiere regle_ = repo.findByCode(matiere);

            String libelleNormalise = Optional.ofNullable(regle_)
                    .map(RegleMatiere::getValeur)
                    .orElse(matiere);

            // log.info("ICI" + matiere + " - " + libelleNormalise);

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


    @Operation(summary = "Génération de l'étiquette de table - Format A4 Paysage")
    @GetMapping("/generate-etiquetteCGS-paysage")
    public void generateEtiquettes(HttpServletResponse response) throws IOException, DocumentException {
        List<RepartitionTirageCGS> list = repositorycgs.findAllByOrderByDisciplineAsc();
        //log.info(list.toString());
        if (list.isEmpty())
        {
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            return;
        }

        // 🔹 Charger les règles
        List<RegleMatiereCGS> regles = repocgs.findAll();
        Map<String, RegleMatiereCGS> regleParCode = regles.stream()
                .collect(Collectors.toMap(
                        r -> r.getValeur().toUpperCase(),
                        r -> r,
                        (r1, r2) -> r1
                ));

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "inline; filename=etiquettes_bac.pdf");

        // 🔹 Polices
        Font helv10 = FontFactory.getFont(FontFactory.HELVETICA, 10, Font.NORMAL);
        Font helv12Bold = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 60);
        Font helv14 = FontFactory.getFont(FontFactory.HELVETICA, 17, Font.BOLD);
        Font helv22 = FontFactory.getFont(FontFactory.HELVETICA, 22, Font.NORMAL);
        Font helv24Bold = FontFactory.getFont(FontFactory.HELVETICA, 24, Font.BOLD);
        Font helv16Bold = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
        Font helv16 = FontFactory.getFont(FontFactory.HELVETICA, 16);
        Font helv26Bold = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 26);

        Document document = new Document(PageSize.A4.rotate(), 36f, 36f, 36f, 36f);
        PdfWriter.getInstance(document, response.getOutputStream());
        document.open();

        // 🔹 Logo
        Image logo = Image.getInstance(new ClassPathResource("images/sn.png").getInputStream().readAllBytes());
        logo.scaleToFit(70f, 70f);

        // 🔹 Parcours optimisé : pour chaque centre et discipline
        for (RepartitionTirageCGS data : list)
        {
            if (data.getDiscipline() == null) continue;

            String matiere = data.getDiscipline().toUpperCase();
            RegleMatiereCGS regle = regleParCode.get(matiere);
            if (regle == null)
            {
                log.warn("Aucune règle trouvée pour {}", matiere);
                continue;
            }

            String series = (data.getSeries() != null && !data.getSeries().isEmpty())
                    ? String.join(", ", data.getSeries())
                    : "";

            // 🔹 Génération pour PREMIERE

            if (data.getEff1ere() != null && data.getEff1ere() > 0)
            {
                generateEtiquetteCGSPage(
                        document,
                        logo,
                        data,
                        matiere,
                        series,
                        data.getEff1ere().longValue(),
                        "PREMIERE",
                        Optional.ofNullable(regle.getDate()).orElse(""),
                        Optional.ofNullable(regle.getHeure()).orElse(""),
                        helv10, helv12Bold, helv14, helv22,
                        helv24Bold, helv16Bold, helv26Bold, helv16
                );
                document.newPage();
            }

            // 🔹 Génération pour TERMINALE

            if (data.getEffTle() != null && data.getEffTle() > 0)
            {
                generateEtiquetteCGSPage(
                        document,
                        logo,
                        data,
                        matiere,
                        series,
                        data.getEffTle().longValue(),
                        "TERMINALE",
                        Optional.ofNullable(regle.getDate()).orElse(""),
                        Optional.ofNullable(regle.getHeure()).orElse(""),
                        helv10, helv12Bold, helv14, helv22,
                        helv24Bold, helv16Bold, helv26Bold, helv16
                );
                document.newPage();
            }

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

        // Cellule texte
        Paragraph headerText = new Paragraph(
                "REPUBLIQUE DU SENEGAL\nUn Peuple - Un But - Une Foi\n" +
                        "Ministère de l'Enseignement supérieur, de la Recherche et de l'Innovation" +
                        "\nOffice du Baccalauréat",
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

        addInfoRow(info, "ACADEMIE :", getAcademieFullName(data.getAcademia()) + "                               " + "[" + grp + "]", f14, f22);
        addInfoRow(info, "CENTRE :", data.getCentreEcrit(), f14, f22);
        addInfoRow(info, "JURY :", String.valueOf(data.getJury()), f14, f22);
        addInfoRow(info, "SERIE (S) :", serie, f14, f22); // à affiner si plusieurs séries possibles
        addInfoRow(info, "CANDIDATS :", effectif + "        NT : " + (Math.round(effectif * 1.05) + 1), f14, f22); // NT = effectif par défaut

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
        epreuveLibelle.setSpacingBefore(5f);
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

        PdfPCell dateValue = new PdfPCell(new Phrase(date != null ? date : "", f14));
        dateValue.setBorder(Rectangle.BOX);
        dateValue.setPadding(8f);
        calTable.addCell(dateValue);

        PdfPCell horaireLabel = new PdfPCell(new Phrase("HORAIRE :", f16));
        horaireLabel.setBorder(Rectangle.BOX);
        horaireLabel.setPadding(8f);
        calTable.addCell(horaireLabel);

        PdfPCell horaireValue = new PdfPCell(new Phrase(horaire != null ? horaire : "", f14));
        horaireValue.setBorder(Rectangle.BOX);
        horaireValue.setPadding(8f);
        calTable.addCell(horaireValue);

        rightCell.addElement(calTable);
        footer.addCell(rightCell);

        document.add(footer);
    }


    private void generateEtiquetteCGSPage(Document document, Image logo, RepartitionTirageCGS data,
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

        // Cellule texte
        Paragraph headerText = new Paragraph(
                "REPUBLIQUE DU SENEGAL\nUn Peuple - Un But - Une Foi\n" +
                        "Ministère de l'Enseignement supérieur, de la Recherche et de l'Innovation" +
                        "\nOffice du Baccalauréat",
                f10
        );
        headerText.setLeading(14f, 0);
        headerText.setAlignment(Element.ALIGN_LEFT);

        PdfPCell textCell = new PdfPCell(headerText);
        textCell.setBorder(Rectangle.NO_BORDER);
        textCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        textCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        header.addCell(textCell);


        PdfPCell codeAcademie = new PdfPCell(new Phrase("", f12Bold));
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

        Paragraph session = new Paragraph("CONCOURS GENERAL SENEGALAIS\nSESSION " + data.getSession(), f22Bold);
        session.setAlignment(Element.ALIGN_CENTER);
        session.setSpacingAfter(20f);
        document.add(session);

        // --- 3. TABLEAU DES INFORMATIONS ---
        PdfPTable info = new PdfPTable(2);
        info.setWidthPercentage(100);
        info.setWidths(new float[]{1.2f, 4f});

        addInfoRow(info, "ACADEMIE :", data.getAcademia(), f14, f22);
        addInfoRow(info, "CENTRE :", data.getCentreEcrit(), f14, f22);
        addInfoRow(info, "CLASSE :", grp, f14, f22);
        // addInfoRow(info, "SERIE (S) :", serie, f14, f22); // à affiner si plusieurs séries possibles
        addInfoRow(info, "SERIE (S) :", "TOUTES SERIES", f14, f22); // à affiner si plusieurs séries possibles
        addInfoRow(info, "CANDIDATS :", effectif + "        NT : " + (Math.round(effectif * 1.055) + 1), f14, f22); // NT = effectif par défaut

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
        epreuveLibelle.setSpacingBefore(5f);
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

        PdfPCell dateValue = new PdfPCell(new Phrase(date != null ? date : "", f14));
        dateValue.setBorder(Rectangle.BOX);
        dateValue.setPadding(8f);
        calTable.addCell(dateValue);

        PdfPCell horaireLabel = new PdfPCell(new Phrase("HORAIRE :", f16));
        horaireLabel.setBorder(Rectangle.BOX);
        horaireLabel.setPadding(8f);
        calTable.addCell(horaireLabel);

        PdfPCell horaireValue = new PdfPCell(new Phrase(horaire != null ? horaire : "", f14));
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


    @Operation(summary = "Génération du document BDR LS")
    @GetMapping("/generate-bdr")
    public void generateBDRDocument(HttpServletResponse response) throws IOException, DocumentException
    {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "inline; filename=BDR_LS_2025.pdf");

        Document document = new Document(PageSize.A4, 50f, 50f, 50f, 50f);
        PdfWriter.getInstance(document, response.getOutputStream());
        document.open();

        try {
            // Ajout immédiat d'un élément pour éviter le document vide
            //document.add(new Paragraph("Génération du document en cours...", new Font(Font.HELVETICA, 12)));

            // Initialisation des polices
            FontConfiguration fonts = initializeFonts();

            // Chargement et configuration du logo
            Image logo = loadAndScaleLogo();

            List<FusionRepartitionTirage> allFRT = ftr.findAll();
            //System.out.println("Nombre de répartitions trouvées : " + allFRT.size());

            if (allFRT.isEmpty())
            {
                document.add(new Paragraph("Aucune répartition trouvée pour cette session.", fonts.normalFont));
            }
            else
            {
                for (int i = 0; i < allFRT.size(); i++)
                {
                    FusionRepartitionTirage repartition = allFRT.get(i);

                    RepartitionCompleteDTO data = tirageJuryMatService.construire(repartition);
                    if (data == null)
                    {
                        System.out.println("Données nulles pour cette répartition");
                        document.add(new Paragraph("Données incomplètes pour ce centre.", fonts.normalFont));
                        continue;
                    }

                    buildDocument(document, fonts, logo, data);

                    if (i < allFRT.size() - 1) {
                        document.newPage();
                    }
                }
            }

        } catch (Exception e) {
            System.err.println("Erreur dans la génération du PDF : " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de la génération du PDF", e);
        } finally {
            if (document != null && document.isOpen()) {
                document.close();
            }
        }
    }

    @Operation(summary = "Génération du document BDR LS")
    @GetMapping("/generate-bdr-cgs")
    public void generateBDRDocument_(HttpServletResponse response) throws IOException, DocumentException {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "inline; filename=BDR_LS_2025.pdf");

        Document document = new Document(PageSize.A4, 50f, 50f, 50f, 50f);
        PdfWriter writer = PdfWriter.getInstance(document, response.getOutputStream());
        FooterEvent event = new FooterEvent(initializeFonts().verySmallFont);
        writer.setPageEvent(event);
        document.open();

        try {

            FontConfiguration fonts = initializeFonts();
            Image logo = loadAndScaleLogo();
            List<RepartitionCompleteCGSDTO> allRep = tirageJuryMatService.construire_();
            //System.out.println("Nombre de répartitions trouvées : " + allFRT.size());

            if (allRep.isEmpty()) {
                document.add(new Paragraph("Aucune répartition trouvée pour cette session.", fonts.normalFont));
            }
            else
            {
                int index = 0;
                for (RepartitionCompleteCGSDTO data : allRep)
                {
                    if (data == null) {
                        System.out.println("Données nulles pour cette répartition");
                        document.add(new Paragraph("Données incomplètes pour ce centre.", fonts.normalFont));
                        continue;
                    }

                    // Afficher les données reçues
                    // System.out.println("Données construites : session=" + data.getSession() + ", centre=" + data.getCentre() + ", nbMatieres=" + (data.getMatieres() != null ? data.getMatieres().size() : 0));

                    buildDocumentCGS(document, fonts, logo, data);

                    if (index < allRep.size() - 1) {
                        document.newPage();
                    }
                    index++;
                }
            }

        } catch (Exception e) {
            System.err.println("Erreur dans la génération du PDF : " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de la génération du PDF", e);
        } finally {
            if (document != null && document.isOpen()) {
                document.close();
            }
        }
    }

    @Operation(summary = "Génération du document BDR LS")
    @GetMapping("/generate-bdr-feuilles")
    public void generateBDRFDocument(HttpServletResponse response) throws IOException, DocumentException {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "inline; filename=BDR_LS_2025.pdf");

        Document document = new Document(PageSize.A4, 50f, 50f, 35f, 50f);
        PdfWriter writer = PdfWriter.getInstance(document, response.getOutputStream());
        FooterEvent2 event = new FooterEvent2(initializeFonts().smallFont);
        writer.setPageEvent(event);
        document.open();

        try {
            // Initialisation des polices
            FontConfiguration fonts = initializeFonts();

            // Chargement et configuration du logo
            Image logo = loadAndScaleLogo();

            // Récupération des données
            List<FusionRepartitionFeuille> allFRT = ffeuil.findAll();

            if (allFRT.isEmpty()) {
                document.add(new Paragraph("Aucune répartition trouvée pour cette session.", fonts.normalFont));
            } else {
                for (int i = 0; i < allFRT.size(); i++) {
                    FusionRepartitionFeuille repartition = allFRT.get(i);

                    // Construction des données
                    RepartitionCompleteFDTO data = decompteFeuilleJuryService.construire(repartition);

                    if (data == null) {
                        System.out.println("Données nulles pour cette répartition");
                        document.add(new Paragraph("Données incomplètes pour ce centre.", fonts.normalFont));
                        continue;
                    }

                    // Construction du document pour ce centre
                    buildBDRFDocument(document, fonts, logo, data, i+1);

                    // Nouvelle page sauf pour le dernier
                    if (i < allFRT.size() - 1) {
                        document.newPage();
                    }
                }
            }

        } catch (Exception e) {
            System.err.println("Erreur dans la génération du PDF : " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de la génération du PDF", e);
        } finally {
            if (document != null && document.isOpen()) {
                document.close();
            }
        }
    }

    private void buildBDRFDocument(Document document, FontConfiguration fonts, Image logo, RepartitionCompleteFDTO data, int a) throws DocumentException
    {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String dateHeure = LocalDate.now().format(formatter);

        // Header avec logo
        document.add(createHeader(logo, fonts, a));
        document.add(new Paragraph(" "));

        // Section Directeur et date
        document.add(createDirectorSection(fonts, dateHeure));
        document.add(new Paragraph(" "));

        // Titre principal
        document.add(createTitleSection(fonts));
        document.add(new Paragraph(" "));

        // Informations du centre
        document.add(createCentreInfoSection(fonts, data));
        document.add(new Paragraph(" "));

        // Tableau des matériels
        document.add(createMaterialTable(fonts, data));

        // Section signatures
        document.add(createSignaturesSection(fonts));
    }

    private PdfPTable createHeader(Image logo, FontConfiguration fonts, int a) throws DocumentException {
        PdfPTable header = new PdfPTable(3);
        header.setWidthPercentage(100);
        header.setWidths(new float[]{0.90f, 2f, 2.65f});

        // Cellule logo
        PdfPCell imageCell = new PdfPCell(logo);
        imageCell.setBorder(Rectangle.NO_BORDER);
        imageCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        imageCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        header.addCell(imageCell);

        // Cellule texte
        Paragraph headerText = new Paragraph(
                "REPUBLIQUE DU SENEGAL\nUn Peuple - Un But - Une Foi\n" +
                        "Ministère de l'Enseignement supérieur, \nde la Recherche et de l'Innovation" +
                        "\nOffice du Baccalauréat",
                fonts.normalFont
        );
        headerText.setLeading(14f, 0);
        headerText.setAlignment(Element.ALIGN_LEFT);

        PdfPCell textCell = new PdfPCell(headerText);
        textCell.setBorder(Rectangle.NO_BORDER);
        textCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        textCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        header.addCell(textCell);

        // Cellule référence
        DecimalFormat df = new DecimalFormat("0000");
        String numeroFormate = df.format(a); // donne "001"
        String texte = "N°_BL-CP " + numeroFormate + "/UCAD/OB/PFE/PF/aat";
        PdfPCell refCell = new PdfPCell(new Phrase(texte, fonts.boldFont));
        refCell.setBorder(Rectangle.NO_BORDER);
        refCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        refCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        header.addCell(refCell);

        return header;
    }

    private PdfPTable createDirectorSection(FontConfiguration fonts, String dateHeure) {
        PdfPTable headerTable = new PdfPTable(2);
        headerTable.setWidthPercentage(100);

        PdfPCell leftCell = new PdfPCell(new Phrase("LE DIRECTEUR", fonts.titleFont_));
        leftCell.setBorder(Rectangle.NO_BORDER);
        leftCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        leftCell.setVerticalAlignment(Element.ALIGN_MIDDLE);

        PdfPCell rightCell = new PdfPCell(new Phrase("Dakar, le " + dateHeure, fonts.normalFont_));
        rightCell.setBorder(Rectangle.NO_BORDER);
        rightCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        rightCell.setVerticalAlignment(Element.ALIGN_MIDDLE);

        headerTable.addCell(leftCell);
        headerTable.addCell(rightCell);

        return headerTable;
    }

    private PdfPTable createTitleSection(FontConfiguration fonts) {
        PdfPTable titre = new PdfPTable(1);
        titre.setWidthPercentage(100);

        PdfPCell titre1 = new PdfPCell(new Phrase("BACCALAUREAT DE L'ENSEIGNEMENT SECONDAIRE - SESSION " + getCurrentSession(), fonts.titleFont_));
        PdfPCell titre2 = new PdfPCell(new Phrase("BORDEREAU DE LIVRAISON DU MATERIEL DE COMPOSITION", fonts.titleFont_));

        titre1.setBorder(PdfPCell.NO_BORDER);
        titre1.setHorizontalAlignment(Element.ALIGN_CENTER);
        titre1.setVerticalAlignment(Element.ALIGN_MIDDLE);

        titre2.setBorder(PdfPCell.NO_BORDER);
        titre2.setHorizontalAlignment(Element.ALIGN_CENTER);
        titre2.setVerticalAlignment(Element.ALIGN_MIDDLE);

        titre.addCell(titre1);
        titre.addCell(titre2);

        return titre;
    }

    private PdfPTable createCentreInfoSection(FontConfiguration fonts, RepartitionCompleteFDTO data) {
        PdfPTable infoTable = new PdfPTable(1);
        infoTable.setWidthPercentage(100);

        addInfoRow(infoTable, "ACADEMIE : ", data.getAcademie(), fonts);
        addInfoRow(infoTable, "LOCALITE DU CENTRE PRINCIPAL : ", data.getLocalite(), fonts);
        addInfoRow(infoTable, "CENTRE : ", data.getCentre(), fonts);
        addInfoRow(infoTable, "NOMBRE DE JURY : ", String.valueOf(data.getNbJury()), fonts);
        addInfoRow(infoTable, "NOMBRE DE CANDIDATS : ", String.valueOf(data.getEffectif()), fonts);

        return infoTable;
    }

    private void addInfoRow(PdfPTable table, String label, String value, FontConfiguration fonts) {
        Paragraph paragraph = new Paragraph();
        paragraph.add(new Chunk(label, fonts.normalFont_));
        paragraph.add(new Chunk(value, fonts.headerFont));
        paragraph.setAlignment(Element.ALIGN_CENTER);

        PdfPCell cell = new PdfPCell(paragraph);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPadding(2f);
        table.addCell(cell);
    }

    private PdfPTable createMaterialTable(FontConfiguration fonts, RepartitionCompleteFDTO data) {
        PdfPTable table = new PdfPTable(7);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{2.5f, 1.5f, 1.5f, 1.5f, 1.5f, 1.5f, 1.5f});

        // Headers
        String[] headers = {
                "Matériel de composition", "Quantité prévue", "Stock",
                "Quantité remise au 1er passage", "Reliquat",
                "Quantité remise au 2ème passage", "Reliquat"
        };

        for (String h : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(h, fonts.boldFont));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setPadding(5f);
            table.addCell(cell);
        }

        // Lignes de données
        addMaterialRow(table, "Feuilles doubles", String.valueOf(data.getFd()), fonts);
        addMaterialRow(table, "Feuilles intercalaires", String.valueOf(data.getIc()), fonts);
        addMaterialRow(table, "Feuilles de brouillon", String.valueOf(data.getFb()), fonts);

        return table;
    }

    private void addMaterialRow(PdfPTable table, String material, String quantity, FontConfiguration fonts) {
        table.addCell(centeredCell(material, fonts.normalFont));
        table.addCell(centeredCell(quantity, fonts.headerFont));
        // Cellules vides
        for (int i = 0; i < 5; i++) {
            table.addCell(centeredCell("", fonts.normalFont));
        }
    }

    private PdfPTable createSignaturesSection(FontConfiguration fonts)
    {
        PdfPTable signaturesTable = new PdfPTable(2);
        signaturesTable.setWidthPercentage(100);
        signaturesTable.setSpacingBefore(15f);
        signaturesTable.setWidths(new float[]{1f, 1f});

        String signatureBase = "Date du %s :\n\n..................................................................\n\n" +
                "Prénoms et NOM du réceptionnaire :\n\n..................................................................\n\n" +
                "Téléphone :\n\n..................................................................\n\n" +
                "Adresse email :\n\n..................................................................\n\n" +
                "Signature et cachet :\n\n..................................................................";

        PdfPCell cell1 = createSignatureCell(String.format(signatureBase, "1er passage"), fonts);
        PdfPCell cell2 = createSignatureCell(String.format(signatureBase, "2ème passage"), fonts);

        signaturesTable.addCell(cell1);
        signaturesTable.addCell(cell2);

        return signaturesTable;
    }

    private PdfPCell createSignatureCell(String text, FontConfiguration fonts)
    {
        PdfPCell cell = new PdfPCell();
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPadding(10);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPhrase(new Phrase(text, fonts.normalFont_));
        return cell;
    }

    private PdfPCell centeredCell(String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(5f);
        return cell;
    }

    private Image loadAndScaleLogo() throws IOException, DocumentException
    {
        Image logo = Image.getInstance(new ClassPathResource("images/sn.png").getInputStream().readAllBytes());
        logo.scaleToFit(75f, 70f);
        return logo;
    }

    private int getCurrentSession() {
        return 2025; // À rendre dynamique selon vos besoins
    }

    /**
     * Configuration des polices utilisées dans le document
     */
    private FontConfiguration initializeFonts() {
        FontConfiguration fonts = new FontConfiguration();
        fonts.titleFont = FontFactory.getFont(FontFactory.HELVETICA, 16);
        fonts.titleFont_ = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
        fonts.normalFont = FontFactory.getFont(FontFactory.HELVETICA, 10);
        fonts.normalFont_ = FontFactory.getFont(FontFactory.HELVETICA, 12);
        fonts.boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11);
        fonts.headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
        fonts.smallFont = FontFactory.getFont(FontFactory.HELVETICA, 9);
        fonts.verySmallFont = FontFactory.getFont(FontFactory.HELVETICA, 8);
        fonts.boldSmallFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);
        return fonts;
    }
    /**
     * Construit l'intégralité du document
     */
    private void buildDocument(Document document, FontConfiguration fonts, Image logo, RepartitionCompleteDTO data) throws DocumentException {
        // En-tête avec logo
        addHeader(document, fonts, logo);
        // System.out.println("addHeader"); // LOG
        // Informations principales
        addMainInfo(document, fonts, data);
        // System.out.println("addMainInfo"); // LOG
        // Tableau des disciplines
        addDisciplinesTable(document, fonts, data);
        addFooter(document, fonts);
        addFooter_(document, fonts);
        //System.out.println("addDisciplinesTable"); // LOG
    }

    private void buildDocumentCGS(Document document, FontConfiguration fonts, Image logo, RepartitionCompleteCGSDTO data) throws DocumentException {
        // En-tête avec logo
        addHeader(document, fonts, logo);
        // System.out.println("addHeader"); // LOG
        // Informations principales
        addMainInfoCGS(document, fonts, data);
        // System.out.println("addMainInfo"); // LOG
        // Tableau des disciplines
        addDisciplinesTableCGS(document, fonts, data);
        addFooter(document, fonts);
        addFooter_(document, fonts);
        //System.out.println("addDisciplinesTable"); // LOG
    }

    private void buildDocument_(Document document, FontConfiguration fonts, Image logo, RepartitionCompleteFDTO data) throws DocumentException
    {
        addHeader_(document, fonts, logo);
        addMainInfo_(document, fonts, data);
        addDisciplinesTable_(document, fonts, data);
    }


    /**
     * Ajoute l'en-tête avec logo, texte républicain et code
     */
    private void addHeader(Document document, FontConfiguration fonts, Image logo) throws DocumentException {
        PdfPTable header = new PdfPTable(3);
        header.setWidthPercentage(100);
        header.setWidths(new float[]{0.90f, 2.85f, 3.5f});

        // Cellule logo
        PdfPCell imageCell = new PdfPCell(logo);
        imageCell.setBorder(Rectangle.NO_BORDER);
        imageCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        imageCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        header.addCell(imageCell);

        // Cellule texte
        Paragraph headerText = new Paragraph(
                "REPUBLIQUE DU SENEGAL\nUn Peuple - Un But - Une Foi\n" +
                        "Ministère de l'Enseignement supérieur, \nde la Recherche et de l'Innovation" +
                        "\nOffice du Baccalauréat",
                fonts.verySmallFont
        );
        headerText.setLeading(14f, 0);
        headerText.setAlignment(Element.ALIGN_LEFT);

        PdfPCell textCell = new PdfPCell(headerText);
        textCell.setBorder(Rectangle.NO_BORDER);
        textCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        textCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        header.addCell(textCell);

        // Cellule code académie
        PdfPCell codeAcademie = new PdfPCell(new Phrase("BORDEREAU DE CONVOYAGE\nDE SUJETS", fonts.boldFont));
        codeAcademie.setBorder(Rectangle.NO_BORDER);
        codeAcademie.setHorizontalAlignment(Element.ALIGN_CENTER);
        codeAcademie.setVerticalAlignment(Element.ALIGN_MIDDLE);
        header.addCell(codeAcademie);

        document.add(header);
    }

    private void addHeader_(Document document, FontConfiguration fonts, Image logo) throws DocumentException {
        PdfPTable header = new PdfPTable(3);
        header.setWidthPercentage(100);
        header.setWidths(new float[]{0.90f, 2.85f, 3.5f});

        // Cellule logo
        PdfPCell imageCell = new PdfPCell(logo);
        imageCell.setBorder(Rectangle.NO_BORDER);
        imageCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        imageCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        header.addCell(imageCell);

        // Cellule texte
        Paragraph headerText = new Paragraph(
                "REPUBLIQUE DU SENEGAL\nUn Peuple - Un But - Une Foi\n" +
                        "Ministère de l'Enseignement supérieur, \nde la Recherche et de l'Innovation" +
                        "\nOffice du Baccalauréat",
                fonts.verySmallFont
        );
        headerText.setLeading(14f, 0);
        headerText.setAlignment(Element.ALIGN_LEFT);

        PdfPCell textCell = new PdfPCell(headerText);
        textCell.setBorder(Rectangle.NO_BORDER);
        textCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        textCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        header.addCell(textCell);

        // Cellule code académie
        PdfPCell codeAcademie = new PdfPCell(new Phrase("N°_BL-CS 01/UCAD/OB/PFE/PF/aat", fonts.boldFont));
        codeAcademie.setBorder(Rectangle.NO_BORDER);
        codeAcademie.setHorizontalAlignment(Element.ALIGN_CENTER);
        codeAcademie.setVerticalAlignment(Element.ALIGN_MIDDLE);
        header.addCell(codeAcademie);

        document.add(header);
    }


    private void addFooter(Document document, FontConfiguration fonts) throws DocumentException {
        PdfPTable header = new PdfPTable(2);
        header.setWidthPercentage(100);
        header.setWidths(new float[]{5f, 5f});

        int a = java.time.Year.now().getValue();

        // Cellule texte
        Paragraph headerText = new Paragraph(
                "Reçu à......................................le......../......../" + a,
                fonts.boldFont
        );
        headerText.setLeading(14f, 0);
        headerText.setAlignment(Element.ALIGN_LEFT);

        PdfPCell textCell = new PdfPCell(headerText);
        textCell.setBorder(Rectangle.NO_BORDER);
        textCell.setPaddingTop(10f);
        textCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        textCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        header.addCell(textCell);

        // Cellule code académie
        PdfPCell codeAcademie = new PdfPCell(new Phrase("Par..........................................................", fonts.boldFont));
        codeAcademie.setBorder(Rectangle.NO_BORDER);
        codeAcademie.setPaddingTop(10f);
        codeAcademie.setHorizontalAlignment(Element.ALIGN_CENTER);
        codeAcademie.setVerticalAlignment(Element.ALIGN_MIDDLE);
        header.addCell(codeAcademie);

        document.add(header);
    }


    private void addFooter_(Document document, FontConfiguration fonts) throws DocumentException {
        PdfPTable header = new PdfPTable(2);
        header.setWidthPercentage(100);
        header.setWidths(new float[]{5f, 5f});

        // Cellule texte
        Paragraph headerText = new Paragraph(
                "Observations : ",
                fonts.boldFont
        );
        headerText.setLeading(14f, 0);
        headerText.setAlignment(Element.ALIGN_LEFT);

        PdfPCell textCell = new PdfPCell(headerText);
        textCell.setBorder(Rectangle.NO_BORDER);
        textCell.setPaddingTop(15f);
        textCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        textCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        header.addCell(textCell);

        // Cellule code académie
        PdfPCell codeAcademie = new PdfPCell(new Phrase("CONVOYEUR (Signature + Nom complet)", fonts.boldFont));
        codeAcademie.setBorder(Rectangle.NO_BORDER);
        codeAcademie.setPaddingTop(15f);
        codeAcademie.setHorizontalAlignment(Element.ALIGN_CENTER);
        codeAcademie.setVerticalAlignment(Element.ALIGN_MIDDLE);
        header.addCell(codeAcademie);

        document.add(header);
    }

    private void addMainInfo(Document document, FontConfiguration fonts, RepartitionCompleteDTO data) throws DocumentException {
        // Titre
        Paragraph titre = new Paragraph("BACCALAUREAT GENERAL SESSION NORMALE " + data.getSession(), fonts.boldFont);
        titre.setAlignment(Element.ALIGN_CENTER);
        titre.setSpacingBefore(5f);
        titre.setSpacingAfter(5f);
        document.add(titre);

        // Tableau principal
        PdfPTable mainInfoTable = new PdfPTable(1);
        mainInfoTable.setWidthPercentage(100);

        // Ligne Académie / Centre
        PdfPTable firstLineTable = new PdfPTable(2);
        firstLineTable.setWidthPercentage(100);
        firstLineTable.setWidths(new float[]{5f, 5f});

        String academie = data.getAcademie() != null ? data.getAcademie() : "N/A";
        String centre = data.getCentre() != null ? data.getCentre() : "N/A";

        PdfPCell academieCell = new PdfPCell(new Phrase("Académie : " + getAcademieFullName(academie), fonts.boldFont));
        academieCell.setBorder(Rectangle.NO_BORDER);
        academieCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        academieCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        academieCell.setPaddingBottom(5f);
        academieCell.setPaddingTop(5f);
        firstLineTable.addCell(academieCell);

        String result = (Boolean.TRUE.equals(data.getCp()) ? " (CP)" : "") + (Boolean.TRUE.equals(data.getCs()) ? " (CS)" : "");

        PdfPCell centreCell = new PdfPCell(new Phrase("Centre d'écrit : " + centre + result, fonts.boldFont));
        centreCell.setBorder(Rectangle.NO_BORDER);
        centreCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        academieCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        centreCell.setPaddingBottom(5f);
        centreCell.setPaddingTop(5f);
        firstLineTable.addCell(centreCell);

        mainInfoTable.addCell(new PdfPCell(firstLineTable));

        // System.out.println("FI LAA" + maxValue);

        // Sort series alphabetically for consistent display
        List<String> distinctSeries = data.getSeries();
        // System.out.println("SERIES" + distinctSeries.toString());

        int seriesCount = distinctSeries.size();
        String seriesList = "";
        if (seriesCount > 1)
        {
            seriesList = String.join(", ", distinctSeries);
        }
        if (seriesCount == 1)
        {
            seriesList = String.join("", distinctSeries);
        }

        // Format max value (adjust formatting as needed)
        String maxValueStr = String.valueOf(data.getEffectif());

        // Ligne Jury, Effectif, Nbre séries, Séries
        PdfPTable secondLineTable = new PdfPTable(4);
        secondLineTable.setWidthPercentage(100);
        secondLineTable.setWidths(new float[]{1.5f, 1f, 2f, 1.5f});

        secondLineTable.addCell(createCell("JURY : " + data.getJury(), fonts.normalFont, false));
        secondLineTable.addCell(createCell("EFF. : " + maxValueStr, fonts.normalFont, false));
        secondLineTable.addCell(createCell("NBR DE SERIE (S) : " + seriesCount, fonts.normalFont, false));
        secondLineTable.addCell(createCell("SERIE (S) : " + seriesList, fonts.normalFont, false));

        mainInfoTable.addCell(new PdfPCell(secondLineTable));

        document.add(mainInfoTable);
    }

    private void addMainInfoCGS(Document document, FontConfiguration fonts, RepartitionCompleteCGSDTO data) throws DocumentException {
        // Titre
        Paragraph titre = new Paragraph("CONCOURS GENERAL SENEGALAIS - SESSION : " + data.getSession(), fonts.boldFont);
        titre.setAlignment(Element.ALIGN_CENTER);
        titre.setSpacingBefore(5f);
        titre.setSpacingAfter(5f);
        document.add(titre);

        // Tableau principal
        PdfPTable mainInfoTable = new PdfPTable(1);
        mainInfoTable.setWidthPercentage(100);

        // Ligne Académie / Centre
        PdfPTable firstLineTable = new PdfPTable(2);
        firstLineTable.setWidthPercentage(100);
        firstLineTable.setWidths(new float[]{5f, 5f});

        String academie = data.getAcademie() != null ? data.getAcademie() : "N/A";
        String centre = data.getCentre() != null ? data.getCentre() : "N/A";

        PdfPCell academieCell = new PdfPCell(new Phrase(academie, fonts.boldFont));
        academieCell.setBorder(Rectangle.NO_BORDER);
        academieCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        academieCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        academieCell.setPaddingBottom(5f);
        academieCell.setPaddingTop(5f);
        firstLineTable.addCell(academieCell);

        PdfPCell centreCell = new PdfPCell(new Phrase("Centre d'écrit : " + centre, fonts.boldFont));
        centreCell.setBorder(Rectangle.NO_BORDER);
        centreCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        academieCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        centreCell.setPaddingBottom(5f);
        centreCell.setPaddingTop(5f);
        firstLineTable.addCell(centreCell);

        mainInfoTable.addCell(new PdfPCell(firstLineTable));

        List<String> distinctSeries = data.getSeries();

        int seriesCount = distinctSeries.size();
        String seriesList = "";
        if (seriesCount > 1)
        {
            seriesList = String.join(", ", distinctSeries);
        }
        if (seriesCount == 1)
        {
            seriesList = String.join("", distinctSeries);
        }

        // Format max value (adjust formatting as needed)
        String maxValueStr = String.valueOf(data.getEffectif());

        // Ligne Jury, Effectif, Nbre séries, Séries
        PdfPTable secondLineTable = new PdfPTable(3);
        secondLineTable.setWidthPercentage(100);
        secondLineTable.setWidths(new float[]{2f, 2f, 2f});

        secondLineTable.addCell(createCell("EFFECTIF DU CENTRE : " + maxValueStr, fonts.normalFont, false));
        // seriesCount
        secondLineTable.addCell(createCell("NBR. DE SERIE (S) : " + "-", fonts.normalFont, false));
        // seriesList
        secondLineTable.addCell(createCell("SERIE (S) : " + "TOUTES SERIES", fonts.normalFont, false));

        mainInfoTable.addCell(new PdfPCell(secondLineTable));

        document.add(mainInfoTable);
    }


    private void addMainInfo_(Document document, FontConfiguration fonts, RepartitionCompleteFDTO data) throws DocumentException {
        // Titre
        Paragraph titre = new Paragraph("BACCALAUREAT DE L'ENSEIGNEMENT SECONDAIRE - SESSION " + data.getSession(), fonts.boldFont);
        titre.setAlignment(Element.ALIGN_CENTER);
        titre.setSpacingBefore(5f);
        titre.setSpacingAfter(5f);
        document.add(titre);

        // Tableau principal
        PdfPTable mainInfoTable = new PdfPTable(1);
        mainInfoTable.setWidthPercentage(100);

        // Ligne Académie / Centre
        PdfPTable firstLineTable = new PdfPTable(2);
        firstLineTable.setWidthPercentage(100);
        firstLineTable.setWidths(new float[]{5f, 5f});

        String academie = data.getAcademie() != null ? data.getAcademie() : "N/A";
        String centre = data.getCentre() != null ? data.getCentre() : "N/A";

        PdfPCell academieCell = new PdfPCell(new Phrase("Académie : " + getAcademieFullName(academie), fonts.boldFont));
        academieCell.setBorder(Rectangle.NO_BORDER);
        academieCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        academieCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        academieCell.setPaddingBottom(5f);
        academieCell.setPaddingTop(5f);
        firstLineTable.addCell(academieCell);

        String result = (Boolean.TRUE.equals(data.getCp()) ? " (CP)" : "") + (Boolean.TRUE.equals(data.getCs()) ? " (CS)" : "");

        PdfPCell centreCell = new PdfPCell(new Phrase("Centre d'écrit : " + centre + result, fonts.boldFont));
        centreCell.setBorder(Rectangle.NO_BORDER);
        centreCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        academieCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        centreCell.setPaddingBottom(5f);
        centreCell.setPaddingTop(5f);
        firstLineTable.addCell(centreCell);

        mainInfoTable.addCell(new PdfPCell(firstLineTable));

        // Format max value (adjust formatting as needed)
        String maxValueStr = String.valueOf(data.getEffectif());

        // Ligne Jury, Effectif, Nbre séries, Séries
        PdfPTable secondLineTable = new PdfPTable(4);
        secondLineTable.setWidthPercentage(100);
        secondLineTable.setWidths(new float[]{1.5f, 1f, 2f, 1.5f});

        secondLineTable.addCell(createCell("NB. JURY : " + data.getNbJury(), fonts.normalFont, false));
        secondLineTable.addCell(createCell("EFF. : " + maxValueStr, fonts.normalFont, false));
        secondLineTable.addCell(createCell("NBR DE SERIE (S) : " , fonts.normalFont, false));
        secondLineTable.addCell(createCell("SERIE (S) : " , fonts.normalFont, false));

        mainInfoTable.addCell(new PdfPCell(secondLineTable));

        document.add(mainInfoTable);
    }


    private PdfPCell createCell(String text, Font font, boolean isBold) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPaddingBottom(5f);
        return cell;
    }


    private void addDisciplinesTable(Document document, FontConfiguration fonts, RepartitionCompleteDTO data) throws DocumentException {
        PdfPTable mainTable = new PdfPTable(4);
        mainTable.setWidthPercentage(100);
        mainTable.setWidths(new float[]{3f, 1f, 1.5f, 1.5f});
        // En-têtes du tableau
        addTableHeaders(mainTable, fonts);

        AtomicInteger totalPremierIndicator = new AtomicInteger();
        AtomicInteger totalSecondIndicator = new AtomicInteger();

        // Lignes de données
        if (data.getMatieres() != null && !data.getMatieres().isEmpty()) {

            data.getMatieres().stream()
                    .sorted(Comparator.comparing(MatiereComposeeDTO::getNom, Comparator.naturalOrder()))
                    .forEach(row -> {

                        double premier = row.getPremierGroupe();
                        String value = premier > 0 ? "1" : "0";

                        if (premier > 0) {
                            totalPremierIndicator.getAndIncrement();
                            totalSecondIndicator.getAndIncrement();
                        }

                        String champ = row.getChamp();
                        String mappedChamp;
                        List<String> target = Arrays.asList("L2", "L'1", "L1B", "L1A", "LA", "L-AR");

                        if ("matiere1".equals(champ) && row.getSeries().stream().anyMatch(target::contains))
                        {
                            mappedChamp = " - LV1";
                        }
                        else if ("matiere2".equals(champ))
                        {
                            mappedChamp = " - LV2";
                        }
                        else
                        {
                            mappedChamp = "";
                        }

                        addDisciplineRow(
                                mainTable,
                                row.getNom() + mappedChamp + " (" + String.join(", ", row.getSeries()) + ")",
                                row.getPremierGroupe().toString(),
                                value, value,
                                fonts.normalFont
                        );
                    });
        }

        else
        {
            // Ajouter une ligne indiquant l'absence de données
            PdfPCell emptyCell = new PdfPCell(new Phrase("Aucune matière à afficher", fonts.normalFont));
            emptyCell.setColspan(4);
            emptyCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            emptyCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            emptyCell.setPadding(5f);
            mainTable.addCell(emptyCell);
        }

        // Ligne total
        addTotalRow(mainTable, fonts, totalPremierIndicator.get(), totalSecondIndicator.get());
        document.add(mainTable);
    }

    private void addDisciplinesTableCGS(Document document, FontConfiguration fonts, RepartitionCompleteCGSDTO data) throws DocumentException {
        PdfPTable mainTable = new PdfPTable(5);
        mainTable.setWidthPercentage(100);
        mainTable.setWidths(new float[]{3f, 1.5f, 1.5f, 1.5f, 1.5f});
        // En-têtes du tableau
        addTableHeadersCGS(mainTable, fonts);

        AtomicInteger totalPremierIndicator = new AtomicInteger();
        AtomicInteger totalTerminaleIndicator = new AtomicInteger();

        // Lignes de données
        if (data.getMatieres() != null && !data.getMatieres().isEmpty()) {

            data.getMatieres().stream()
                    .sorted(Comparator.comparing(MatiereComposeeCGSDTO::getDiscipline, Comparator.naturalOrder()))
                    .forEach(row -> {

                        String value1 = row.getPremiere() > 0 ? "1" : "0";
                        String value2 = row.getTerminale() > 0 ? "1" : "0";

                        if (row.getPremiere() > 0)
                        {
                            totalPremierIndicator.getAndIncrement();
                        }

                        if (row.getTerminale() > 0)
                        {
                            totalTerminaleIndicator.getAndIncrement();
                        }

                        addDisciplineRowCGS(
                                mainTable,
                                row.getDiscipline(),
                                row.getPremiere().toString(),
                                row.getTerminale().toString(),
                                value1, value2,
                                fonts.normalFont
                        );
                    });
        }

        else
        {
            // Ajouter une ligne indiquant l'absence de données
            PdfPCell emptyCell = new PdfPCell(new Phrase("Aucune matière à afficher", fonts.normalFont));
            emptyCell.setColspan(5);
            emptyCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            emptyCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            emptyCell.setPadding(5f);
            mainTable.addCell(emptyCell);
        }

        // Ligne total
        addTotalRowCGS(mainTable, fonts, totalPremierIndicator.get(), totalTerminaleIndicator.get());
        document.add(mainTable);
    }


    private void addDisciplinesTable_(Document document, FontConfiguration fonts, RepartitionCompleteFDTO data) throws DocumentException {
        PdfPTable mainTable = new PdfPTable(4);
        mainTable.setWidthPercentage(100);
        mainTable.setWidths(new float[]{3f, 1f, 1.5f, 1.5f});
        // En-têtes du tableau
        addTableHeaders(mainTable, fonts);

        AtomicInteger totalPremierIndicator = new AtomicInteger();
        AtomicInteger totalSecondIndicator = new AtomicInteger();



        // Ligne total
        addTotalRow(mainTable, fonts, totalPremierIndicator.get(), totalSecondIndicator.get());
        document.add(mainTable);
    }


    /**
     * Ajoute les en-têtes du tableau
     */
    private void addTableHeaders(PdfPTable table, FontConfiguration fonts) {
        String[] headers = {"DISCIPLINES", "EFFECTIF", "ENVELOPPE\n 1ER GROUPE", "ENVELOPPE\n 2ND GROUPE"};
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, fonts.boldFont));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setPadding(5f);
            table.addCell(cell);
        }
    }

    private void addTableHeadersCGS(PdfPTable table, FontConfiguration fonts) {
        String[] headers = {"DISCIPLINES","EFFECTIFS EN\nPREMIERE", "EFFECTIFS EN\nTERMINALE", "ENVELOPPES EN\nPREMIERE", "ENVELOPPES EN\nTERMINALE", };
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, fonts.boldSmallFont));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setPadding(5f);
            table.addCell(cell);
        }
    }

    /**
     * Ajoute toutes les lignes de disciplines
     */

    /**
     * Ajoute une ligne de discipline au tableau
     */
    private void addDisciplineRow(PdfPTable table, String discipline, String effectif,
                                  String groupe1, String groupe2, Font font) {
        // Discipline
        PdfPCell discCell = new PdfPCell(new Phrase(discipline, font));
        discCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        discCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        discCell.setPadding(2f);
        table.addCell(discCell);

        // Effectif
        PdfPCell effCell = new PdfPCell(new Phrase(effectif, font));
        effCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        effCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        effCell.setPadding(2f);
        table.addCell(effCell);

        // Groupe 1
        PdfPCell g1Cell = new PdfPCell(new Phrase(groupe1, font));
        g1Cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        g1Cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        g1Cell.setPadding(2f);
        table.addCell(g1Cell);

        // Groupe 2
        PdfPCell g2Cell = new PdfPCell(new Phrase(groupe2, font));
        g2Cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        g2Cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        g2Cell.setPadding(2f);
        table.addCell(g2Cell);
    }

    private void addDisciplineRowCGS(PdfPTable table, String discipline, String env1ere,
                                     String envTle, String cdt1ere, String cdtTle, Font font) {
        // Discipline
        PdfPCell discCell = new PdfPCell(new Phrase(discipline, font));
        discCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        discCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        discCell.setPadding(2f);
        table.addCell(discCell);

        PdfPCell env1ere_ = new PdfPCell(new Phrase(env1ere, font));
        env1ere_.setHorizontalAlignment(Element.ALIGN_CENTER);
        env1ere_.setVerticalAlignment(Element.ALIGN_MIDDLE);
        env1ere_.setPadding(2f);
        table.addCell(env1ere_);

        PdfPCell envTle_ = new PdfPCell(new Phrase(envTle, font));
        envTle_.setHorizontalAlignment(Element.ALIGN_CENTER);
        envTle_.setVerticalAlignment(Element.ALIGN_MIDDLE);
        envTle_.setPadding(2f);
        table.addCell(envTle_);

        PdfPCell cdt1ere_ = new PdfPCell(new Phrase(cdt1ere, font));
        cdt1ere_.setHorizontalAlignment(Element.ALIGN_CENTER);
        cdt1ere_.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cdt1ere_.setPadding(2f);
        table.addCell(cdt1ere_);

        PdfPCell cdtTle_ = new PdfPCell(new Phrase(cdtTle, font));
        cdtTle_.setHorizontalAlignment(Element.ALIGN_CENTER);
        cdtTle_.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cdtTle_.setPadding(2f);
        table.addCell(cdtTle_);
    }

    /**
     * Ajoute la ligne TOTAL ENV
     */
    private void addTotalRow(PdfPTable table, FontConfiguration fonts, int total1, int total2) {
        PdfPCell totalLabel = new PdfPCell(new Phrase("TOTAL DES ENVELOPPES A LIVRER ", fonts.boldFont));
        totalLabel.setColspan(2);
        totalLabel.setHorizontalAlignment(Element.ALIGN_RIGHT);
        totalLabel.setPadding(2f);
        table.addCell(totalLabel);

        PdfPCell totalValue = new PdfPCell(new Phrase(String.valueOf(total1), fonts.boldFont));
        totalValue.setHorizontalAlignment(Element.ALIGN_CENTER);
        totalValue.setPadding(2f);
        table.addCell(totalValue);

        PdfPCell totalValue2 = new PdfPCell(new Phrase(String.valueOf(total2), fonts.boldFont));
        totalValue2.setHorizontalAlignment(Element.ALIGN_CENTER);
        totalValue2.setPadding(2f);
        table.addCell(totalValue2);
    }


    private void addTotalRowCGS(PdfPTable table, FontConfiguration fonts, int total1, int total2) {
        PdfPCell totalLabel = new PdfPCell(new Phrase("TOTAL DES ENVELOPPES A LIVRER DANS LE CENTRE", fonts.boldFont));
        totalLabel.setColspan(3);
        totalLabel.setHorizontalAlignment(Element.ALIGN_RIGHT);
        totalLabel.setPadding(2f);
        table.addCell(totalLabel);

        PdfPCell totalValue = new PdfPCell(new Phrase(String.valueOf(total1), fonts.boldFont));
        totalValue.setHorizontalAlignment(Element.ALIGN_CENTER);
        totalValue.setPadding(2f);
        table.addCell(totalValue);

        PdfPCell totalValue2 = new PdfPCell(new Phrase(String.valueOf(total2), fonts.boldFont));
        totalValue2.setHorizontalAlignment(Element.ALIGN_CENTER);
        totalValue2.setPadding(2f);
        table.addCell(totalValue2);
    }

    /**
     * Classes internes pour organiser les données
     */
    private static class FontConfiguration {
        Font titleFont;
        Font normalFont;
        Font titleFont_;
        Font normalFont_;
        Font boldFont;
        Font headerFont;
        Font smallFont;
        Font verySmallFont;
        Font boldSmallFont;
    }

    private static class InfoItem
    {
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
