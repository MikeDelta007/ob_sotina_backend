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
import java.util.stream.Collectors;

@Slf4j
@Service
public class MandatementExcelService {

    private static final DateTimeFormatter DATE_FR =
            DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.FRENCH);
    private static final DateTimeFormatter DATETIME_FR =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm", Locale.FRENCH);

    // Colonnes contenant des montants (nécessitent une largeur suffisante pour éviter les ####)
    private static final int[] COLONNES_MONTANT = {5, 6, 7, 12, 13, 14, 15};

    public byte[] genererListe(List<Mandatement> mandatements) {
        try (XSSFWorkbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Mandatements");

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

            String[] headers = {
                "Date", "N° Facture(s)", "Motif(s)", "Type", "Mode de règlement",
                "Montant total", "Avance", "Reliquat", "Reliquat payé", "Mode reliquat", "Date paiement reliquat",
                "Mode de paiement", "Décaissé", "Montant décaissé", "Solde avant", "Solde après", "Créé par",
                "Observations", "Bénéficiaire", "N° CNI"
            };
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell c = headerRow.createCell(i);
                c.setCellValue(headers[i]);
                c.setCellStyle(headerStyle);
            }

            int rowIdx = 1;
            BigDecimal totalGeneral = BigDecimal.ZERO;
            BigDecimal totalDecaisseGeneral = BigDecimal.ZERO;
            BigDecimal totalReliquatEnAttente = BigDecimal.ZERO;

            for (Mandatement m : mandatements) {
                Row row = sheet.createRow(rowIdx++);
                int col = 0;

                row.createCell(col++).setCellValue(
                    m.getDateCreation() != null ? m.getDateCreation().format(DATE_FR) : "—");

                String numeros = m.getFactures() == null ? "—" : m.getFactures().stream()
                        .map(Mandatement.FactureEmbedded::getNumero)
                        .collect(Collectors.joining("; "));
                row.createCell(col++).setCellValue(numeros);

                String motifs = m.getFactures() == null ? "—" : m.getFactures().stream()
                        .map(f -> f.getMotifLibelle() != null ? f.getMotifLibelle() : "—")
                        .collect(Collectors.joining("; "));
                row.createCell(col++).setCellValue(motifs);

                row.createCell(col++).setCellValue(
                    m.getType() == Mandatement.TypeMandatement.SIMPLE
                        ? "Simple" : "Cumulatif (" + (m.getFactures() != null ? m.getFactures().size() : 0) + ")");

                boolean estAvance = m.getTypePaiement() == Mandatement.TypePaiement.AVANCE;
                row.createCell(col++).setCellValue(estAvance ? "Avance + Reliquat" : "Totalité");

                Cell totalCell = row.createCell(col++);
                totalCell.setCellValue(m.getMontantTotal() != null ? m.getMontantTotal().doubleValue() : 0d);
                totalCell.setCellStyle(montantStyle);

                Cell avanceCell = row.createCell(col++);
                avanceCell.setCellValue(m.getMontantAvance() != null ? m.getMontantAvance().doubleValue() : 0d);
                avanceCell.setCellStyle(montantStyle);

                Cell reliquatCell = row.createCell(col++);
                reliquatCell.setCellValue(m.getMontantReliquat() != null ? m.getMontantReliquat().doubleValue() : 0d);
                reliquatCell.setCellStyle(montantStyle);

                boolean aReliquat = estAvance && m.getMontantReliquat() != null
                        && m.getMontantReliquat().compareTo(BigDecimal.ZERO) > 0;
                row.createCell(col++).setCellValue(!aReliquat ? "—" : (m.isReliquatPaye() ? "Payé" : "En attente"));
                row.createCell(col++).setCellValue(
                    m.getModePaiementReliquat() == null ? "—"
                        : (m.getModePaiementReliquat() == Mandatement.ModePaiement.ESPECES ? "Espèces" : "Chèque"));
                row.createCell(col++).setCellValue(
                    m.getDateReliquatPaye() != null ? m.getDateReliquatPaye().format(DATETIME_FR) : "—");

                row.createCell(col++).setCellValue(
                    m.getModePaiement() == Mandatement.ModePaiement.ESPECES ? "Espèces" : "Chèque");

                row.createCell(col++).setCellValue(m.isDecaisse() ? "Oui" : "Non");

                Cell decaisseCell = row.createCell(col++);
                decaisseCell.setCellValue(m.getMontantDecaisse() != null ? m.getMontantDecaisse().doubleValue() : 0d);
                decaisseCell.setCellStyle(montantStyle);

                Cell soldeAvantCell = row.createCell(col++);
                soldeAvantCell.setCellValue(m.getSoldeAvant() != null ? m.getSoldeAvant().doubleValue() : 0d);
                soldeAvantCell.setCellStyle(montantStyle);

                Cell soldeApresCell = row.createCell(col++);
                soldeApresCell.setCellValue(m.getSoldeApres() != null ? m.getSoldeApres().doubleValue() : 0d);
                soldeApresCell.setCellStyle(montantStyle);

                row.createCell(col++).setCellValue(m.getCreePar() != null ? m.getCreePar() : "—");
                row.createCell(col++).setCellValue(m.getDescription() != null ? m.getDescription() : "—");

                String beneficiaires = m.getType() == Mandatement.TypeMandatement.SIMPLE
                        ? (m.getBeneficiaire() != null ? m.getBeneficiaire() : "—")
                        : (m.getFactures() == null ? "—" : m.getFactures().stream()
                                .map(f -> f.getBeneficiaire() != null ? f.getBeneficiaire() : "—")
                                .collect(Collectors.joining("; ")));
                row.createCell(col++).setCellValue(beneficiaires);
                row.createCell(col).setCellValue(m.getNumeroCni() != null ? m.getNumeroCni() : "—");

                if (m.getMontantTotal() != null) totalGeneral = totalGeneral.add(m.getMontantTotal());
                if (m.getMontantDecaisse() != null) totalDecaisseGeneral = totalDecaisseGeneral.add(m.getMontantDecaisse());
                if (aReliquat && !m.isReliquatPaye()) totalReliquatEnAttente = totalReliquatEnAttente.add(m.getMontantReliquat());
            }

            Row totalRow = sheet.createRow(rowIdx + 1);
            Cell totalLabel = totalRow.createCell(0);
            totalLabel.setCellValue("Total général / Reliquats en attente / Total décaissé");
            totalLabel.setCellStyle(headerStyle);

            Cell totalGeneralCell = totalRow.createCell(5);
            totalGeneralCell.setCellValue(totalGeneral.doubleValue());
            totalGeneralCell.setCellStyle(totalStyle);

            Cell totalReliquatCell = totalRow.createCell(7);
            totalReliquatCell.setCellValue(totalReliquatEnAttente.doubleValue());
            totalReliquatCell.setCellStyle(totalStyle);

            Cell totalDecaisseCell = totalRow.createCell(13);
            totalDecaisseCell.setCellValue(totalDecaisseGeneral.doubleValue());
            totalDecaisseCell.setCellStyle(totalStyle);

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
                sheet.setColumnWidth(i, sheet.getColumnWidth(i) + 1500);
            }
            for (int i : COLONNES_MONTANT) {
                sheet.setColumnWidth(i, Math.max(sheet.getColumnWidth(i), 5000));
            }

            workbook.write(baos);
            return baos.toByteArray();

        } catch (IOException e) {
            log.error("Erreur génération Excel liste mandatements", e);
            throw new RuntimeException("Erreur génération Excel liste mandatements", e);
        }
    }
}
