package com.officedubac.project.services;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.*;
import com.officedubac.project.dto.StatistiquesBacDTO;
import com.officedubac.project.repository.SourceCandidatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
public class PdfStatService
{
    @Autowired
    private StatsService statistiqueService;

    @Autowired
    private SourceCandidatRepository sourceCandidatRepository;

    public byte[] genererTableauStatistiques() throws IOException, DocumentException {
        StatistiquesBacDTO stats = statistiqueService.calculerStatistiques();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        // Création du document PDF en paysage
        Document document = new Document(PageSize.A4.rotate(), 10, 10, 20, 20);
        PdfWriter writer = PdfWriter.getInstance(document, baos);
        document.open();

        // Titre
        Font titleFont = new Font(Font.HELVETICA, 16, Font.BOLD);
        Paragraph title = new Paragraph("STATISTIQUES GLOBALES DES INSCRITS AU BACCALAUREAT 2025", titleFont);
        title.setAlignment(Paragraph.ALIGN_CENTER);
        title.setSpacingAfter(20);
        document.add(title);

        // Création du tableau principal (6 colonnes)
        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{2f, 1.5f, 1.5f, 1.5f, 1.5f, 1.5f});

        // En-têtes
        Font headerFont = new Font(Font.HELVETICA, 10, Font.BOLD);
        String[] headers = {"Option", "Effectif filles", "% Filles", "% Public", "% Individuels", "Poids relatif"};

        // Couleur gris clair pour les en-têtes

        for (String header : headers) {
            PdfPCell headerCell = new PdfPCell(new Paragraph(header, headerFont));
            headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            headerCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            headerCell.setBorderWidth(1);
            table.addCell(headerCell);
        }

        // Ordre des séries selon votre tableau
        String[] seriesOrder = {"STEG", "F6", "T1", "T2", "STIDD", "L'1", "L1A", "L1B", "L2", "LA", "L-AR",
                "S1", "S1A", "S2", "S2A", "S3", "S4", "S5"};

        Font cellFont = new Font(Font.HELVETICA, 9, Font.NORMAL);

        for (String serie : seriesOrder) {
            StatistiquesBacDTO.OptionStat optionStat = stats.getStatsByOption().get(serie);
            if (optionStat != null) {
                // Option
                PdfPCell optionCell = new PdfPCell(new Paragraph(serie, cellFont));
                optionCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(optionCell);

                // Effectif filles
                PdfPCell fillesCell = new PdfPCell(new Paragraph(String.valueOf(optionStat.getFilles()), cellFont));
                fillesCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table.addCell(fillesCell);

                // % Filles
                PdfPCell pctFillesCell = new PdfPCell(new Paragraph(String.format("%.2f%%", optionStat.getPourcentageFilles()), cellFont));
                pctFillesCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table.addCell(pctFillesCell);

                // % Public
                PdfPCell pctPublicCell = new PdfPCell(new Paragraph(String.format("%.2f%%", optionStat.getPourcentagePublic()), cellFont));
                pctPublicCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table.addCell(pctPublicCell);

                // % Individuels
                PdfPCell pctIndivCell = new PdfPCell(new Paragraph(String.format("%.2f%%", optionStat.getPourcentageIndividuels()), cellFont));
                pctIndivCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table.addCell(pctIndivCell);

                // Poids relatif
                PdfPCell poidsCell = new PdfPCell(new Paragraph(String.format("%.2f%%", optionStat.getPoidsRelatif()), cellFont));
                poidsCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table.addCell(poidsCell);
            }
        }

        // Ligne Total
        PdfPCell totalCell = new PdfPCell(new Paragraph("Total: " + stats.getTotalGeneral() + " candidats", headerFont));
        totalCell.setColspan(6);
        totalCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        totalCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.addCell(totalCell);

        document.add(table);

        // Espacement
        document.add(new Paragraph(" "));

        // Tableau Tertiaire
        addSubTable(document, "Tertiaire (STEG)", new String[]{"STEG"},
                new StatistiquesBacDTO.OptionStat[]{stats.getStatsByOption().get("STEG")});

        // Tableau Sciences et Techniques
        String[] techSeries = {"S1", "S1A", "S2", "S2A", "S3", "S4", "S5", "F6", "T1", "T2", "STIDD"};
        StatistiquesBacDTO.OptionStat[] techStats = new StatistiquesBacDTO.OptionStat[techSeries.length];
        for (int i = 0; i < techSeries.length; i++) {
            techStats[i] = stats.getStatsByOption().get(techSeries[i]);
        }
        addSubTable(document, "Sciences et Techniques (S&T)", techSeries, techStats);

        // Tableau Littéraires
        String[] litterairesSeries = {"L'1", "L1A", "L1B", "L2", "LA", "L-AR"};
        StatistiquesBacDTO.OptionStat[] litterairesStats = new StatistiquesBacDTO.OptionStat[litterairesSeries.length];
        for (int i = 0; i < litterairesSeries.length; i++) {
            litterairesStats[i] = stats.getStatsByOption().get(litterairesSeries[i]);
        }
        addSubTable(document, "Littéraires L", litterairesSeries, litterairesStats);

        // Tableau Franco-Arabe
        String[] francoArabeSeries = {"LA", "S1A", "S2A"};
        StatistiquesBacDTO.OptionStat[] francoArabeStats = new StatistiquesBacDTO.OptionStat[francoArabeSeries.length];
        for (int i = 0; i < francoArabeSeries.length; i++) {
            francoArabeStats[i] = stats.getStatsByOption().get(francoArabeSeries[i]);
        }
        addSubTable(document, "Franco-Arabe (LA; SA)", francoArabeSeries, francoArabeStats);

