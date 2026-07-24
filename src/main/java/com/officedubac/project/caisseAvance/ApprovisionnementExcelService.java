package com.officedubac.project.caisseAvance;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@Slf4j
@Service
public class ApprovisionnementExcelService {

    private static final DateTimeFormatter DATE_FR =
            DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.FRENCH);

    public byte[] genererListe(List<Approvisionnement> approvisionnements) {
        try (XSSFWorkbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Approvisionnements");

            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            CellStyle montantStyle = workbook.createCellStyle();
            montantStyle.setDataFormat(workbook.createDataFormat().getFormat("#,##0 \"FCFA\""));

            CellStyle totalStyle = workbook.createCellStyle();
            totalStyle.cloneStyleFrom(montantStyle);
            Font totalFont = workbook.createFont();
            totalFont.setBold(true);
            totalStyle.setFont(totalFont);

            String[] headers = {"Date", "Montant ajouté", "Solde avant", "Solde après", "Description", "Créé par"};
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell c = headerRow.createCell(i);
                c.setCellValue(headers[i]);
                c.setCellStyle(headerStyle);
            }

            int rowIdx = 1;
            BigDecimal totalApprovisionne = BigDecimal.ZERO;
            for (Approvisionnement a : approvisionnements) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(a.getDate() != null ? a.getDate().format(DATE_FR) : "—");

                Cell montantCell = row.createCell(1);
                montantCell.setCellValue(a.getMontant() != null ? a.getMontant().doubleValue() : 0d);
                montantCell.setCellStyle(montantStyle);

                Cell soldeAvantCell = row.createCell(2);
                soldeAvantCell.setCellValue(a.getSoldeAvant() != null ? a.getSoldeAvant().doubleValue() : 0d);
                soldeAvantCell.setCellStyle(montantStyle);

                Cell soldeApresCell = row.createCell(3);
                soldeApresCell.setCellValue(a.getSoldeApres() != null ? a.getSoldeApres().doubleValue() : 0d);
                soldeApresCell.setCellStyle(montantStyle);

                row.createCell(4).setCellValue(a.getDescription() != null ? a.getDescription() : "—");
                row.createCell(5).setCellValue(a.getCreePar() != null ? a.getCreePar() : "—");

                if (a.getMontant() != null) totalApprovisionne = totalApprovisionne.add(a.getMontant());
            }

            Row totalRow = sheet.createRow(rowIdx + 1);
            Cell totalLabel = totalRow.createCell(0);
            totalLabel.setCellValue("Total approvisionné");
            totalLabel.setCellStyle(headerStyle);
            Cell totalValue = totalRow.createCell(1);
            totalValue.setCellValue(totalApprovisionne.doubleValue());
            totalValue.setCellStyle(totalStyle);

            int[] colonnesMontant = {1, 2, 3};
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
                sheet.setColumnWidth(i, sheet.getColumnWidth(i) + 1500);
            }
            for (int i : colonnesMontant) {
                sheet.setColumnWidth(i, Math.max(sheet.getColumnWidth(i), 5000));
            }

            workbook.write(baos);
            return baos.toByteArray();

        } catch (IOException e) {
            log.error("Erreur génération Excel liste approvisionnements", e);
            throw new RuntimeException("Erreur génération Excel liste approvisionnements", e);
        }
    }
}
