package com.officedubac.project.controllers;

import com.officedubac.project.dto.*;
import com.officedubac.project.models.*;
import com.officedubac.project.services.AuthenticationService;
import com.officedubac.project.services.ParametrageService;
import com.officedubac.project.services.StatsService;
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
@RequestMapping("/api/v1/stats")
@RequiredArgsConstructor
@Tag(name="Statistiques Controller", description = "Endpoints responsables de la gestion des statistiques de la plateforme")
public class StatistiquesController
{
    @Autowired
    private final StatsService statsService;

    @Operation(summary="Service de création d'un compte")
    @GetMapping("/stats-globales/{session}")
    public ResponseEntity<GlobalStatDTO> statsGlobales(@PathVariable int session)
    {
        return ResponseEntity.ok(statsService.getGlobalStat(session));
    }

    @GetMapping("/stats-globales-vignettes/{session}")
    public ResponseEntity<GlobalStatVCDTO> statsGlobalesVC(@PathVariable int session)
    {
        return ResponseEntity.ok(statsService.getGlobalStatVC(session));
    }
    @GetMapping("/stats-nationales/{session}")
    public ResponseEntity<List<MapDTO>> statsNationales(@PathVariable int session)
    {
        return ResponseEntity.ok(statsService.getStatsParDepartement(session));
    }

    @GetMapping("/stats-by-academie/{session}")
    public ResponseEntity<List<StatAcademieDTO>> statsByAcademie(@PathVariable int session)
    {
        return ResponseEntity.ok(statsService.getStatsParAcademie(session));
    }

    @GetMapping("/stats-by-handicap/{session}")
    public ResponseEntity<List<StatHandicapDTO>> statsByHandicap(@PathVariable int session)
    {
        return ResponseEntity.ok(statsService.getStatsParHandicap(session));
    }

    @GetMapping("/stats-by-serie/{session}")
    public ResponseEntity<List<StatSerieDTO>> statsBySerie(@PathVariable int session)
    {
        return ResponseEntity.ok(statsService.getStatsParSerie(session));
    }


}
