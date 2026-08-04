package com.officedubac.project.modules.A3_DeuxiemePartie;

import com.officedubac.project.modules.A3_DeuxiemePartie.dto.ReleveA3DeuxiemePartieSaisieRequest;
import com.officedubac.project.modules.A3_DeuxiemePartie.model.ReleveA3DeuxiemePartie;
import com.officedubac.project.modules.A3_DeuxiemePartie.pdf.ReleveA3DeuxiemePartiePdfService;
import com.officedubac.project.modules.A3_DeuxiemePartie.service.ReleveA3DeuxiemePartieService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/releves-a3-2eme-partie")
public class ReleveA3DeuxiemePartieController {

    private final ReleveA3DeuxiemePartieService service;
    private final ReleveA3DeuxiemePartiePdfService pdfService;

    public ReleveA3DeuxiemePartieController(ReleveA3DeuxiemePartieService service,
                                             ReleveA3DeuxiemePartiePdfService pdfService) {
        this.service = service;
        this.pdfService = pdfService;
    }

    @PostMapping
    public ReleveA3DeuxiemePartie creer(@RequestBody ReleveA3DeuxiemePartieSaisieRequest request) {
        return service.creer(request);
    }

    @PutMapping("/{id}")
    public ReleveA3DeuxiemePartie mettreAJour(@PathVariable String id, @RequestBody ReleveA3DeuxiemePartieSaisieRequest request) {
        return service.mettreAJour(id, request);
    }

    @GetMapping("/{id}")
    public ReleveA3DeuxiemePartie obtenir(@PathVariable String id) {
        return service.obtenir(id);
    }

    @GetMapping(value = "/{id}/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> genererPdf(@PathVariable String id) {
        ReleveA3DeuxiemePartie releve = service.obtenir(id);
        byte[] pdf = pdfService.genererPdf(releve);
        String nomFichier = "releve-A3-2eme-partie-" + id + ".pdf";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + nomFichier + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}
