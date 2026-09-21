package com.officedubac.project.banque.v1;

import com.officedubac.project.banque.Banque;
import com.officedubac.project.banque.BanqueService;
import com.officedubac.project.banque.dto.BanqueRequest;
import com.officedubac.project.banque.dto.BanqueResponse;
import com.officedubac.project.exception.ResourceNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
@RestController
@RequestMapping("/api/v1/banques")
@RequiredArgsConstructor
@PreAuthorize("hasAnyAuthority('ADMIN')")
public class BanqueResource {
    private final BanqueService service;
    @GetMapping("/all")
    @PreAuthorize("isAuthenticated()")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<BanqueResponse>> all(){
        List<BanqueResponse> response = service.all();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @GetMapping(value = "/{id}")
    public ResponseEntity<Optional<BanqueResponse>> one(@PathVariable(value = "id") String id) {
        Optional<BanqueResponse> response = service.oneById(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @PostMapping(value = "/")
    // @PreAuthorize("hasRole('USER_ADD') or hasRole('ADMIN')")
    public ResponseEntity<BanqueResponse> add(@RequestBody @Valid BanqueRequest request) {
        BanqueResponse response = service.add(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    @PutMapping(value = "/{id}")
    // @PreAuthorize("hasRole('USER_MAJ') or hasRole('ADMIN')")
    public ResponseEntity<BanqueResponse> maj(@PathVariable(value="id") String id,
                                                @RequestBody @Valid BanqueRequest request) {
        BanqueResponse response = service.maj(request, id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @DeleteMapping(value = "/{id}")
    // @PreAuthorize("hasRole('USER_DEL') or hasRole('ADMIN')")
    public ResponseEntity<Void> del(@PathVariable(value="id") String id) {
        service.del(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    @GetMapping("/libelle-availability")
    public ResponseEntity<Boolean> checkFonctionAvailability(@RequestParam String libelle) {
        try {
            service.verifyBanqueUnique(libelle);
            return ResponseEntity.ok(true);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.ok(false);
        }
    }
    @PostMapping("/import")
    public ResponseEntity<?> importerDepuisExcel(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Aucun fichier n'a été envoyé.");
        }
        try {
            List<Banque> logs = service.importExcel(file.getInputStream());
            return ResponseEntity.ok(logs); // Retourne la liste des logs comme JSON
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de la lecture du fichier : " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Erreur lors de l'importation : " + e.getMessage());
        }

    }
}

