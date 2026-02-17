package com.officedubac.project.controllers;

import com.officedubac.project.dto.*;
import com.officedubac.project.models.*;
import com.officedubac.project.services.AuditService;
import com.officedubac.project.services.CandidatService;
import com.officedubac.project.services.ParametrageService;
import com.officedubac.project.services.StatsService;
import com.officedubac.project.utils.IpUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/v1/validation-candidats")
@RequiredArgsConstructor
@Tag(name="Validation Controller", description = "Endpoints responsables de la validation des dossiers de candidature")
public class ValidationController
{

    @Autowired
    private final CandidatService candidatService;
    @Autowired
    private final AuditService auditService;
    @Autowired
    private final ParametrageService parametrageService;
    @Autowired
    private final StatsService statsService;

    @Autowired
    private IpUtils ipUtils;

    @Operation(summary="Service de filtrage des candidats selon l'établissement et la série")
    @GetMapping("/candidats/filter")
    public ResponseEntity<List<Candidat>> filterCandidats(
            @RequestParam String etablissementId,
            @RequestParam Long session
    ) {
        List<Candidat> result = candidatService.getFilteredCandidats(etablissementId, session);
        return ResponseEntity.ok(result);
    }

    @Operation(summary="Service de mis à jour des coupons numériques achetés")
    @PatchMapping(value="/update-coupons-etab")
    public ResponseEntity<?> updateCouponsNombres(@RequestParam String idEV, @RequestParam String f, @RequestParam String l, @RequestBody VignetteAddDTO vignetteAddDTO) throws Exception {

        return ResponseEntity.ok(this.candidatService.updateEV(idEV, vignetteAddDTO, f, l));
    }

    @Operation(summary="Service de mis à jour des coupons numériques achetés")
    @PatchMapping(value="/correction-coupons-etab")
    public ResponseEntity<?> correctionCouponsNombres(@RequestParam String idEV, @RequestParam String motif, @RequestParam String f, @RequestParam String l) throws Exception {

        return ResponseEntity.ok(this.candidatService.updateEV_(idEV, motif, f, l));
    }

    @Operation(summary="Service de mis à jour de la décision d'un dossier de candidature")
    @PatchMapping(value="/update-decision-cdt")
    public ResponseEntity<Candidat> updateDecision(@RequestParam String idCdt, @RequestBody CandidatDecisionDTO candidatDecisionDTO, HttpServletRequest request) throws Exception
    {
        String clientIp = ipUtils.getClientIp(request);
        String login = SecurityContextHolder.getContext().getAuthentication().getName();

        return ResponseEntity.ok(this.candidatService.updateDecision(idCdt, candidatDecisionDTO, login, clientIp));
    }

    @Operation(summary="Service de filtrage des états de versement")
    @GetMapping("/etat-versements/filter")
    public ResponseEntity<List<EtatDeVersement>> filterEVs(@RequestParam String etablissementId, @RequestParam Long session)
    {
        List<EtatDeVersement> result = candidatService.getFilteredEVs(etablissementId, session);
        return ResponseEntity.ok(result);
    }

    @Operation(summary="Service de filtrage des états de versement")
    @GetMapping("/etat-versements")
    public ResponseEntity<List<EtatDeVersement>> filterEVs(@RequestParam Long session)
    {
        List<EtatDeVersement> result = candidatService.getFilteredEVs_(session);
        return ResponseEntity.ok(result);
    }

    @Operation(summary="Service de listing des éditions du BAC")
    @GetMapping(value="/programmations")
    public ResponseEntity<List<Programmation>> getProgrammations() throws Exception {

        return ResponseEntity.ok(this.parametrageService.getProgs());
    }

    @Operation(summary="Service de recupération des droits FAEB")
    @GetMapping("/compte-droits-inscription")
    public ResponseEntity<CompteDroitsInscription> compte_droits_inscription(@RequestParam String establishmentId, @RequestParam Long session)
    {
        CompteDroitsInscription result = candidatService.getCompteDroitsInscription(establishmentId, session);
        return ResponseEntity.ok(result);
    }

    @Operation(summary="Service de recupération du nombre EPF")
    @GetMapping("/decompte-nombre-epFac")
    public ResponseEntity<Map<String, Long>> epreuveFac(@RequestParam String establishmentId, @RequestParam Long session)
    {
        Map<String, Long> result = candidatService.compterFacultatives(establishmentId, session);
        return ResponseEntity.ok(result);
    }

    @Operation(summary="Service de mise à jour du mandataire et d'autorisation des receptions")
    @PatchMapping(value="/autorisation-reception")
    public ResponseEntity<CompteDroitsInscription> updateAutorisation(@RequestParam String idCmptDroitInsc,
                                                   @RequestBody AutorisationReception autorisationReception) throws Exception
    {
        return ResponseEntity.ok(this.candidatService.enabledReception(idCmptDroitInsc, autorisationReception));
    }

    @Operation(summary="Boite noire")
    @GetMapping("/get-audit-reception-dosssier/{id}")
    public ResponseEntity<List<AuditLog>> getLogsByCandidate(@PathVariable("id") String candidateId) {
        List<AuditLog> logs = auditService.getLogsByCandidateId(candidateId);
        return ResponseEntity.ok(logs);
    }

    @GetMapping("/operator-daily/{start}/{end}/{session}")
    public List<OperatorDailyCountDTO> getDailyStats(
            @PathVariable("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @PathVariable("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end,
            @PathVariable("session") Integer session
    ) {
        return statsService.countDailyByOperator(start, end, session);
    }

    @GetMapping("/operations-reception/{session}")
    public List<EtablissementSummaryReception> getSummarizeOperations(@PathVariable("session") Long session) {
        return candidatService.summarize(session);
    }

    @GetMapping("/all-operations-reception/{session}")
    public EtablissementSummaryReception_ getSummarizeOperations_(@PathVariable("session") Long session) {
        return candidatService.summarize_(session);
    }
}
