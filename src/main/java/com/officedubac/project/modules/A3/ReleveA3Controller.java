package com.officedubac.project.modules.A3;

import com.officedubac.project.modules.A3.dto.ReleveA3SaisieRequest;
import com.officedubac.project.modules.A3.model.ReleveA3;
import com.officedubac.project.modules.A3.pdf.ReleveA3PdfService;
import com.officedubac.project.modules.A3.service.ReleveA3Service;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/releves-a3")
public class ReleveA3Controller {

    private final ReleveA3Service service;
    private final ReleveA3PdfService pdfService;

    public ReleveA3Controller(ReleveA3Service service, ReleveA3PdfService pdfService) {
        this.service = service;
        this.pdfService = pdfService;
    }

    @PostMapping
    public ReleveA3 creer(@RequestBody ReleveA3SaisieRequest request) {
        return service.creer(request);
    }

    @PutMapping("/{id}")
    public ReleveA3 mettreAJour(@PathVariable String id, @RequestBody ReleveA3SaisieRequest request) {
        return service.mettreAJour(id, request);
    }

    @GetMapping("/{id}")
    public ReleveA3 obtenir(@PathVariable String id) {
        return service.obtenir(id);
    }

    @GetMapping(value = "/{id}/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> genererPdf(@PathVariable String id) {
        ReleveA3 releve = service.obtenir(id);
        byte[] pdf = pdfService.genererPdf(releve);
        String nomFichier = "releve-A3-" + id + ".pdf";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + nomFichier + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}
