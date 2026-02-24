package com.officedubac.project.controllers;

import com.officedubac.project.dto.JwtAuthenticationResponse;
import com.officedubac.project.dto.RefreshTokenDTO;
import com.officedubac.project.dto.SignInDTO;
import com.officedubac.project.models.RegleMatiere;
import com.officedubac.project.models.User;
import com.officedubac.project.services.AuthenticationService;
import com.officedubac.project.services.RegleMatiereService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/v1/regleMatiere")
@RequiredArgsConstructor
@Tag(name="Auth Controller", description = "Endpoints responsables de l'authentification")
public class RegleMatiereController
{
    @Autowired
    private final RegleMatiereService service;

    @GetMapping("/all-regles")
    public List<RegleMatiere> getAll() {
        return service.findAll();
    }

    @GetMapping("/regles/{id}")
    public ResponseEntity<RegleMatiere> getById(@PathVariable String id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/create-regle")
    public RegleMatiere create(@RequestBody RegleMatiere regle) {
        return service.create(regle);
    }

    @PutMapping("/{id}")
    public RegleMatiere update(@PathVariable String id, @RequestBody RegleMatiere regle) {
        return service.update(id, regle);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}