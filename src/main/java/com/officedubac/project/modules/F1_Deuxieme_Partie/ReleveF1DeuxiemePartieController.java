package com.officedubac.project.modules.F1_Deuxieme_Partie;

import com.officedubac.project.modules.F1_Deuxieme_Partie.dto.ReleveF1DeuxiemePartieSaisieRequest;
import com.officedubac.project.modules.F1_Deuxieme_Partie.model.ReleveF1DeuxiemePartie;
import com.officedubac.project.modules.F1_Deuxieme_Partie.pdf.ReleveF1DeuxiemePartiePdfService;
import com.officedubac.project.modules.F1_Deuxieme_Partie.service.ReleveF1DeuxiemePartieService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/releves-f1-2eme-partie")
public class ReleveF1DeuxiemePartieController {

    private final ReleveF1DeuxiemePartieService service;
    private final ReleveF1DeuxiemePartiePdfService pdfService;

    public ReleveF1DeuxiemePartieController(ReleveF1DeuxiemePartieService service,
                                             ReleveF1DeuxiemePartiePdfService pdfService) {
        this.service = service;
        this.pdfService = pdfService;
    }

    @PostMapping
    public ReleveF1DeuxiemePartie creer(@RequestBody ReleveF1DeuxiemePartieSaisieRequest request) {
        return service.creer(request);
    }

    @PutMapping("/{id}")
    public ReleveF1DeuxiemePartie mettreAJour(@PathVariable String id, @RequestBody ReleveF1DeuxiemePartieSaisieRequest request) {
        return service.mettreAJour(id, request);
    }

    @GetMapping("/{id}")
    public ReleveF1DeuxiemePartie obtenir(@PathVariable String id) {
        return service.obtenir(id);
    }

    @GetMapping(value = "/{id}/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> genererPdf(@PathVariable String id) {
        ReleveF1DeuxiemePartie releve = service.obtenir(id);
        byte[] pdf = pdfService.genererPdf(releve);
        String nomFichier = "releve-F1-2eme-partie-" + id + ".pdf";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + nomFichier + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}
