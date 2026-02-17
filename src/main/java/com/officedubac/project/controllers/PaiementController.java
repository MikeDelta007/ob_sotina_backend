package com.officedubac.project.controllers;

import com.officedubac.project.models.Candidat;
import com.officedubac.project.models.DroitInscription;
import com.officedubac.project.services.PaiementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Update;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/v1/payment-FAEB3")
@RequiredArgsConstructor
@Tag(name="Paiement Controller", description = "Endpoints responsables de la gestion des paiements")
public class PaiementController
{
    private final MongoTemplate mongoTemplate;
    private final PaiementService paiementService;

    @Operation(summary="Service de paiement d'un droit d'inscription")
    @PostMapping("/createPayment/{etab_code}/{session}")
    public ResponseEntity<Map<String, Object>> createPayment(@RequestBody Map<String, Object> request, @PathVariable String etab_code, @PathVariable Long session) {
        return ResponseEntity.ok(this.paiementService.createPayment(request, etab_code, session));
    }

    @Operation(summary="Service de consultation des droits d'inscription")
    @GetMapping("/getDroitsInscription/{etab_code}/{session}")
    public ResponseEntity<List<DroitInscription>> getDroitsInscription(@PathVariable String etab_code, @PathVariable Long session) {
        return ResponseEntity.ok(this.paiementService.getDI(etab_code, session));
    }
}
