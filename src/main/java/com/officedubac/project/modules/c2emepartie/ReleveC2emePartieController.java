package com.officedubac.project.modules.c2emepartie;

import com.officedubac.project.modules.c2emepartie.dto.ReleveC2emePartieResume;
import com.officedubac.project.modules.c2emepartie.dto.ReleveC2emePartieSaisieRequest;
import com.officedubac.project.modules.c2emepartie.model.ReleveC2emePartie;
import com.officedubac.project.modules.c2emepartie.pdf.ReleveC2emePartiePdfService;
import com.officedubac.project.modules.c2emepartie.service.ReleveC2emePartieService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/releves-c-2eme-partie")
public class ReleveC2emePartieController {

    private final ReleveC2emePartieService service;
    private final ReleveC2emePartiePdfService pdfService;

    public ReleveC2emePartieController(ReleveC2emePartieService service, ReleveC2emePartiePdfService pdfService) {
        this.service = service;
        this.pdfService = pdfService;
    }

    @GetMapping
    public Page<ReleveC2emePartieResume> lister(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "desc") String sort,
            @RequestParam(required = false) String numeroTable
    ) {
        Sort.Direction direction = "asc".equalsIgnoreCase(sort) ? Sort.Direction.ASC : Sort.Direction.DESC;
        return service.lister(numeroTable, PageRequest.of(page, size, Sort.by(direction, "createdAt")));
    }

    @PostMapping
    public ReleveC2emePartie creer(@RequestBody ReleveC2emePartieSaisieRequest request) {
        return service.creer(request);
    }

    @PutMapping("/{id}")
    public ReleveC2emePartie mettreAJour(@PathVariable String id, @RequestBody ReleveC2emePartieSaisieRequest request) {
        return service.mettreAJour(id, request);
    }

    @GetMapping("/{id}")
    public ReleveC2emePartie obtenir(@PathVariable String id) {
        return service.obtenir(id);
    }

    @GetMapping(value = "/{id}/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> genererPdf(@PathVariable String id) {
        ReleveC2emePartie releve = service.obtenir(id);
        byte[] pdf = pdfService.genererPdf(releve);
        String nomFichier = "releve-C-2emePartie-" + id + ".pdf";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + nomFichier + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}
