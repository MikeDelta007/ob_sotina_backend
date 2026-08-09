package com.officedubac.project.modules.ddeuxiemepartie;

import com.officedubac.project.modules.ddeuxiemepartie.dto.ReleveDDeuxiemePartieResume;
import com.officedubac.project.modules.ddeuxiemepartie.dto.ReleveDDeuxiemePartieSaisieRequest;
import com.officedubac.project.modules.ddeuxiemepartie.model.ReleveDDeuxiemePartie;
import com.officedubac.project.modules.ddeuxiemepartie.pdf.ReleveDDeuxiemePartiePdfService;
import com.officedubac.project.modules.ddeuxiemepartie.service.ReleveDDeuxiemePartieService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/releves-d-2eme-partie")
public class ReleveDDeuxiemePartieController {

    private final ReleveDDeuxiemePartieService service;
    private final ReleveDDeuxiemePartiePdfService pdfService;

    public ReleveDDeuxiemePartieController(ReleveDDeuxiemePartieService service, ReleveDDeuxiemePartiePdfService pdfService) {
        this.service = service;
        this.pdfService = pdfService;
    }

    @GetMapping
    public Page<ReleveDDeuxiemePartieResume> lister(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "desc") String sort,
            @RequestParam(required = false) String numeroTable
    ) {
        Sort.Direction direction = "asc".equalsIgnoreCase(sort) ? Sort.Direction.ASC : Sort.Direction.DESC;
        return service.lister(numeroTable, PageRequest.of(page, size, Sort.by(direction, "createdAt")));
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
        String nomFichier = "releve-D-2emePartie-" + id + ".pdf";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + nomFichier + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}
