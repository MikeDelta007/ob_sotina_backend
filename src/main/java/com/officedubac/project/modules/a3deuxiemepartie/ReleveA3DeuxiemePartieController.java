package com.officedubac.project.modules.a3deuxiemepartie;

import com.officedubac.project.modules.a3deuxiemepartie.dto.ReleveA3DeuxiemePartieResume;
import com.officedubac.project.modules.a3deuxiemepartie.dto.ReleveA3DeuxiemePartieSaisieRequest;
import com.officedubac.project.modules.a3deuxiemepartie.model.ReleveA3DeuxiemePartie;
import com.officedubac.project.modules.a3deuxiemepartie.pdf.ReleveA3DeuxiemePartiePdfService;
import com.officedubac.project.modules.a3deuxiemepartie.service.ReleveA3DeuxiemePartieService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/releves-a3-2eme-partie")
public class ReleveA3DeuxiemePartieController {

    private final ReleveA3DeuxiemePartieService service;
    private final ReleveA3DeuxiemePartiePdfService pdfService;

    public ReleveA3DeuxiemePartieController(ReleveA3DeuxiemePartieService service, ReleveA3DeuxiemePartiePdfService pdfService) {
        this.service = service;
        this.pdfService = pdfService;
    }

    @GetMapping
    public Page<ReleveA3DeuxiemePartieResume> lister(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "desc") String sort,
            @RequestParam(required = false) String numeroTable
    ) {
        Sort.Direction direction = "asc".equalsIgnoreCase(sort) ? Sort.Direction.ASC : Sort.Direction.DESC;
        return service.lister(numeroTable, PageRequest.of(page, size, Sort.by(direction, "createdAt")));
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
        String nomFichier = "releve-A3-2emePartie-" + id + ".pdf";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + nomFichier + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}
