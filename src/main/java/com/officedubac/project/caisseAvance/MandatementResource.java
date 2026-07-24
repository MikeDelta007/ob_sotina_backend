package com.officedubac.project.caisseAvance;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/api/v1/mandatement")
@RequiredArgsConstructor
public class MandatementResource {

    private final MandatementService      mandatementService;
    private final DecaissementPdfService  pdfService;
    private final MandatementRepository  mandatementRepo;
    private final MandatementExcelService excelService;

    @GetMapping
    public ResponseEntity<List<Mandatement>> getAll() {
        return ResponseEntity.ok(mandatementService.getAll());
    }

    // ── Payer le reliquat d'un mandatement (mode AVANCE) ──
    @PutMapping("/{id}/payer-reliquat")
    public ResponseEntity<Mandatement> payerReliquat(@PathVariable String id) {
        return ResponseEntity.ok(mandatementService.payerReliquat(id));
    }

    // ── Exporter la liste des mandatements (Excel) ──
    @GetMapping("/export.xlsx")
    public void exportMandatements(HttpServletResponse response) throws IOException {
        byte[] excel = excelService.genererListe(mandatementService.getAll());

        String filename = "mandatements_caisse_avance.xlsx";
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition",
                "attachment; filename=\"" + filename + "\"; filename*=UTF-8''"
                + URLEncoder.encode(filename, StandardCharsets.UTF_8).replace("+", "%20"));
        response.setContentLength(excel.length);
        response.getOutputStream().write(excel);
    }

    // ── Télécharger le PDF de décaissement ──
    @GetMapping("/{id}/decaissement.pdf")
    public void downloadDecaissement(@PathVariable String id,
                                     HttpServletResponse response) throws IOException {
        Mandatement m = mandatementRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Mandatement introuvable : " + id));

        byte[] pdf = pdfService.genererDecaissement(m);

        String filename = "decaissement_" + (m.getFactures().isEmpty() ? id
                : m.getFactures().get(0).getNumero()) + ".pdf";

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition",
                "attachment; filename=\"" + filename + "\"; filename*=UTF-8''"
                + URLEncoder.encode(filename, StandardCharsets.UTF_8).replace("+", "%20"));
        response.setContentLength(pdf.length);
        response.getOutputStream().write(pdf);
    }

    @PostMapping(value = "/simple", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Mandatement> simple(
            @Valid @RequestPart("data")                           MandatementSimpleRequest req,
            @RequestPart(value = "pdfFacture", required = false) MultipartFile pdfFacture,
            @RequestPart(value = "pdfCheque",  required = false) MultipartFile pdfCheque,
            @RequestPart(value = "pdfCni",     required = false) MultipartFile pdfCni) {
        return ResponseEntity.ok(
            mandatementService.mandatementSimple(req, pdfFacture, pdfCheque, pdfCni));
    }

    @PostMapping(value = "/cumulatif", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Mandatement> cumulatif(
            @Valid @RequestPart("data")                          MandatementCumulatifRequest req,
            @RequestPart(value = "pdfs",    required = false)   List<MultipartFile> pdfs,
            @RequestPart(value = "cheques", required = false)   List<MultipartFile> cheques,
            @RequestPart(value = "cnis",    required = false)   List<MultipartFile> cnis) {
        return ResponseEntity.ok(
            mandatementService.mandatementCumulatif(req, pdfs, cheques, cnis));
    }
}
