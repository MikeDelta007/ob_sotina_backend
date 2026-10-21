package com.officedubac.project.mission;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/ordre-mission")
@RequiredArgsConstructor
public class OrdreMissionResource {

    private final OrdreMissionService ordreMissionService;
    private final OrdreMissionPdfService ordreMissionPdfService;

    @PreAuthorize("hasAuthority('CSA')")
    @PostMapping
    public ResponseEntity<OrdreMission> creer(@Valid @RequestBody OrdreMissionRequest req) {
        return ResponseEntity.ok(ordreMissionService.creer(req));
    }

    @PreAuthorize("hasAuthority('CSA')")
    @PutMapping("/{id}")
    public ResponseEntity<OrdreMission> modifier(@PathVariable String id, @Valid @RequestBody OrdreMissionRequest req) {
        return ResponseEntity.ok(ordreMissionService.modifier(id, req));
    }

    @PreAuthorize("hasAuthority('CSA')")
    @PutMapping("/{id}/annuler")
    public ResponseEntity<OrdreMission> annuler(@PathVariable String id) {
        return ResponseEntity.ok(ordreMissionService.annuler(id));
    }

    @GetMapping("/mine")
    public ResponseEntity<List<OrdreMission>> mesMissions() {
        return ResponseEntity.ok(ordreMissionService.mesMissions());
    }

    @PreAuthorize("hasAnyAuthority('CSA','DIRECTEUR','ADMIN')")
    @GetMapping
    public ResponseEntity<List<OrdreMission>> toutes() {
        return ResponseEntity.ok(ordreMissionService.toutes());
    }

    // ── Export PDF ──

    @PreAuthorize("hasAnyAuthority('CSA','DIRECTEUR','ADMIN')")
    @GetMapping("/{id}/pdf")
    public void telechargerPdf(@PathVariable String id, HttpServletResponse response) throws IOException {
        OrdreMission ordre = ordreMissionService.getById(id);
        byte[] pdf = ordreMissionPdfService.genererPdf(List.of(ordre));
        envoyerPdf(response, pdf, "ordre_mission_" + id + ".pdf");
    }

    @PreAuthorize("hasAnyAuthority('CSA','DIRECTEUR','ADMIN')")
    @PostMapping("/pdf-groupe")
    public void telechargerPdfGroupe(@RequestBody Map<String, List<String>> body, HttpServletResponse response) throws IOException {
        List<String> ids = body.getOrDefault("ids", List.of());
        if (ids.isEmpty()) {
            throw new RuntimeException("Sélectionnez au moins un ordre de mission à exporter");
        }
        List<OrdreMission> ordres = ids.stream().map(ordreMissionService::getById).toList();

        OrdreMission reference = ordres.get(0);
        java.util.Set<String> villesReference = new java.util.HashSet<>(reference.getVilleIds());
        boolean memeGroupe = ordres.stream().allMatch(o ->
                o.getDateDebut().equals(reference.getDateDebut())
                        && o.getDateFin().equals(reference.getDateFin())
                        && java.util.Objects.equals(o.getMotifId(), reference.getMotifId())
                        && new java.util.HashSet<>(o.getVilleIds()).equals(villesReference));
        if (!memeGroupe) {
            throw new RuntimeException("Les ordres sélectionnés doivent avoir les mêmes dates, le même motif et les mêmes villes pour un export groupé");
        }

        byte[] pdf = ordreMissionPdfService.genererPdf(ordres);
        envoyerPdf(response, pdf, "ordres_mission_groupe.pdf");
    }

    // Export de tous les ordres actifs dont la date de début tombe dans l'intervalle donné — pas
    // de contrainte de motif/villes identiques ici, contrairement à /pdf-groupe (sélection manuelle).
    @PreAuthorize("hasAnyAuthority('CSA','DIRECTEUR','ADMIN')")
    @PostMapping("/pdf-intervalle")
    public void telechargerPdfIntervalle(@RequestBody Map<String, String> body, HttpServletResponse response) throws IOException {
        String debutStr = body.get("dateDebut");
        String finStr = body.get("dateFin");
        if (debutStr == null || finStr == null) {
            throw new RuntimeException("Indiquez une date de début et une date de fin");
        }
        java.time.LocalDate debut = java.time.LocalDate.parse(debutStr);
        java.time.LocalDate fin = java.time.LocalDate.parse(finStr);
        List<OrdreMission> ordres = ordreMissionService.parIntervalleDeDates(debut, fin);
        if (ordres.isEmpty()) {
            throw new RuntimeException("Aucun ordre de mission dans cet intervalle de dates");
        }

        byte[] pdf = ordreMissionPdfService.genererPdf(ordres);
        envoyerPdf(response, pdf, "ordres_mission_du_" + debutStr + "_au_" + finStr + ".pdf");
    }

    private void envoyerPdf(HttpServletResponse response, byte[] pdf, String filename) throws IOException {
        response.setContentType(MediaType.APPLICATION_PDF_VALUE);
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + filename + "\"; filename*=UTF-8''"
                        + URLEncoder.encode(filename, StandardCharsets.UTF_8).replace("+", "%20"));
        response.setContentLength(pdf.length);
        response.getOutputStream().write(pdf);
        response.getOutputStream().flush();
    }

    // ── Motifs (liste déroulante à la création) — liste propre aux ordres de mission ──

    @GetMapping("/motifs")
    public ResponseEntity<List<MotifMission>> getMotifs() {
        return ResponseEntity.ok(ordreMissionService.getMotifs());
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','CSA','DIRECTEUR')")
    @GetMapping("/motifs/all")
    public ResponseEntity<List<MotifMission>> getAllMotifs() {
        return ResponseEntity.ok(ordreMissionService.getAllMotifs());
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','CSA','DIRECTEUR')")
    @PostMapping("/motifs")
    public ResponseEntity<MotifMission> creerMotif(@RequestBody MotifMission motif) {
        return ResponseEntity.ok(ordreMissionService.creerMotif(motif.getLibelle()));
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','CSA','DIRECTEUR')")
    @PutMapping("/motifs/{id}")
    public ResponseEntity<MotifMission> modifierMotif(@PathVariable String id, @RequestBody MotifMission motif) {
        return ResponseEntity.ok(ordreMissionService.modifierMotif(id, motif.getLibelle(), motif.isActif()));
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','CSA','DIRECTEUR')")
    @DeleteMapping("/motifs/{id}")
    public ResponseEntity<Void> supprimerMotif(@PathVariable String id) {
        ordreMissionService.supprimerMotif(id);
        return ResponseEntity.noContent().build();
    }
}
