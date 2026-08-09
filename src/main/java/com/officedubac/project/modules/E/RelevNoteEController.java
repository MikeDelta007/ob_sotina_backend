package com.officedubac.project.modules.E;

import com.officedubac.project.modules.E.dto.RelevNoteEResume;
import com.officedubac.project.modules.E.dto.RelevNoteESaisieRequest;
import com.officedubac.project.modules.E.model.RelevNoteE;
import com.officedubac.project.modules.E.pdf.RelevNoteEPdfService;
import com.officedubac.project.modules.E.service.RelevNoteEService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/releves-e")
public class RelevNoteEController {

    private final RelevNoteEService service;
    private final RelevNoteEPdfService pdfService;

    public RelevNoteEController(RelevNoteEService service, RelevNoteEPdfService pdfService) {
        this.service = service;
        this.pdfService = pdfService;
    }

    @PostMapping
    public RelevNoteE creer(@RequestBody RelevNoteESaisieRequest request) {
        return service.creer(request);
    }

    @PutMapping("/{id}")
    public RelevNoteE mettreAJour(@PathVariable String id, @RequestBody RelevNoteESaisieRequest request) {
        return service.mettreAJour(id, request);
    }

    @GetMapping("/{id}")
    public RelevNoteE obtenir(@PathVariable String id) {
        return service.obtenir(id);
    }

    @GetMapping
    public Page<RelevNoteEResume> lister(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "desc") String sort,
            @RequestParam(required = false) String numeroTable,
            @RequestParam(required = false) Integer annee
    ) {
        Sort.Direction direction = "asc".equalsIgnoreCase(sort) ? Sort.Direction.ASC : Sort.Direction.DESC;
        return service.lister(numeroTable, annee, PageRequest.of(page, size, Sort.by(direction, "createdAt")));
    }

    @GetMapping(value = "/{id}/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> genererPdf(@PathVariable String id) {
        RelevNoteE releve = service.obtenir(id);
        byte[] pdf = pdfService.genererPdf(releve);
        String nomFichier = "releve-E-" + id + ".pdf";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + nomFichier + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}
