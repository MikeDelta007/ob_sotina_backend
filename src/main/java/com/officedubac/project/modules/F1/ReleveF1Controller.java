package com.officedubac.project.modules.F1;

import com.officedubac.project.modules.F1.dto.ReleveF1SaisieRequest;
import com.officedubac.project.modules.F1.model.ReleveF1;
import com.officedubac.project.modules.F1.pdf.ReleveF1PdfService;
import com.officedubac.project.modules.F1.service.ReleveF1Service;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/releves-f1")
public class ReleveF1Controller {

    private final ReleveF1Service service;
    private final ReleveF1PdfService pdfService;

    public ReleveF1Controller(ReleveF1Service service, ReleveF1PdfService pdfService) {
        this.service = service;
        this.pdfService = pdfService;
    }

    @PostMapping
    public ReleveF1 creer(@RequestBody ReleveF1SaisieRequest request) {
        return service.creer(request);
    }

    @PutMapping("/{id}")
    public ReleveF1 mettreAJour(@PathVariable String id, @RequestBody ReleveF1SaisieRequest request) {
        return service.mettreAJour(id, request);
    }

    @GetMapping("/{id}")
    public ReleveF1 obtenir(@PathVariable String id) {
        return service.obtenir(id);
    }

    @GetMapping(value = "/{id}/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> genererPdf(@PathVariable String id) {
        ReleveF1 releve = service.obtenir(id);
        byte[] pdf = pdfService.genererPdf(releve);
        String nomFichier = "releve-F1-" + id + ".pdf";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + nomFichier + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}
