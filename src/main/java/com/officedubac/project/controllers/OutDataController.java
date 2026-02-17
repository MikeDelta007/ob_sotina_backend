package com.officedubac.project.controllers;

import com.officedubac.project.dto.JwtAuthenticationResponse;
import com.officedubac.project.dto.SignInDTO;
import com.officedubac.project.models.Candidat;
import com.officedubac.project.services.AuthenticationService;
import com.officedubac.project.services.CandidatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name="Data Controller", description = "Endpoints responsables du partage des données")
public class OutDataController
{
    @Autowired
    private final CandidatService candidatService;
    @Operation(summary="Service de partage des données des candidats validés d'une édition du BAC")
    // Dans CandidatController.java
    @GetMapping("/portailbac-to-campusen/{session}")
    public ResponseEntity<List<Candidat>> getCandidatsValides(@PathVariable Long session) {
        List<Candidat> candidats = candidatService.getCandidatsValidesParSession(session);
        return ResponseEntity.ok(candidats);
    }

}
