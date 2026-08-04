package com.officedubac.project.modules.A4;

import com.officedubac.project.modules.A4.dto.ReleveA4SaisieRequest;
import com.officedubac.project.modules.A4.model.ReleveA4;
import com.officedubac.project.modules.A4.pdf.ReleveA4PdfService;
import com.officedubac.project.modules.A4.service.ReleveA4Service;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/releves-a4")
public class ReleveA4Controller {

    private final ReleveA4Service service;
    private final ReleveA4PdfService pdfService;

    public ReleveA4Controller(ReleveA4Service service, ReleveA4PdfService pdfService) {
        this.service = service;
        this.pdfService = pdfService;
    }

    @PostMapping
    public ReleveA4 creer(@RequestBody ReleveA4SaisieRequest request) {
        return service.creer(request);
    }

    @PutMapping("/{id}")
    public ReleveA4 mettreAJour(@PathVariable String id, @RequestBody ReleveA4SaisieRequest request) {
        return service.mettreAJour(id, request);
    }

    @GetMapping("/{id}")
    public ReleveA4 obtenir(@PathVariable String id) {
        return service.obtenir(id);
    }

    @GetMapping(value = "/{id}/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> genererPdf(@PathVariable String id) {
        ReleveA4 releve = service.obtenir(id);
        byte[] pdf = pdfService.genererPdf(releve);
        String nomFichier = "releve-A4-" + id + ".pdf";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + nomFichier + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}
