package com.officedubac.project.modules.F7;

import com.officedubac.project.modules.F7.dto.RelevNoteF7Resume;
import com.officedubac.project.modules.F7.dto.RelevNoteF7SaisieRequest;
import com.officedubac.project.modules.F7.model.RelevNoteF7;
import com.officedubac.project.modules.F7.pdf.RelevNoteF7PdfService;
import com.officedubac.project.modules.F7.service.RelevNoteF7Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/releves-f7")
public class RelevNoteF7Controller {

    private final RelevNoteF7Service service;
    private final RelevNoteF7PdfService pdfService;

    public RelevNoteF7Controller(RelevNoteF7Service service, RelevNoteF7PdfService pdfService) {
        this.service = service;
        this.pdfService = pdfService;
    }

    @PostMapping
    public RelevNoteF7 creer(@RequestBody RelevNoteF7SaisieRequest request) {
        return service.creer(request);
    }

    @PutMapping("/{id}")
    public RelevNoteF7 mettreAJour(@PathVariable String id, @RequestBody RelevNoteF7SaisieRequest request) {
        return service.mettreAJour(id, request);
    }

    @GetMapping("/{id}")
    public RelevNoteF7 obtenir(@PathVariable String id) {
        return service.obtenir(id);
    }

    @GetMapping
    public Page<RelevNoteF7Resume> lister(
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
        RelevNoteF7 releve = service.obtenir(id);
        byte[] pdf = pdfService.genererPdf(releve);
        String nomFichier = "releve-F7-" + id + ".pdf";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + nomFichier + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}
