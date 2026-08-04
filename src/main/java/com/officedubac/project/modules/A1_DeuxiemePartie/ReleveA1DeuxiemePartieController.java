package com.officedubac.project.modules.A1_DeuxiemePartie;

import com.officedubac.project.modules.A1_DeuxiemePartie.dto.ReleveA1DeuxiemePartieSaisieRequest;
import com.officedubac.project.modules.A1_DeuxiemePartie.model.ReleveA1DeuxiemePartie;
import com.officedubac.project.modules.A1_DeuxiemePartie.pdf.ReleveA1DeuxiemePartiePdfService;
import com.officedubac.project.modules.A1_DeuxiemePartie.service.ReleveA1DeuxiemePartieService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/releves-a1-2eme-partie")
public class ReleveA1DeuxiemePartieController {

    private final ReleveA1DeuxiemePartieService service;
    private final ReleveA1DeuxiemePartiePdfService pdfService;

    public ReleveA1DeuxiemePartieController(ReleveA1DeuxiemePartieService service,
                                             ReleveA1DeuxiemePartiePdfService pdfService) {
        this.service = service;
        this.pdfService = pdfService;
    }

    @PostMapping
    public ReleveA1DeuxiemePartie creer(@RequestBody ReleveA1DeuxiemePartieSaisieRequest request) {
        return service.creer(request);
    }

    @PutMapping("/{id}")
    public ReleveA1DeuxiemePartie mettreAJour(@PathVariable String id, @RequestBody ReleveA1DeuxiemePartieSaisieRequest request) {
        return service.mettreAJour(id, request);
    }

    @GetMapping("/{id}")
    public ReleveA1DeuxiemePartie obtenir(@PathVariable String id) {
        return service.obtenir(id);
    }

    @GetMapping(value = "/{id}/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> genererPdf(@PathVariable String id) {
        ReleveA1DeuxiemePartie releve = service.obtenir(id);
        byte[] pdf = pdfService.genererPdf(releve);
        String nomFichier = "releve-A1-2eme-partie-" + id + ".pdf";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + nomFichier + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}
