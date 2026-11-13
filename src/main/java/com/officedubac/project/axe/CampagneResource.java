package com.officedubac.project.axe;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/campagnes")
@RequiredArgsConstructor
public class CampagneResource {

    private static final Logger log = LoggerFactory.getLogger(CampagneResource.class);

    private final CampagneService campagneService;

    private static final String AUTORISATION =
            "hasAnyAuthority('ADMIN','DIRECTEUR','ASSISTANTE_DIRECTEUR','CSA') or @axeAuthService.estChefHygieneSecurite()";

    @PreAuthorize(AUTORISATION)
    @GetMapping
    public ResponseEntity<List<Campagne>> getAll() {
        return ResponseEntity.ok(campagneService.getAll());
    }

    // Proposées lors de l'ajout d'une mission : campagnes actives pas encore terminées
    @PreAuthorize(AUTORISATION)
    @GetMapping("/en-cours")
    public ResponseEntity<List<Campagne>> getEnCours() {
        return ResponseEntity.ok(campagneService.getEnCours());
    }

    // Écran de gestion (inclut les campagnes désactivées)
    @PreAuthorize(AUTORISATION)
    @GetMapping("/all")
    public ResponseEntity<List<Campagne>> getAllAdmin() {
        return ResponseEntity.ok(campagneService.getAllAdmin());
    }

    @PreAuthorize(AUTORISATION)
    @GetMapping("/{id}")
    public ResponseEntity<Campagne> getById(@PathVariable String id) {
        return ResponseEntity.ok(campagneService.getById(id));
    }

    @PreAuthorize(AUTORISATION)
    @PostMapping
    public ResponseEntity<Campagne> creer(@Valid @RequestBody CampagneRequest req) {
        return ResponseEntity.ok(campagneService.creer(req));
    }

    @PreAuthorize(AUTORISATION)
    @PutMapping("/{id}")
    public ResponseEntity<Campagne> modifier(@PathVariable String id, @Valid @RequestBody CampagneRequest req) {
        return ResponseEntity.ok(campagneService.modifier(id, req));
    }

    @PreAuthorize(AUTORISATION)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimer(@PathVariable String id) {
        campagneService.supprimer(id);
        return ResponseEntity.noContent().build();
    }

    // ── Missions d'une campagne ──
    @PreAuthorize(AUTORISATION)
    @PostMapping("/{id}/missions")
    public ResponseEntity<Campagne> ajouterMissions(@PathVariable String id, @RequestBody List<MissionRequest> reqs) {
        try {
            return ResponseEntity.ok(campagneService.ajouterMissions(id, reqs));
        } catch (RuntimeException ex) {
            log.error("Erreur lors de l'ajout de missions à la campagne {} :", id, ex);
            throw ex;
        }
    }

    @PreAuthorize(AUTORISATION)
    @PutMapping("/{id}/missions/{missionId}")
    public ResponseEntity<Campagne> modifierMission(@PathVariable String id, @PathVariable String missionId,
                                                     @Valid @RequestBody MissionRequest req) {
        return ResponseEntity.ok(campagneService.modifierMission(id, missionId, req));
    }

    @PreAuthorize(AUTORISATION)
    @DeleteMapping("/{id}/missions/{missionId}")
    public ResponseEntity<Campagne> supprimerMission(@PathVariable String id, @PathVariable String missionId) {
        return ResponseEntity.ok(campagneService.supprimerMission(id, missionId));
    }
}
