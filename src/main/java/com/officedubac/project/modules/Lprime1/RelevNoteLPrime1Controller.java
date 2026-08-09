package com.officedubac.project.modules.Lprime1;

import com.officedubac.project.modules.Lprime1.dto.RelevNoteLPrime1Resume;
import com.officedubac.project.modules.Lprime1.dto.RelevNoteLPrime1SaisieRequest;
import com.officedubac.project.modules.Lprime1.model.RelevNoteLPrime1;
import com.officedubac.project.modules.Lprime1.pdf.RelevNoteLPrime1PdfService;
import com.officedubac.project.modules.Lprime1.service.RelevNoteLPrime1Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/releves-lprime1")
public class RelevNoteLPrime1Controller {

    private final RelevNoteLPrime1Service service;
    private final RelevNoteLPrime1PdfService pdfService;

    public RelevNoteLPrime1Controller(RelevNoteLPrime1Service service, RelevNoteLPrime1PdfService pdfService) {
        this.service = service;
        this.pdfService = pdfService;
    }

    @PostMapping
    public RelevNoteLPrime1 creer(@RequestBody RelevNoteLPrime1SaisieRequest request) {
        return service.creer(request);
    }

    @PutMapping("/{id}")
    public RelevNoteLPrime1 mettreAJour(@PathVariable String id, @RequestBody RelevNoteLPrime1SaisieRequest request) {
        return service.mettreAJour(id, request);
    }

    @GetMapping("/{id}")
    public RelevNoteLPrime1 obtenir(@PathVariable String id) {
        return service.obtenir(id);
    }

    @GetMapping
    public Page<RelevNoteLPrime1Resume> lister(
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
        RelevNoteLPrime1 releve = service.obtenir(id);
        byte[] pdf = pdfService.genererPdf(releve);
        String nomFichier = "releve-L'1-" + id + ".pdf";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + nomFichier + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}
