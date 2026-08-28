package com.officedubac.project.expressionBesoin;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/expression-besoin")
@RequiredArgsConstructor
public class ExpressionBesoinResource {

    private final ExpressionBesoinService expressionBesoinService;

    // ── Chef de service ──
    @PreAuthorize("hasAuthority('CHEF_SERVICE')")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ExpressionBesoin> creer(
            @Valid @RequestPart("data") ExpressionBesoinRequest req,
            @RequestPart(value = "pdfFactureProforma", required = false) MultipartFile pdfFactureProforma,
            @RequestPart(value = "pdfDeclarationHonneur", required = false) MultipartFile pdfDeclarationHonneur) {
        return ResponseEntity.ok(expressionBesoinService.creer(req, pdfFactureProforma, pdfDeclarationHonneur));
    }

    @PreAuthorize("hasAuthority('CHEF_SERVICE')")
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ExpressionBesoin> modifier(
            @PathVariable String id,
            @Valid @RequestPart("data") ExpressionBesoinRequest req,
            @RequestPart(value = "pdfFactureProforma", required = false) MultipartFile pdfFactureProforma,
            @RequestPart(value = "pdfDeclarationHonneur", required = false) MultipartFile pdfDeclarationHonneur) {
        return ResponseEntity.ok(expressionBesoinService.modifier(id, req, pdfFactureProforma, pdfDeclarationHonneur));
    }

    @PreAuthorize("hasAuthority('CHEF_SERVICE')")
    @GetMapping("/mine")
    public ResponseEntity<List<ExpressionBesoin>> getMesExpressions() {
        return ResponseEntity.ok(expressionBesoinService.getMesExpressions());
    }

    // ── CSA / Directeur / Admin ──
    @PreAuthorize("hasAnyAuthority('CSA','DIRECTEUR','ADMIN')")
    @GetMapping("/a-valider")
    public ResponseEntity<List<ExpressionBesoin>> getAValider() {
        return ResponseEntity.ok(expressionBesoinService.getAValider());
    }

    @PreAuthorize("hasAnyAuthority('CSA','DIRECTEUR','ADMIN')")
    @PutMapping("/{id}/valider")
    public ResponseEntity<ExpressionBesoin> valider(@PathVariable String id) {
        return ResponseEntity.ok(expressionBesoinService.valider(id));
    }

    @PreAuthorize("hasAnyAuthority('CSA','DIRECTEUR','ADMIN')")
    @PutMapping("/{id}/rejeter")
    public ResponseEntity<ExpressionBesoin> rejeter(@PathVariable String id, @Valid @RequestBody RejeterRequest req) {
        return ResponseEntity.ok(expressionBesoinService.rejeter(id, req.getMotif()));
    }

    // ── Chef comptable / Agent comptable ──
    @PreAuthorize("hasAnyAuthority('CHEF_COMPTABLE','AGENT_COMPTABLE')")
    @GetMapping("/a-traiter")
    public ResponseEntity<List<ExpressionBesoin>> getATraiter() {
        return ResponseEntity.ok(expressionBesoinService.getATraiter());
    }

    @PreAuthorize("hasAnyAuthority('CHEF_COMPTABLE','AGENT_COMPTABLE')")
    @PutMapping("/{id}/traiter")
    public ResponseEntity<ExpressionBesoin> traiter(@PathVariable String id, @Valid @RequestBody TraiterRequest req) {
        return ResponseEntity.ok(expressionBesoinService.traiter(id, req));
    }

    @PreAuthorize("hasAnyAuthority('CHEF_COMPTABLE','AGENT_COMPTABLE','ADMIN')")
    @GetMapping("/disponibles-mandatement")
    public ResponseEntity<List<ExpressionBesoin>> getDisponiblesPourMandatement() {
        return ResponseEntity.ok(expressionBesoinService.getDisponiblesPourMandatement());
    }
}
