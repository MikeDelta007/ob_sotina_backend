package com.officedubac.project.controllers;

import com.officedubac.project.models.EtatDeVersement;
import com.officedubac.project.models.RegleMatiere;
import com.officedubac.project.models.RegleMatiereCGS;
import com.officedubac.project.models.SpecialiteCGS;
import com.officedubac.project.services.RegleMatiereCGSService;
import com.officedubac.project.services.RegleMatiereService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/v1/regleMatiereCGS")
@RequiredArgsConstructor
@Tag(name="Auth Controller", description = "Endpoints responsables de l'authentification")
public class RegleMatiereCGSController
{
    @Autowired
    private final RegleMatiereCGSService service;

    @GetMapping("/all-regles")
    public List<RegleMatiereCGS> getAll() {
        return service.findAll();
    }

    @GetMapping("/regles/{id}")
    public ResponseEntity<RegleMatiereCGS> getById(@PathVariable String id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/create-regle")
    public RegleMatiereCGS create(@RequestBody RegleMatiereCGS regle)
    {
        return service.create(regle);
    }

    @PutMapping("/{id}")
    public RegleMatiereCGS update(@PathVariable String id, @RequestBody RegleMatiereCGS regle) {
        return service.update(id, regle);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/matiereCGS-by-level")
    public ResponseEntity<List<SpecialiteCGS>> matByLevel(@RequestParam String level)
    {
        List<SpecialiteCGS> result = service.findByClasse(level);
        return ResponseEntity.ok(result);
    }
}