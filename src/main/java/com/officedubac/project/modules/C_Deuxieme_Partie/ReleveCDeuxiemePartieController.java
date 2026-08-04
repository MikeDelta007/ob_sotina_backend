package com.officedubac.project.modules.C_Deuxieme_Partie;

import com.officedubac.project.modules.C_Deuxieme_Partie.dto.ReleveCDeuxiemePartieSaisieRequest;
import com.officedubac.project.modules.C_Deuxieme_Partie.model.ReleveCDeuxiemePartie;
import com.officedubac.project.modules.C_Deuxieme_Partie.pdf.ReleveCDeuxiemePartiePdfService;
import com.officedubac.project.modules.C_Deuxieme_Partie.service.ReleveCDeuxiemePartieService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/releves-c-2eme-partie")
public class ReleveCDeuxiemePartieController {

    private final ReleveCDeuxiemePartieService service;
    private final ReleveCDeuxiemePartiePdfService pdfService;

    public ReleveCDeuxiemePartieController(ReleveCDeuxiemePartieService service,
                                            ReleveCDeuxiemePartiePdfService pdfService) {
        this.service = service;
        this.pdfService = pdfService;
    }

    @PostMapping
    public ReleveCDeuxiemePartie creer(@RequestBody ReleveCDeuxiemePartieSaisieRequest request) {
        return service.creer(request);
    }

    @PutMapping("/{id}")
    public ReleveCDeuxiemePartie mettreAJour(@PathVariable String id, @RequestBody ReleveCDeuxiemePartieSaisieRequest request) {
        return service.mettreAJour(id, request);
    }

    @GetMapping("/{id}")
    public ReleveCDeuxiemePartie obtenir(@PathVariable String id) {
        return service.obtenir(id);
    }

    @GetMapping(value = "/{id}/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> genererPdf(@PathVariable String id) {
        ReleveCDeuxiemePartie releve = service.obtenir(id);
        byte[] pdf = pdfService.genererPdf(releve);
        String nomFichier = "releve-C-2eme-partie-" + id + ".pdf";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + nomFichier + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}
