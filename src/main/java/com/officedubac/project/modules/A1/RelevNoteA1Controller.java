package com.officedubac.project.modules.A1;

import com.officedubac.project.modules.A1.dto.RelevNoteA1Resume;
import com.officedubac.project.modules.A1.pdf.RelevNoteA1PdfService;
import com.officedubac.project.modules.A1.service.RelevNoteA1Service;
import com.officedubac.project.modules.A1.dto.RelevNoteA1SaisieRequest;
import com.officedubac.project.modules.A1.model.RelevNoteA1;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/releves-a1")
public class RelevNoteA1Controller {

    private final RelevNoteA1Service service;
    private final RelevNoteA1PdfService pdfService;

    public RelevNoteA1Controller(RelevNoteA1Service service, RelevNoteA1PdfService pdfService)
    {
        this.service = service;
        this.pdfService = pdfService;
    }

    @PostMapping
    public RelevNoteA1 creer(@RequestBody RelevNoteA1SaisieRequest request) {
        return service.creer(request);
    }

    @PutMapping("/{id}")
    public RelevNoteA1 mettreAJour(@PathVariable String id, @RequestBody RelevNoteA1SaisieRequest request) {
        return service.mettreAJour(id, request);
    }

    @GetMapping("/{id}")
    public RelevNoteA1 obtenir(@PathVariable String id) {
        return service.obtenir(id);
    }

    @GetMapping
    public Page<RelevNoteA1Resume> lister(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "desc") String sort
    ) {
        Sort.Direction direction = "asc".equalsIgnoreCase(sort) ? Sort.Direction.ASC : Sort.Direction.DESC;
        return service.lister(PageRequest.of(page, size, Sort.by(direction, "createdAt")));
    }

    @GetMapping(value = "/{id}/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> genererPdf(@PathVariable String id) {
        RelevNoteA1 releve = service.obtenir(id);
        byte[] pdf = pdfService.genererPdf(releve);
        String nomFichier = "releve-A1-" + id + ".pdf";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + nomFichier + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}
