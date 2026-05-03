package com.officedubac.project.services;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;

public class ExportExcelService
{
    /**
     * Exporte toutes les statistiques vers un fichier Excel multi-feuilles
     */
    public byte[] exportAllStatisticsToExcel(Integer session) throws IOException {
        // Récupérer les données
        StatistiquesGlobalesDTO globales = statistiquesBacService.getStatistiquesGlobales(session);
        StatistiquesParOptionDTO parOption = statistiquesBacService.getStatistiquesParOption(session);
        StatistiquesParCategorieDTO parCategorie = statistiquesBacService.getStatistiquesParCategorie(session);

        // Créer le classeur Excel
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            // Feuille 1 : Synthèse globale
            createSynthèseGlobaleSheet(workbook, globales, parCategorie, session);

            // Feuille 2 : Détail par option (série)
            createDetailParOptionSheet(workbook, parOption, session);

            // Feuille 3 : Répartition par catégorie
            createRepartitionParCategorieSheet(workbook, parCategorie, session);

            // Feuille 4 : Effectifs par genre
            createEffectifsParGenreSheet(workbook, parOption, session);

            // Feuille 5 : Répartition Public/Privé
            createRepartitionPublicPriveSheet(workbook, parOption, session);

            workbook.write(out);
            return out.toByteArray();
        }
    }

    /**
     * Feuille 1 : Synthèse globale
     */
    private void createSynthèseGlobaleSheet(Workbook workbook, StatistiquesGlobalesDTO globales,
                                            StatistiquesParCategorieDTO parCategorie, Integer session) {
        Sheet sheet = workbook.createSheet("1_Synthese_Globale");
        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle boldStyle = createBoldStyle(workbook);
        CellStyle numberStyle = createNumberStyle(workbook);

        int rowNum = 0;

        // Titre
        Row titleRow = sheet.createRow(rowNum++);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("STATISTIQUES GLOBALES DU BACCALAUREAT - SESSION " + session);
        titleCell.setCellStyle(headerStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 3));

        rowNum++;

        // En-têtes
        Row headerRow = sheet.createRow(rowNum++);
        createCell(headerRow, 0, "Indicateur", headerStyle);
        createCell(headerRow, 1, "Effectif Total", headerStyle);
        createCell(headerRow, 2, "Pourcentage Public", headerStyle);
        createCell(headerRow, 3, "Poids Relatif", headerStyle);

        // Ligne Effectif total
        Row totalRow = sheet.createRow(rowNum++);
        createCell(totalRow, 0, "Effectif Total Général", boldStyle);
        createCell(totalRow, 1, String.valueOf(parCategorie.getEffectifTotal()), numberStyle);
        createCell(totalRow, 2, "", null);
        createCell(totalRow, 3, "100%", numberStyle);

        rowNum++;

        // En-têtes catégories
        Row catHeaderRow = sheet.createRow(rowNum++);
        createCell(catHeaderRow, 0, "Catégorie", headerStyle);
        createCell(catHeaderRow, 1, "Effectif", headerStyle);
        createCell(catHeaderRow, 2, "% Filles", headerStyle);
        createCell(catHeaderRow, 3, "% Public", headerStyle);
        createCell(catHeaderRow, 4, "Poids Relatif", headerStyle);

        // STEG
        addCategoryRow(sheet, rowNum++, "Tertiaire (STEG)", globales.getSteg(), parCategorie.getTertiaire());
        // Sciences
        addCategoryRow(sheet, rowNum++, "Sciences & Techniques", globales.getSciences(), parCategorie.getSciencesTechniques());
        // Littéraires
        addCategoryRow(sheet, rowNum++, "Littéraires", globales.getLitteraires(), parCategorie.getLitteraires());

        // Ajuster les largeurs
        for (int i = 0; i <= 4; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private void addCategoryRow(Sheet sheet, int rowNum, String category, SerieStatsDTO stats, CategorieStatDTO catStat) {
        Row row = sheet.createRow(rowNum);
        createCell(row, 0, category, null);
        createCell(row, 1, String.valueOf(catStat != null ? catStat.getEffectif() : stats.getEffectifTotal()), createNumberStyle(sheet.getWorkbook()));
        createCell(row, 2, String.format("%.2f%%", stats.getPourcentageFilles()), createPercentStyle(sheet.getWorkbook()));
        createCell(row, 3, String.format("%.2f%%", catStat != null ? catStat.getPourcentagePublic() : 0), createPercentStyle(sheet.getWorkbook()));
        createCell(row, 4, String.format("%.2f%%", catStat != null ? catStat.getPoidsRelatif() : 0), createPercentStyle(sheet.getWorkbook()));
    }

    /**
     * Feuille 2 : Détail par option (série)
     */
    private void createDetailParOptionSheet(Workbook workbook, StatistiquesParOptionDTO parOption, Integer session) {
        Sheet sheet = workbook.createSheet("2_Detail_Par_Option");
        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle percentStyle = createPercentStyle(workbook);
        CellStyle numberStyle = createNumberStyle(workbook);

        int rowNum = 0;

        // Titre
        Row titleRow = sheet.createRow(rowNum++);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("DÉTAIL PAR OPTION - SESSION " + session);
        titleCell.setCellStyle(headerStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 7));

        rowNum++;

        // En-têtes
        Row headerRow = sheet.createRow(rowNum++);
        createCell(headerRow, 0, "Option", headerStyle);
        createCell(headerRow, 1, "Effectif Total", headerStyle);
        createCell(headerRow, 2, "Filles", headerStyle);
        createCell(headerRow, 3, "Garçons", headerStyle);
        createCell(headerRow, 4, "% Filles", headerStyle);
        createCell(headerRow, 5, "% Public", headerStyle);
        createCell(headerRow, 6, "Poids Relatif", headerStyle);

        // Remplir les données
        for (SerieStatDetail stat : parOption.getOptions()) {
            Row row = sheet.createRow(rowNum++);
            createCell(row, 0, stat.getSerie(), null);
            createCell(row, 1, String.valueOf(stat.getEffectifTotal()), numberStyle);
            createCell(row, 2, String.valueOf(stat.getEffectifFilles()), numberStyle);
            createCell(row, 3, String.valueOf(stat.getEffectifGarcons()), numberStyle);
            createCell(row, 4, String.format("%.2f%%", stat.getPourcentageFilles()), percentStyle);
            createCell(row, 5, String.format("%.2f%%", stat.getPourcentagePublic()), percentStyle);
            createCell(row, 6, String.format("%.2f%%", stat.getPoidsRelatif()), percentStyle);
        }

        // Ligne total
        Row totalRow = sheet.createRow(rowNum);
        createCell(totalRow, 0, "TOTAL", createBoldStyle(workbook));
        createCell(totalRow, 1, String.valueOf(parOption.getEffectifTotal()), numberStyle);
        createCell(totalRow, 6, "100%", percentStyle);

        for (int i = 0; i <= 6; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    /**
     * Feuille 3 : Répartition par catégorie
     */
    private void createRepartitionParCategorieSheet(Workbook workbook, StatistiquesParCategorieDTO data, Integer session) {
        Sheet sheet = workbook.createSheet("3_Repartition_Categorie");
        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle percentStyle = createPercentStyle(workbook);
        CellStyle numberStyle = createNumberStyle(workbook);

        int rowNum = 0;

        // Titre
        Row titleRow = sheet.createRow(rowNum++);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("RÉPARTITION PAR CATÉGORIE - SESSION " + session);
        titleCell.setCellStyle(headerStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 4));

        rowNum++;

        // En-têtes
        Row headerRow = sheet.createRow(rowNum++);
        createCell(headerRow, 0, "Catégorie", headerStyle);
        createCell(headerRow, 1, "Effectif", headerStyle);
        createCell(headerRow, 2, "% Public", headerStyle);
        createCell(headerRow, 3, "Poids Relatif", headerStyle);

        // Tertiaire
        addCategorieRow(sheet, rowNum++, "Tertiaire (STEG)", data.getTertiaire());
        // Sciences
        addCategorieRow(sheet, rowNum++, "Sciences & Techniques", data.getSciencesTechniques());
        // Littéraires
        addCategorieRow(sheet, rowNum++, "Littéraires", data.getLitteraires());
        // Franco-Arabe
        addCategorieRowSimple(sheet, rowNum++, "Franco-Arabe", data.getFrancoArabe());
        // Arabe
        addCategorieRowSimple(sheet, rowNum++, "Arabe", data.getArabe());

        // Total
        Row totalRow = sheet.createRow(rowNum);
        createCell(totalRow, 0, "TOTAL", createBoldStyle(workbook));
        createCell(totalRow, 1, String.valueOf(data.getEffectifTotal()), numberStyle);
        createCell(totalRow, 2, String.format("%.2f%%", data.getPourcentagePublic()), percentStyle);
        createCell(totalRow, 3, "100%", percentStyle);

        for (int i = 0; i <= 3; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private void addCategorieRow(Sheet sheet, int rowNum, String category, CategorieStatDTO stat) {
        Row row = sheet.createRow(rowNum);
        createCell(row, 0, category, null);
        createCell(row, 1, String.valueOf(stat.getEffectif()), createNumberStyle(sheet.getWorkbook()));
        createCell(row, 2, String.format("%.2f%%", stat.getPourcentagePublic()), createPercentStyle(sheet.getWorkbook()));
        createCell(row, 3, String.format("%.2f%%", stat.getPoidsRelatif()), createPercentStyle(sheet.getWorkbook()));
    }

    private void addCategorieRowSimple(Sheet sheet, int rowNum, String category, CategorieStatSimpleDTO stat) {
        Row row = sheet.createRow(rowNum);
        createCell(row, 0, category, null);
        createCell(row, 1, String.valueOf(stat.getEffectif()), createNumberStyle(sheet.getWorkbook()));
        createCell(row, 2, "", null);
        createCell(row, 3, String.format("%.2f%%", stat.getPoidsRelatif()), createPercentStyle(sheet.getWorkbook()));
    }

    /**
     * Feuille 4 : Effectifs par genre
     */
    private void createEffectifsParGenreSheet(Workbook workbook, StatistiquesParOptionDTO parOption, Integer session) {
        Sheet sheet = workbook.createSheet("4_Effectifs_Par_Genre");
        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle numberStyle = createNumberStyle(workbook);
        CellStyle percentStyle = createPercentStyle(workbook);

        int rowNum = 0;

        Row titleRow = sheet.createRow(rowNum++);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("EFFECTIFS PAR GENRE - SESSION " + session);
        titleCell.setCellStyle(headerStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 3));

        rowNum++;

        Row headerRow = sheet.createRow(rowNum++);
        createCell(headerRow, 0, "Option", headerStyle);
        createCell(headerRow, 1, "Filles", headerStyle);
        createCell(headerRow, 2, "Garçons", headerStyle);
        createCell(headerRow, 3, "Total", headerStyle);

        long totalFilles = 0;
        long totalGarcons = 0;

        for (SerieStatDetail stat : parOption.getOptions()) {
            Row row = sheet.createRow(rowNum++);
            createCell(row, 0, stat.getSerie(), null);
            createCell(row, 1, String.valueOf(stat.getEffectifFilles()), numberStyle);
            createCell(row, 2, String.valueOf(stat.getEffectifGarcons()), numberStyle);
            createCell(row, 3, String.valueOf(stat.getEffectifTotal()), numberStyle);
            totalFilles += stat.getEffectifFilles();
            totalGarcons += stat.getEffectifGarcons();
        }

        Row totalRow = sheet.createRow(rowNum);
        createCell(totalRow, 0, "TOTAL", createBoldStyle(workbook));
        createCell(totalRow, 1, String.valueOf(totalFilles), numberStyle);
        createCell(totalRow, 2, String.valueOf(totalGarcons), numberStyle);
        createCell(totalRow, 3, String.valueOf(totalFilles + totalGarcons), numberStyle);

        for (int i = 0; i <= 3; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    /**
     * Feuille 5 : Répartition Public/Privé
     */
    private void createRepartitionPublicPriveSheet(Workbook workbook, StatistiquesParOptionDTO parOption, Integer session) {
        Sheet sheet = workbook.createSheet("5_Repartition_Public_Prive");
        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle numberStyle = createNumberStyle(workbook);
        CellStyle percentStyle = createPercentStyle(workbook);

        int rowNum = 0;

        Row titleRow = sheet.createRow(rowNum++);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("RÉPARTITION PUBLIC/PRIVÉ - SESSION " + session);
        titleCell.setCellStyle(headerStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 3));

        rowNum++;

        Row headerRow = sheet.createRow(rowNum++);
        createCell(headerRow, 0, "Option", headerStyle);
        createCell(headerRow, 1, "Effectif Public", headerStyle);
        createCell(headerRow, 2, "Effectif Privé", headerStyle);
        createCell(headerRow, 3, "% Public", headerStyle);

        for (SerieStatDetail stat : parOption.getOptions()) {
            Row row = sheet.createRow(rowNum++);
            long effectifPrive = stat.getEffectifTotal() - stat.getEffectifPublic();
            createCell(row, 0, stat.getSerie(), null);
            createCell(row, 1, String.valueOf(stat.getEffectifPublic()), numberStyle);
            createCell(row, 2, String.valueOf(effectifPrive), numberStyle);
            createCell(row, 3, String.format("%.2f%%", stat.getPourcentagePublic()), percentStyle);
        }

        for (int i = 0; i <= 3; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    // ==================== STYLES ET UTILITAIRES ====================

    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 12);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    private CellStyle createBoldStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        return style;
    }

    private CellStyle createNumberStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setDataFormat(workbook.createDataFormat().getFormat("#,##0"));
        return style;
    }

    private CellStyle createPercentStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setDataFormat(workbook.createDataFormat().getFormat("0.00%"));
        return style;
    }

    private void createCell(Row row, int col, String value, CellStyle style) {
        Cell cell = row.createCell(col);
        cell.setCellValue(value);
        if (style != null) {
            cell.setCellStyle(style);
        }
    }

    private void createCell(Row row, int col, String value, CellStyle style, String format) {
        Cell cell = row.createCell(col);
        cell.setCellValue(value);
        if (style != null) {
            cell.setCellStyle(style);
        }
    }
}
