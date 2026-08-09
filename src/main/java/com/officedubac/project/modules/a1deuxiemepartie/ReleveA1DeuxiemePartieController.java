package com.officedubac.project.modules.a1deuxiemepartie;

import com.officedubac.project.modules.a1deuxiemepartie.dto.ReleveA1DeuxiemePartieResume;
import com.officedubac.project.modules.a1deuxiemepartie.dto.ReleveA1DeuxiemePartieSaisieRequest;
import com.officedubac.project.modules.a1deuxiemepartie.model.ReleveA1DeuxiemePartie;
import com.officedubac.project.modules.a1deuxiemepartie.pdf.ReleveA1DeuxiemePartiePdfService;
import com.officedubac.project.modules.a1deuxiemepartie.service.ReleveA1DeuxiemePartieService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/releves-a1-2eme-partie")
public class ReleveA1DeuxiemePartieController {

    private final ReleveA1DeuxiemePartieService service;
    private final ReleveA1DeuxiemePartiePdfService pdfService;

    public ReleveA1DeuxiemePartieController(ReleveA1DeuxiemePartieService service,
                                             ReleveA1DeuxiemePartiePdfService pdfService) {
        this.service = service;
        this.pdfService = pdfService;
    }

    @GetMapping
    public Page<ReleveA1DeuxiemePartieResume> lister(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "desc") String sort,
            @RequestParam(required = false) String numeroTable
    ) {
        Sort.Direction direction = "asc".equalsIgnoreCase(sort) ? Sort.Direction.ASC : Sort.Direction.DESC;
        return service.lister(numeroTable, PageRequest.of(page, size, Sort.by(direction, "createdAt")));
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
