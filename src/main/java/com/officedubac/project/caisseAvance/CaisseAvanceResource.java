package com.officedubac.project.caisseAvance;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/api/v1/caisse-avance")
@RequiredArgsConstructor
public class CaisseAvanceResource {

    private final CaisseAvanceService           caisseService;
    private final MotifRepository                motifRepo;
    private final ApprovisionnementExcelService  approvisionnementExcelService;

    // ── Caisse ──
    @GetMapping("/current")
    public ResponseEntity<CaisseAvance> getCaisse() {
        return ResponseEntity.ok(caisseService.getCaisseCourante());
    }

    // ── Approvisionnement : ajoute un montant à la caisse (historisé) ──
    // Réservé au Chef comptable ; le Directeur peut suppléer en cas d'absence.
    // Ni l'Agent comptable ni l'Admin n'y ont accès.
    @PreAuthorize("hasAnyAuthority('CHEF_COMPTABLE','DIRECTEUR')")
    @PostMapping("/approvisionner")
    public ResponseEntity<Approvisionnement> approvisionner(@Valid @RequestBody ApprovisionnementRequest req) {
        return ResponseEntity.ok(
            caisseService.approvisionner(req.getMontant(), req.getDate(), req.getDescription()));
    }

    @GetMapping("/approvisionnements")
    public ResponseEntity<List<Approvisionnement>> getApprovisionnements(
            @RequestParam(required = false) Integer annee,
            @RequestParam(required = false) Integer mois,
            @RequestParam(required = false) Integer semaine) {
        return ResponseEntity.ok(caisseService.getAllApprovisionnements(annee, mois, semaine));
    }

    @GetMapping("/approvisionnements/export.xlsx")
    public void exportApprovisionnements(
            @RequestParam(required = false) Integer annee,
            @RequestParam(required = false) Integer mois,
            @RequestParam(required = false) Integer semaine,
            HttpServletResponse response) throws IOException {
        byte[] excel = approvisionnementExcelService.genererListe(
                caisseService.getAllApprovisionnements(annee, mois, semaine));

        String filename = "approvisionnements_caisse_avance.xlsx";
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition",
                "attachment; filename=\"" + filename + "\"; filename*=UTF-8''"
                + URLEncoder.encode(filename, StandardCharsets.UTF_8).replace("+", "%20"));
        response.setContentLength(excel.length);
        response.getOutputStream().write(excel);
    }

    // ── Motifs ──
    @GetMapping("/motifs")
    public ResponseEntity<List<Motif>> getMotifs() {
        return ResponseEntity.ok(motifRepo.findByActifTrue());
    }

    // Tous les motifs (actifs et inactifs) — pour l'écran de gestion admin
    @GetMapping("/motifs/all")
    public ResponseEntity<List<Motif>> getAllMotifs() {
        return ResponseEntity.ok(motifRepo.findAll());
    }

    // Motifs : CRUD ouvert aux comptables ainsi qu'au CSA et au Directeur
    @PreAuthorize("hasAnyAuthority('CHEF_COMPTABLE','AGENT_COMPTABLE','ADMIN','CSA','DIRECTEUR')")
    @PostMapping("/motifs")
    public ResponseEntity<Motif> creerMotif(@RequestBody Motif motif) {
        motif.setActif(true);
        return ResponseEntity.ok(motifRepo.save(motif));
    }

    // Motifs : CRUD ouvert aux comptables ainsi qu'au CSA et au Directeur
    @PreAuthorize("hasAnyAuthority('CHEF_COMPTABLE','AGENT_COMPTABLE','ADMIN','CSA','DIRECTEUR')")
    @PutMapping("/motifs/{id}")
    public ResponseEntity<Motif> modifierMotif(@PathVariable String id, @RequestBody Motif req) {
        Motif motif = motifRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Motif introuvable"));
        motif.setLibelle(req.getLibelle());
        motif.setActif(req.isActif());
        return ResponseEntity.ok(motifRepo.save(motif));
    }

    // Motifs : CRUD ouvert aux comptables ainsi qu'au CSA et au Directeur
    @PreAuthorize("hasAnyAuthority('CHEF_COMPTABLE','AGENT_COMPTABLE','ADMIN','CSA','DIRECTEUR')")
    @DeleteMapping("/motifs/{id}")
    public ResponseEntity<Void> supprimerMotif(@PathVariable String id) {
        motifRepo.findById(id).ifPresent(m -> { m.setActif(false); motifRepo.save(m); });
        return ResponseEntity.noContent().build();
    }
}
