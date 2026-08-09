package com.officedubac.project.modules.f1deuxiemepartie;

import com.officedubac.project.modules.f1deuxiemepartie.dto.ReleveF1DeuxiemePartieResume;
import com.officedubac.project.modules.f1deuxiemepartie.dto.ReleveF1DeuxiemePartieSaisieRequest;
import com.officedubac.project.modules.f1deuxiemepartie.model.ReleveF1DeuxiemePartie;
import com.officedubac.project.modules.f1deuxiemepartie.pdf.ReleveF1DeuxiemePartiePdfService;
import com.officedubac.project.modules.f1deuxiemepartie.service.ReleveF1DeuxiemePartieService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/releves-f1-2eme-partie")
public class ReleveF1DeuxiemePartieController {

    private final ReleveF1DeuxiemePartieService service;
    private final ReleveF1DeuxiemePartiePdfService pdfService;

    public ReleveF1DeuxiemePartieController(ReleveF1DeuxiemePartieService service, ReleveF1DeuxiemePartiePdfService pdfService) {
        this.service = service;
        this.pdfService = pdfService;
    }

    @GetMapping
    public Page<ReleveF1DeuxiemePartieResume> lister(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "desc") String sort,
            @RequestParam(required = false) String numeroTable
    ) {
        Sort.Direction direction = "asc".equalsIgnoreCase(sort) ? Sort.Direction.ASC : Sort.Direction.DESC;
        return service.lister(numeroTable, PageRequest.of(page, size, Sort.by(direction, "createdAt")));
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
        String nomFichier = "releve-F1-2emePartie-" + id + ".pdf";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + nomFichier + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}
