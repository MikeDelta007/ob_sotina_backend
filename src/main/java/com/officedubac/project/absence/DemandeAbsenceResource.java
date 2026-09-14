package com.officedubac.project.absence;

import com.officedubac.project.expressionBesoin.RejeterRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/demande-absence")
@RequiredArgsConstructor
public class DemandeAbsenceResource {

    private final DemandeAbsenceService demandeAbsenceService;

    @PostMapping
    public ResponseEntity<DemandeAbsence> creer(@Valid @RequestBody DemandeAbsenceRequest req) {
        return ResponseEntity.ok(demandeAbsenceService.creer(req));
    }

    @GetMapping("/mine")
    public ResponseEntity<List<DemandeAbsence>> mesDemandes() {
        return ResponseEntity.ok(demandeAbsenceService.mesDemandes());
    }

    @GetMapping("/a-valider")
    public ResponseEntity<List<DemandeAbsence>> aValider() {
        return ResponseEntity.ok(demandeAbsenceService.aValider());
    }

    @PutMapping("/{id}/valider")
    public ResponseEntity<DemandeAbsence> valider(@PathVariable String id) {
        return ResponseEntity.ok(demandeAbsenceService.valider(id));
    }

    @PutMapping("/{id}/rejeter")
    public ResponseEntity<DemandeAbsence> rejeter(@PathVariable String id, @Valid @RequestBody RejeterRequest req) {
        return ResponseEntity.ok(demandeAbsenceService.rejeter(id, req.getMotif()));
    }
}
