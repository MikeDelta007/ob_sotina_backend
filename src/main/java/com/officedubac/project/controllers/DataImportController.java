package com.officedubac.project.controllers;

import com.officedubac.project.dto.*;
import com.officedubac.project.models.*;
import com.officedubac.project.repository.CandidatRepository;
import com.officedubac.project.repository.NotificationRepository;
import com.officedubac.project.services.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/v1/import-data")
@RequiredArgsConstructor
@Tag(name="Data Import Controller", description = "Endpoints responsables de la gestion des imports de données")

public class DataImportController
{
    @Autowired
    private final ImportDataService importDataService;
    @Autowired
    private final ParametrageService parametrageService;
    @Autowired
    private final TirageJuryMatService tirageJuryMatService;

    @Autowired
    private final DecompteFeuilleJuryService decompteFeuilleJuryService;

    //SOTINA
    @PostMapping(value="/fusion-repartition")
    public ResponseEntity<?> fusionTirage()
    {
        try
        {
            this.tirageJuryMatService.unionCollections();
            return ResponseEntity.ok("Fusion effectuée avec succès");
        }
        catch (Exception e)
        {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur fusion: " + e.getMessage());
        }
    }

    //SOTINA
    @PostMapping(value="/fusion-feuille")
    public ResponseEntity<?> fusionFeuille()
    {
        try
        {
            this.decompteFeuilleJuryService.unionCollections();
            return ResponseEntity.ok("Fusion effectuée avec succès");
        }
        catch (Exception e)
        {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur fusion: " + e.getMessage());
        }
    }



    //SOTINA
    @PostMapping("/data-candidats")
    public String importCdtsByFile(@RequestParam("file") MultipartFile file)
    {
        String message;
        try
        {
            // Sauvegarder le fichier temporairement
            File tempFile = File.createTempFile("data_cdt_", ".xlsx");
            file.transferTo(tempFile);
            // Appeler le service
            boolean ok = parametrageService.importCdtByFile(tempFile.getAbsolutePath());
            // Supprimer le fichier temporaire après import
            tempFile.delete();
            if (ok)
            {
                message = "Les données ont été chargées avec succés.";
            }
            else
            {
                message = "Aucune donnée n\'a été chargée";
            }
            return message;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return "Erreur lors de l'import : " + e.getMessage();
        }
    }

    //SOTINA
    @GetMapping("/get-all-candidats/{page}/{size}")
    public ResponseEntity<?> getCandidats(
            @PathVariable int page,
            @PathVariable int size
    ) {
        Page<SourceCandidatDTO> p = this.tirageJuryMatService.getListCandidats(page, size);

        Map<String, Object> res = new HashMap<>();
        res.put("content", p.getContent());
        res.put("totalElements", p.getTotalElements());
        res.put("totalPages", p.getTotalPages());
        res.put("size", p.getSize());
        res.put("page", p.getNumber());

        return ResponseEntity.ok(res);
    }

    //SOTINA
    @Operation(summary="")
    @GetMapping(value="/repCP-by-aca")
    public ResponseEntity<Map<String, List<RepartitionTirageCEP>>> repCP() throws Exception
    {
        return ResponseEntity.ok(this.tirageJuryMatService.getRepCPByAcademie());
    }

    //SOTINA - Export Excel
    @Operation(summary="")
    @GetMapping(value="/repCP-all")
    public ResponseEntity<List<RepartitionTirageCEP>> repCP_() throws Exception
    {
        return ResponseEntity.ok(this.tirageJuryMatService.getRepCP_());
    }

    @Operation(summary="")
    @GetMapping(value="/repCS-by-aca")
    public ResponseEntity<Map<String, List<RepartitionTirageCES>>> repCS() throws Exception
    {
        return ResponseEntity.ok(this.tirageJuryMatService.getRepCSByAcademie());
    }

    //SOTINA


    //SOTINA
    @PostMapping("/repartition-cep")
    public ResponseEntity<List<RepartitionTirageCEPDTO>> repartitionTirageCEP()
    {
        return ResponseEntity.ok(this.tirageJuryMatService.repartitionParCEP());
    }

    //SOTINA
    @PostMapping("/repartition-cs")
    public ResponseEntity<List<RepartitionTirageCSDTO>> repartitionTirageCS()
    {
        return ResponseEntity.ok(this.tirageJuryMatService.repartitionParCS());
    }

    //SOTINA
    @PostMapping("/feuille-cep")
    public ResponseEntity<List<RepartitionFeuilleCEPDTO>> repartitionFeuillesCEP()
    {
        return ResponseEntity.ok(this.decompteFeuilleJuryService.repartitionParCEP());
    }

    //SOTINA
    @PostMapping("/feuille-cs")
    public ResponseEntity<List<RepartitionFeuilleCESDTO>> repartitionFeuilleCS()
    {
        return ResponseEntity.ok(this.decompteFeuilleJuryService.repartitionParCS());
    }

    //SOTINA
    @Operation(summary="")
    @GetMapping(value="/all-feuille-by-aca")
    public ResponseEntity<Map<String, List<FusionRepartitionFeuille>>> repFCP() throws Exception
    {
        return ResponseEntity.ok(this.decompteFeuilleJuryService.getRepCPByAcademie());
    }

    //SOTINA
    @Operation(summary="")
    @GetMapping(value="/feuilleCS-by-aca")
    public ResponseEntity<Map<String, List<RepartitionFeuilleCES>>> repFCS() throws Exception
    {
        return ResponseEntity.ok(this.decompteFeuilleJuryService.getRepCSByAcademie());
    }

    //SOTINA
    @Operation(summary="")
    @GetMapping(value="/get-all-fusion-tirage")
    public ResponseEntity<Map<String, List<FusionRepartitionTirage>>> fusionRep() throws Exception
    {
        return ResponseEntity.ok(this.tirageJuryMatService.getAllFusionRepTirage());
    }

    //SOTINA


    @GetMapping("/horaires")
    public HoraireRequest getHoraires() {
        return tirageJuryMatService.getHoraires();
    }

}
