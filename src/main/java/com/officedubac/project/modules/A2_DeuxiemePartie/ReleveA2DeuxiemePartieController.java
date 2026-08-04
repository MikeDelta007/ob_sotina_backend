package com.officedubac.project.modules.A2_DeuxiemePartie;

import com.officedubac.project.modules.A2_DeuxiemePartie.dto.ReleveA2DeuxiemePartieSaisieRequest;
import com.officedubac.project.modules.A2_DeuxiemePartie.model.ReleveA2DeuxiemePartie;
import com.officedubac.project.modules.A2_DeuxiemePartie.pdf.ReleveA2DeuxiemePartiePdfService;
import com.officedubac.project.modules.A2_DeuxiemePartie.service.ReleveA2DeuxiemePartieService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/releves-a2-2eme-partie")
public class ReleveA2DeuxiemePartieController {

    private final ReleveA2DeuxiemePartieService service;
    private final ReleveA2DeuxiemePartiePdfService pdfService;

    public ReleveA2DeuxiemePartieController(ReleveA2DeuxiemePartieService service,
                                             ReleveA2DeuxiemePartiePdfService pdfService) {
        this.service = service;
        this.pdfService = pdfService;
    }

    @PostMapping
    public ReleveA2DeuxiemePartie creer(@RequestBody ReleveA2DeuxiemePartieSaisieRequest request) {
        return service.creer(request);
    }

    @PutMapping("/{id}")
    public ReleveA2DeuxiemePartie mettreAJour(@PathVariable String id, @RequestBody ReleveA2DeuxiemePartieSaisieRequest request) {
        return service.mettreAJour(id, request);
    }

    @GetMapping("/{id}")
    public ReleveA2DeuxiemePartie obtenir(@PathVariable String id) {
        return service.obtenir(id);
    }

    @GetMapping(value = "/{id}/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> genererPdf(@PathVariable String id) {
        ReleveA2DeuxiemePartie releve = service.obtenir(id);
        byte[] pdf = pdfService.genererPdf(releve);
        String nomFichier = "releve-A2-2eme-partie-" + id + ".pdf";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + nomFichier + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}
