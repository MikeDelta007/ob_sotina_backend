package com.officedubac.project.mission;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/ordre-mission")
@RequiredArgsConstructor
public class OrdreMissionResource {

    private final OrdreMissionService ordreMissionService;

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
}
