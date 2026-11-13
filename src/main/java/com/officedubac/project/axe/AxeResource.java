package com.officedubac.project.axe;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/axes")
@RequiredArgsConstructor
public class AxeResource {

    private final AxeService axeService;
    private final AxeAuthorization axeAuthorization;

    @PreAuthorize("hasAnyAuthority('ADMIN','DIRECTEUR','ASSISTANTE_DIRECTEUR','CSA') or @axeAuthService.estChefHygieneSecurite()")
    @GetMapping
    public ResponseEntity<List<Axe>> getAll() {
        return ResponseEntity.ok(axeService.getAll());
    }

    // Écran de gestion (inclut les axes désactivés)
    @PreAuthorize("hasAnyAuthority('ADMIN','DIRECTEUR','ASSISTANTE_DIRECTEUR','CSA') or @axeAuthService.estChefHygieneSecurite()")
    @GetMapping("/all")
    public ResponseEntity<List<Axe>> getAllAdmin() {
        return ResponseEntity.ok(axeService.getAllAdmin());
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','DIRECTEUR','ASSISTANTE_DIRECTEUR','CSA') or @axeAuthService.estChefHygieneSecurite()")
    @GetMapping("/{id}")
    public ResponseEntity<Axe> getById(@PathVariable String id) {
        return ResponseEntity.ok(axeService.getById(id));
    }

    // Pour que le frontend sache s'il doit afficher le menu/la page à ce chef précis —
    // n'importe quel compte peut vérifier son propre statut, sans fuite d'information.
    @GetMapping("/suis-chef-hygiene-securite")
    public ResponseEntity<Boolean> suisChefHygieneSecurite() {
        return ResponseEntity.ok(axeAuthorization.estChefHygieneSecurite());
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','DIRECTEUR','ASSISTANTE_DIRECTEUR','CSA') or @axeAuthService.estChefHygieneSecurite()")
    @PostMapping
    public ResponseEntity<Axe> creer(@Valid @RequestBody AxeRequest req) {
        return ResponseEntity.ok(axeService.creer(req));
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','DIRECTEUR','ASSISTANTE_DIRECTEUR','CSA') or @axeAuthService.estChefHygieneSecurite()")
    @PutMapping("/{id}")
    public ResponseEntity<Axe> modifier(@PathVariable String id, @Valid @RequestBody AxeRequest req) {
        return ResponseEntity.ok(axeService.modifier(id, req));
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','DIRECTEUR','ASSISTANTE_DIRECTEUR','CSA') or @axeAuthService.estChefHygieneSecurite()")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimer(@PathVariable String id) {
        axeService.supprimer(id);
        return ResponseEntity.noContent().build();
    }

    // Import Excel : voir AxeService.importer() pour le format attendu
    @PreAuthorize("hasAnyAuthority('ADMIN','DIRECTEUR','ASSISTANTE_DIRECTEUR','CSA') or @axeAuthService.estChefHygieneSecurite()")
    @PostMapping(value = "/import", consumes = "multipart/form-data")
    public ResponseEntity<AxeImportResult> importer(@RequestParam("fichier") MultipartFile fichier) {
        return ResponseEntity.ok(axeService.importer(fichier));
    }
}