        // Tableau Arabe
        addSubTable(document, "Arabe (L-AR)", new String[]{"L-AR"},
                new StatistiquesBacDTO.OptionStat[]{stats.getStatsByOption().get("L-AR")});

        // Tableau Bac Technique
        String[] techniqueSeries = {"F6", "T1", "T2", "STIDD", "STEG"};
        StatistiquesBacDTO.OptionStat[] techniqueStats = new StatistiquesBacDTO.OptionStat[techniqueSeries.length];
        for (int i = 0; i < techniqueSeries.length; i++) {
            techniqueStats[i] = stats.getStatsByOption().get(techniqueSeries[i]);
        }
        addSubTableWithDetails(document, "Baccalauréat technique", techniqueSeries, techniqueStats);

        document.close();
        writer.close();

        return baos.toByteArray();
    }

    private void addSubTable(Document document, String title, String[] seriesNames, StatistiquesBacDTO.OptionStat[] options) throws DocumentException {
        Font titleFont = new Font(Font.HELVETICA, 12, Font.BOLD);
        Paragraph titlePara = new Paragraph(title, titleFont);
        titlePara.setSpacingBefore(10);
        titlePara.setSpacingAfter(5);
        document.add(titlePara);

        PdfPTable subTable = new PdfPTable(4);
        subTable.setWidthPercentage(60);
        subTable.setWidths(new float[]{2f, 1.5f, 1.5f, 1.5f});

        Font headerFont = new Font(Font.HELVETICA, 10, Font.BOLD);
        Font cellFont = new Font(Font.HELVETICA, 9, Font.NORMAL);

        // En-têtes
        String[] headers = {"Option", "Effectif", "% Public", "% Filles"};
        for (String header : headers) {
            PdfPCell headerCell = new PdfPCell(new Paragraph(header, headerFont));
            headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            subTable.addCell(headerCell);
        }

        // Données
        for (int i = 0; i < options.length && i < seriesNames.length; i++) {
            if (options[i] != null && options[i].getEffectif() > 0) {
                PdfPCell cell1 = new PdfPCell(new Paragraph(seriesNames[i], cellFont));
                cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
                subTable.addCell(cell1);

                PdfPCell cell2 = new PdfPCell(new Paragraph(String.valueOf(options[i].getEffectif()), cellFont));
                cell2.setHorizontalAlignment(Element.ALIGN_RIGHT);
                subTable.addCell(cell2);

                PdfPCell cell3 = new PdfPCell(new Paragraph(String.format("%.2f%%", options[i].getPourcentagePublic()), cellFont));
                cell3.setHorizontalAlignment(Element.ALIGN_RIGHT);
                subTable.addCell(cell3);

                PdfPCell cell4 = new PdfPCell(new Paragraph(String.format("%.2f%%", options[i].getPourcentageFilles()), cellFont));
                cell4.setHorizontalAlignment(Element.ALIGN_RIGHT);
                subTable.addCell(cell4);
            }
        }

        document.add(subTable);
    }

    private void addSubTableWithDetails(Document document, String title, String[] seriesNames, StatistiquesBacDTO.OptionStat[] options) throws DocumentException {
        Font titleFont = new Font(Font.HELVETICA, 12, Font.BOLD);
        Paragraph titlePara = new Paragraph(title, titleFont);
        titlePara.setSpacingBefore(10);
        titlePara.setSpacingAfter(5);
        document.add(titlePara);

        PdfPTable subTable = new PdfPTable(5);
        subTable.setWidthPercentage(80);
        subTable.setWidths(new float[]{1.5f, 1.2f, 1.2f, 1.2f, 1.5f});

        Font headerFont = new Font(Font.HELVETICA, 10, Font.BOLD);
        Font cellFont = new Font(Font.HELVETICA, 9, Font.NORMAL);

        // En-têtes
        String[] headers = {"Option", "Effectif", "Filles", "Public", "% Public"};
        for (String header : headers) {
            PdfPCell headerCell = new PdfPCell(new Paragraph(header, headerFont));
            headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            subTable.addCell(headerCell);
        }

        // Données
        for (int i = 0; i < options.length && i < seriesNames.length; i++) {
            if (options[i] != null && options[i].getEffectif() > 0) {
                // Option
                PdfPCell optionCell = new PdfPCell(new Paragraph(seriesNames[i], cellFont));
                optionCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                subTable.addCell(optionCell);

                // Effectif
                PdfPCell effectifCell = new PdfPCell(new Paragraph(String.valueOf(options[i].getEffectif()), cellFont));
                effectifCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                subTable.addCell(effectifCell);

                // Filles
                PdfPCell fillesCell = new PdfPCell(new Paragraph(String.valueOf(options[i].getFilles()), cellFont));
                fillesCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                subTable.addCell(fillesCell);

                // Public
                PdfPCell publicCell = new PdfPCell(new Paragraph(String.valueOf(options[i].getPublicCount()), cellFont));
                publicCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                subTable.addCell(publicCell);

                // % Public
                PdfPCell pctPublicCell = new PdfPCell(new Paragraph(String.format("%.2f%%", options[i].getPourcentagePublic()), cellFont));
                pctPublicCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                subTable.addCell(pctPublicCell);
            }
        }

        document.add(subTable);
    }
}