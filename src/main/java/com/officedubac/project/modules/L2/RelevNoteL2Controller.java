package com.officedubac.project.modules.L2;

import com.officedubac.project.modules.L2.dto.RelevNoteL2Resume;
import com.officedubac.project.modules.L2.dto.RelevNoteL2SaisieRequest;
import com.officedubac.project.modules.L2.model.RelevNoteL2;
import com.officedubac.project.modules.L2.pdf.RelevNoteL2PdfService;
import com.officedubac.project.modules.L2.service.RelevNoteL2Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/releves-l2")
public class RelevNoteL2Controller {

    private final RelevNoteL2Service service;
    private final RelevNoteL2PdfService pdfService;

    public RelevNoteL2Controller(RelevNoteL2Service service, RelevNoteL2PdfService pdfService) {
        this.service = service;
        this.pdfService = pdfService;
    }

    @PostMapping
    public RelevNoteL2 creer(@RequestBody RelevNoteL2SaisieRequest request) {
        return service.creer(request);
    }

    @PutMapping("/{id}")
    public RelevNoteL2 mettreAJour(@PathVariable String id, @RequestBody RelevNoteL2SaisieRequest request) {
        return service.mettreAJour(id, request);
    }

    @GetMapping("/{id}")
    public RelevNoteL2 obtenir(@PathVariable String id) {
        return service.obtenir(id);
    }

    @GetMapping
    public Page<RelevNoteL2Resume> lister(
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
        RelevNoteL2 releve = service.obtenir(id);
        byte[] pdf = pdfService.genererPdf(releve);
        String nomFichier = "releve-L2-" + id + ".pdf";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + nomFichier + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}
