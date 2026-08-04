package com.officedubac.project.modules.D;

import com.officedubac.project.modules.D.dto.ReleveDSaisieRequest;
import com.officedubac.project.modules.D.model.ReleveD;
import com.officedubac.project.modules.D.pdf.ReleveDPdfService;
import com.officedubac.project.modules.D.service.ReleveDService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/releves-d")
public class ReleveDController {

    private final ReleveDService service;
    private final ReleveDPdfService pdfService;

    public ReleveDController(ReleveDService service, ReleveDPdfService pdfService) {
        this.service = service;
        this.pdfService = pdfService;
    }

    @PostMapping
    public ReleveD creer(@RequestBody ReleveDSaisieRequest request) {
        return service.creer(request);
    }

    @PutMapping("/{id}")
    public ReleveD mettreAJour(@PathVariable String id, @RequestBody ReleveDSaisieRequest request) {
        return service.mettreAJour(id, request);
    }

    @GetMapping("/{id}")
    public ReleveD obtenir(@PathVariable String id) {
        return service.obtenir(id);
    }

    @GetMapping(value = "/{id}/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> genererPdf(@PathVariable String id) {
        ReleveD releve = service.obtenir(id);
        byte[] pdf = pdfService.genererPdf(releve);
        String nomFichier = "releve-D-" + id + ".pdf";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + nomFichier + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}
