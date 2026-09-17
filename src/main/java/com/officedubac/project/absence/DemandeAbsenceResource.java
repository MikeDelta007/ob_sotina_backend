package com.officedubac.project.absence;

import com.officedubac.project.expressionBesoin.RejeterRequest;
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
@RequestMapping("/api/v1/demande-absence")
@RequiredArgsConstructor
public class DemandeAbsenceResource {

    private final DemandeAbsenceService demandeAbsenceService;
    private final DemandeAbsenceRepository demandeAbsenceRepository;
    private final AutorisationAbsencePdfService autorisationAbsencePdfService;
    private final MotifAbsenceRepository motifAbsenceRepo;

    @PostMapping
    public ResponseEntity<DemandeAbsence> creer(@Valid @RequestBody DemandeAbsenceRequest req) {
        return ResponseEntity.ok(demandeAbsenceService.creer(req));
    }

    // ── Motifs d'absence (liste prédéfinie pour une AUTORISATION) ──
    @GetMapping("/motifs")
    public ResponseEntity<List<MotifAbsence>> getMotifs() {
        return ResponseEntity.ok(motifAbsenceRepo.findByActifTrue());
    }

    // Tous les motifs (actifs et inactifs) — pour l'écran de gestion
    @GetMapping("/motifs/all")
    public ResponseEntity<List<MotifAbsence>> getAllMotifs() {
        return ResponseEntity.ok(motifAbsenceRepo.findAll());
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','CSA','DIRECTEUR')")
    @PostMapping("/motifs")
    public ResponseEntity<MotifAbsence> creerMotif(@RequestBody MotifAbsence motif) {
        motif.setActif(true);
        return ResponseEntity.ok(motifAbsenceRepo.save(motif));
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','CSA','DIRECTEUR')")
    @PutMapping("/motifs/{id}")
    public ResponseEntity<MotifAbsence> modifierMotif(@PathVariable String id, @RequestBody MotifAbsence req) {
        MotifAbsence motif = motifAbsenceRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Motif introuvable"));
        motif.setLibelle(req.getLibelle());
        motif.setActif(req.isActif());
        return ResponseEntity.ok(motifAbsenceRepo.save(motif));
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','CSA','DIRECTEUR')")
    @DeleteMapping("/motifs/{id}")
    public ResponseEntity<Void> supprimerMotif(@PathVariable String id) {
        motifAbsenceRepo.findById(id).ifPresent(m -> { m.setActif(false); motifAbsenceRepo.save(m); });
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/mine")
    public ResponseEntity<List<DemandeAbsence>> mesDemandes(@RequestParam TypeAbsence type) {
        return ResponseEntity.ok(demandeAbsenceService.mesDemandes(type));
    }

    @GetMapping("/a-valider")
    public ResponseEntity<List<DemandeAbsence>> aValider(@RequestParam TypeAbsence type) {
        return ResponseEntity.ok(demandeAbsenceService.aValider(type));
    }

    // Historique complet (tous statuts) des demandes des agents du chef connecté
    @GetMapping("/mes-agents")
    public ResponseEntity<List<DemandeAbsence>> demandesDeMesAgents(@RequestParam TypeAbsence type) {
        return ResponseEntity.ok(demandeAbsenceService.demandesDeMesAgents(type));
    }

    // Demandes déjà traitées (VALIDEE ou REJETEE) — onglet "Déjà validées" pour toute la chaîne
    @GetMapping("/traitees")
    public ResponseEntity<List<DemandeAbsence>> demandesTraitees(@RequestParam TypeAbsence type) {
        return ResponseEntity.ok(demandeAbsenceService.demandesTraitees(type));
    }

    @PutMapping("/{id}/valider")
    public ResponseEntity<DemandeAbsence> valider(@PathVariable String id) {
        return ResponseEntity.ok(demandeAbsenceService.valider(id));
    }

    @PutMapping("/{id}/rejeter")
    public ResponseEntity<DemandeAbsence> rejeter(@PathVariable String id, @Valid @RequestBody RejeterRequest req) {
        return ResponseEntity.ok(demandeAbsenceService.rejeter(id, req.getMotif()));
    }

    // Télécharger le PDF d'une autorisation d'absence validée
    @PreAuthorize("hasAnyAuthority('CSA','DIRECTEUR','ADMIN')")
    @GetMapping("/{id}/autorisation.pdf")
    public void telechargerAutorisation(@PathVariable String id, HttpServletResponse response) throws IOException {
        DemandeAbsence demande = demandeAbsenceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Demande d'absence introuvable"));
        if (demande.getStatut() != StatutAbsence.VALIDEE) {
            throw new RuntimeException("Cette demande n'est pas encore validée");
        }

        byte[] pdf = autorisationAbsencePdfService.genererAutorisation(demande);

        String filename = "autorisation_absence_" + id + ".pdf";
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition",
                "attachment; filename=\"" + filename + "\"; filename*=UTF-8''"
                + URLEncoder.encode(filename, StandardCharsets.UTF_8).replace("+", "%20"));
        response.setContentLength(pdf.length);
        response.getOutputStream().write(pdf);
    }
}
