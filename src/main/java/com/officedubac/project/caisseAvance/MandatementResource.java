package com.officedubac.project.caisseAvance;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    public ResponseEntity<List<Mandatement>> getAll(
            @RequestParam(required = false) Integer annee,
            @RequestParam(required = false) Integer mois,
            @RequestParam(required = false) Integer semaine) {
        return ResponseEntity.ok(mandatementService.getAll(annee, mois, semaine));
    }

    // ── Payer le reliquat d'un mandatement (mode AVANCE) — chèque + CNI requis si le
    //    montant du reliquat impose un paiement par chèque ──
    @PreAuthorize("hasAnyAuthority('CHEF_COMPTABLE','AGENT_COMPTABLE','ADMIN')")
    @PutMapping(value = "/{id}/payer-reliquat", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Mandatement> payerReliquat(
            @PathVariable String id,
            @RequestParam(value = "piecesJustificatives", required = false) MultipartFile piecesJustificatives) {
        return ResponseEntity.ok(mandatementService.payerReliquat(id, piecesJustificatives));
    }

    // ── Exporter la liste des mandatements (Excel) ──
    @GetMapping("/export.xlsx")
    public void exportMandatements(
            @RequestParam(required = false) Integer annee,
            @RequestParam(required = false) Integer mois,
            @RequestParam(required = false) Integer semaine,
            HttpServletResponse response) throws IOException {
        byte[] excel = excelService.genererListe(mandatementService.getAll(annee, mois, semaine));

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

    @PreAuthorize("hasAnyAuthority('CHEF_COMPTABLE','AGENT_COMPTABLE','ADMIN')")
    @PostMapping(value = "/simple", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Mandatement> simple(
            @Valid @RequestPart("data") MandatementSimpleRequest req,
            @RequestPart(value = "piecesJustificatives", required = false) MultipartFile piecesJustificatives) {
        return ResponseEntity.ok(
            mandatementService.mandatementSimple(req, piecesJustificatives));
    }

    @PreAuthorize("hasAnyAuthority('CHEF_COMPTABLE','AGENT_COMPTABLE','ADMIN')")
    @PostMapping(value = "/cumulatif", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Mandatement> cumulatif(
            @Valid @RequestPart("data") MandatementCumulatifRequest req,
            @RequestPart(value = "pieces", required = false) List<MultipartFile> pieces) {
        return ResponseEntity.ok(
            mandatementService.mandatementCumulatif(req, pieces));
    }
}
