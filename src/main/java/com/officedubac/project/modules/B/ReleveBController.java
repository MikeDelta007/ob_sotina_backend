package com.officedubac.project.modules.B;

import com.officedubac.project.modules.B.dto.ReleveBSaisieRequest;
import com.officedubac.project.modules.B.model.ReleveB;
import com.officedubac.project.modules.B.pdf.ReleveBPdfService;
import com.officedubac.project.modules.B.service.ReleveBService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/releves-b")
public class ReleveBController {

    private final ReleveBService service;
    private final ReleveBPdfService pdfService;

    public ReleveBController(ReleveBService service, ReleveBPdfService pdfService) {
        this.service = service;
        this.pdfService = pdfService;
    }

    @PostMapping
    public ReleveB creer(@RequestBody ReleveBSaisieRequest request) {
        return service.creer(request);
    }

    @PutMapping("/{id}")
    public ReleveB mettreAJour(@PathVariable String id, @RequestBody ReleveBSaisieRequest request) {
        return service.mettreAJour(id, request);
    }

    @GetMapping("/{id}")
    public ReleveB obtenir(@PathVariable String id) {
        return service.obtenir(id);
    }

    @GetMapping(value = "/{id}/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> genererPdf(@PathVariable String id) {
        ReleveB releve = service.obtenir(id);
        byte[] pdf = pdfService.genererPdf(releve);
        String nomFichier = "releve-B-" + id + ".pdf";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + nomFichier + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}
