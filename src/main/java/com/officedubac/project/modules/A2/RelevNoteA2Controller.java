package com.officedubac.project.modules.A2;


import com.officedubac.project.modules.A2.dto.RelevNoteA2Resume;
import com.officedubac.project.modules.A2.dto.RelevNoteA2SaisieRequest;
import com.officedubac.project.modules.A2.model.RelevNoteA2;
import com.officedubac.project.modules.A2.pdf.RelevNoteA2PdfService;
import com.officedubac.project.modules.A2.service.RelevNoteA2Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/releves-a2")
public class RelevNoteA2Controller {

    private final RelevNoteA2Service service;
    private final RelevNoteA2PdfService pdfService;

    public RelevNoteA2Controller(RelevNoteA2Service service, RelevNoteA2PdfService pdfService)
    {
        this.service = service;
        this.pdfService = pdfService;
    }

    @PostMapping
    public RelevNoteA2 creer(@RequestBody RelevNoteA2SaisieRequest request) {
        return service.creer(request);
    }

    @PutMapping("/{id}")
    public RelevNoteA2 mettreAJour(@PathVariable String id, @RequestBody RelevNoteA2SaisieRequest request) {
        return service.mettreAJour(id, request);
    }

    @GetMapping("/{id}")
    public RelevNoteA2 obtenir(@PathVariable String id) {
        return service.obtenir(id);
    }

    @GetMapping
    public Page<RelevNoteA2Resume> lister(
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
        RelevNoteA2 releve = service.obtenir(id);
        byte[] pdf = pdfService.genererPdf(releve);
        String nomFichier = "releve-A2-" + id + ".pdf";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + nomFichier + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}
