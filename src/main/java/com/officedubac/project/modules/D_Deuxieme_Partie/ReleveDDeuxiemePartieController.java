package com.officedubac.project.modules.D_Deuxieme_Partie;

import com.officedubac.project.modules.D_Deuxieme_Partie.dto.ReleveDDeuxiemePartieSaisieRequest;
import com.officedubac.project.modules.D_Deuxieme_Partie.model.ReleveDDeuxiemePartie;
import com.officedubac.project.modules.D_Deuxieme_Partie.pdf.ReleveDDeuxiemePartiePdfService;
import com.officedubac.project.modules.D_Deuxieme_Partie.service.ReleveDDeuxiemePartieService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/releves-d-2eme-partie")
public class ReleveDDeuxiemePartieController {

    private final ReleveDDeuxiemePartieService service;
    private final ReleveDDeuxiemePartiePdfService pdfService;

    public ReleveDDeuxiemePartieController(ReleveDDeuxiemePartieService service,
                                            ReleveDDeuxiemePartiePdfService pdfService) {
        this.service = service;
        this.pdfService = pdfService;
    }

    @PostMapping
    public ReleveDDeuxiemePartie creer(@RequestBody ReleveDDeuxiemePartieSaisieRequest request) {
        return service.creer(request);
    }

    @PutMapping("/{id}")
    public ReleveDDeuxiemePartie mettreAJour(@PathVariable String id, @RequestBody ReleveDDeuxiemePartieSaisieRequest request) {
        return service.mettreAJour(id, request);
    }

    @GetMapping("/{id}")
    public ReleveDDeuxiemePartie obtenir(@PathVariable String id) {
        return service.obtenir(id);
    }

    @GetMapping(value = "/{id}/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> genererPdf(@PathVariable String id) {
        ReleveDDeuxiemePartie releve = service.obtenir(id);
        byte[] pdf = pdfService.genererPdf(releve);
        String nomFichier = "releve-D-2eme-partie-" + id + ".pdf";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + nomFichier + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}
