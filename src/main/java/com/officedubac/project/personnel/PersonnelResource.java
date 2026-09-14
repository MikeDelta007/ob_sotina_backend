package com.officedubac.project.personnel;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/personnel")
@RequiredArgsConstructor
public class PersonnelResource {

    private final DivisionRepository divisionRepo;
    private final FonctionRepository fonctionRepo;

    // ── Divisions ──
    @GetMapping("/divisions")
    public ResponseEntity<List<Division>> getDivisions() {
        return ResponseEntity.ok(divisionRepo.findByActifTrue());
    }

    // Toutes les divisions (actives et inactives) — pour l'écran de gestion
    @GetMapping("/divisions/all")
    public ResponseEntity<List<Division>> getAllDivisions() {
        return ResponseEntity.ok(divisionRepo.findAll());
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','CSA','DIRECTEUR')")
    @PostMapping("/divisions")
    public ResponseEntity<Division> creerDivision(@RequestBody Division division) {
        division.setActif(true);
        return ResponseEntity.ok(divisionRepo.save(division));
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','CSA','DIRECTEUR')")
    @PutMapping("/divisions/{id}")
    public ResponseEntity<Division> modifierDivision(@PathVariable String id, @RequestBody Division req) {
        Division division = divisionRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Division introuvable"));
        division.setLibelle(req.getLibelle());
        division.setChefServiceId(req.getChefServiceId());
        division.setActif(req.isActif());
        return ResponseEntity.ok(divisionRepo.save(division));
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','CSA','DIRECTEUR')")
    @DeleteMapping("/divisions/{id}")
    public ResponseEntity<Void> supprimerDivision(@PathVariable String id) {
        divisionRepo.findById(id).ifPresent(d -> { d.setActif(false); divisionRepo.save(d); });
        return ResponseEntity.noContent().build();
    }

    // ── Fonctions ──
    @GetMapping("/fonctions")
    public ResponseEntity<List<Fonction>> getFonctions() {
        return ResponseEntity.ok(fonctionRepo.findByActifTrue());
    }

    @GetMapping("/fonctions/all")
    public ResponseEntity<List<Fonction>> getAllFonctions() {
        return ResponseEntity.ok(fonctionRepo.findAll());
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','CSA','DIRECTEUR')")
    @PostMapping("/fonctions")
    public ResponseEntity<Fonction> creerFonction(@RequestBody Fonction fonction) {
        fonction.setActif(true);
        return ResponseEntity.ok(fonctionRepo.save(fonction));
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','CSA','DIRECTEUR')")
    @PutMapping("/fonctions/{id}")
    public ResponseEntity<Fonction> modifierFonction(@PathVariable String id, @RequestBody Fonction req) {
        Fonction fonction = fonctionRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Fonction introuvable"));
        fonction.setLibelle(req.getLibelle());
        fonction.setActif(req.isActif());
        return ResponseEntity.ok(fonctionRepo.save(fonction));
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','CSA','DIRECTEUR')")
    @DeleteMapping("/fonctions/{id}")
    public ResponseEntity<Void> supprimerFonction(@PathVariable String id) {
        fonctionRepo.findById(id).ifPresent(f -> { f.setActif(false); fonctionRepo.save(f); });
        return ResponseEntity.noContent().build();
    }
}
