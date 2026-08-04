package com.officedubac.project.modules.E;

import com.officedubac.project.modules.E.dto.ReleveESaisieRequest;
import com.officedubac.project.modules.E.model.ReleveE;
import com.officedubac.project.modules.E.pdf.ReleveEPdfService;
import com.officedubac.project.modules.E.service.ReleveEService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/releves-e")
public class ReleveEController {

    private final ReleveEService service;
    private final ReleveEPdfService pdfService;

    public ReleveEController(ReleveEService service, ReleveEPdfService pdfService) {
        this.service = service;
        this.pdfService = pdfService;
    }

    @PostMapping
    public ReleveE creer(@RequestBody ReleveESaisieRequest request) {
        return service.creer(request);
    }

    @PutMapping("/{id}")
    public ReleveE mettreAJour(@PathVariable String id, @RequestBody ReleveESaisieRequest request) {
        return service.mettreAJour(id, request);
    }

    @GetMapping("/{id}")
    public ReleveE obtenir(@PathVariable String id) {
        return service.obtenir(id);
    }

    @GetMapping(value = "/{id}/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> genererPdf(@PathVariable String id) {
        ReleveE releve = service.obtenir(id);
        byte[] pdf = pdfService.genererPdf(releve);
        String nomFichier = "releve-E-" + id + ".pdf";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + nomFichier + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}
