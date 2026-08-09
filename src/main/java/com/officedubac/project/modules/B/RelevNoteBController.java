package com.officedubac.project.modules.B;

import com.officedubac.project.modules.B.dto.RelevNoteBResume;
import com.officedubac.project.modules.B.dto.RelevNoteBSaisieRequest;
import com.officedubac.project.modules.B.model.RelevNoteB;
import com.officedubac.project.modules.B.pdf.RelevNoteBPdfService;
import com.officedubac.project.modules.B.service.RelevNoteBService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/releves-b")
public class RelevNoteBController {

    private final RelevNoteBService service;
    private final RelevNoteBPdfService pdfService;

    public RelevNoteBController(RelevNoteBService service, RelevNoteBPdfService pdfService) {
        this.service = service;
        this.pdfService = pdfService;
    }

    @PostMapping
    public RelevNoteB creer(@RequestBody RelevNoteBSaisieRequest request) {
        return service.creer(request);
    }

    @PutMapping("/{id}")
    public RelevNoteB mettreAJour(@PathVariable String id, @RequestBody RelevNoteBSaisieRequest request) {
        return service.mettreAJour(id, request);
    }

    @GetMapping("/{id}")
    public RelevNoteB obtenir(@PathVariable String id) {
        return service.obtenir(id);
    }

    @GetMapping
    public Page<RelevNoteBResume> lister(
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
        RelevNoteB releve = service.obtenir(id);
        byte[] pdf = pdfService.genererPdf(releve);
        String nomFichier = "releve-B-" + id + ".pdf";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + nomFichier + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}
