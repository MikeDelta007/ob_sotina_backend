package com.officedubac.project.modules.A4;

import com.officedubac.project.modules.A4.dto.RelevNoteA4Resume;
import com.officedubac.project.modules.A4.dto.RelevNoteA4SaisieRequest;
import com.officedubac.project.modules.A4.model.RelevNoteA4;
import com.officedubac.project.modules.A4.pdf.RelevNoteA4PdfService;
import com.officedubac.project.modules.A4.service.RelevNoteA4Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/releves-a4")
public class RelevNoteA4Controller {

    private final RelevNoteA4Service service;
    private final RelevNoteA4PdfService pdfService;

    public RelevNoteA4Controller(RelevNoteA4Service service, RelevNoteA4PdfService pdfService) {
        this.service = service;
        this.pdfService = pdfService;
    }

    @PostMapping
    public RelevNoteA4 creer(@RequestBody RelevNoteA4SaisieRequest request) {
        return service.creer(request);
    }

    @PutMapping("/{id}")
    public RelevNoteA4 mettreAJour(@PathVariable String id, @RequestBody RelevNoteA4SaisieRequest request) {
        return service.mettreAJour(id, request);
    }

    @GetMapping("/{id}")
    public RelevNoteA4 obtenir(@PathVariable String id) {
        return service.obtenir(id);
    }

    @GetMapping
    public Page<RelevNoteA4Resume> lister(
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
        RelevNoteA4 releve = service.obtenir(id);
        byte[] pdf = pdfService.genererPdf(releve);
        String nomFichier = "releve-A4-" + id + ".pdf";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + nomFichier + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}
