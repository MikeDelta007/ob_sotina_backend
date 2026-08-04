package com.officedubac.project.modules.F2;

import com.officedubac.project.modules.F2.dto.ReleveF2SaisieRequest;
import com.officedubac.project.modules.F2.model.ReleveF2;
import com.officedubac.project.modules.F2.pdf.ReleveF2PdfService;
import com.officedubac.project.modules.F2.service.ReleveF2Service;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/releves-f2")
public class ReleveF2Controller {

    private final ReleveF2Service service;
    private final ReleveF2PdfService pdfService;

    public ReleveF2Controller(ReleveF2Service service, ReleveF2PdfService pdfService) {
        this.service = service;
        this.pdfService = pdfService;
    }

    @PostMapping
    public ReleveF2 creer(@RequestBody ReleveF2SaisieRequest request) {
        return service.creer(request);
    }

    @PutMapping("/{id}")
    public ReleveF2 mettreAJour(@PathVariable String id, @RequestBody ReleveF2SaisieRequest request) {
        return service.mettreAJour(id, request);
    }

    @GetMapping("/{id}")
    public ReleveF2 obtenir(@PathVariable String id) {
        return service.obtenir(id);
    }

    @GetMapping(value = "/{id}/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> genererPdf(@PathVariable String id) {
        ReleveF2 releve = service.obtenir(id);
        byte[] pdf = pdfService.genererPdf(releve);
        String nomFichier = "releve-F2-" + id + ".pdf";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + nomFichier + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}
