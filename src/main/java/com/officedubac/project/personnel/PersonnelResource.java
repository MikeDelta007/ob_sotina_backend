package com.officedubac.project.personnel;

import com.officedubac.project.models.Civilite;
import com.officedubac.project.models.User;
import com.officedubac.project.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/personnel")
@RequiredArgsConstructor
public class PersonnelResource {

    private final DivisionRepository divisionRepo;
    private final FonctionRepository fonctionRepo;
    private final VoitureRepository voitureRepo;
    private final PersonnelRepository personnelRepo;
    private final UserRepository userRepository;
    private final PersonnelCompteService personnelCompteService;

    // ── Divisions ──
    @GetMapping("/divisions")
    public ResponseEntity<List<Division>> getDivisions() {
        return ResponseEntity.ok(divisionRepo.findByActifTrue());
    }

    // Toutes les divisions (actives et inactives) — pour l'écran de gestion
    @GetMapping("/divisions/all")
    public ResponseEntity<List<Division>> getAllDivisions() {
        List<Division> divisions = divisionRepo.findAll();
        divisions.forEach(d -> {
            if (d.getChefServiceId() != null) {
                userRepository.findById(d.getChefServiceId()).ifPresent(u -> {
                    String nom = u.getPersonnel() != null
                            ? ((u.getPersonnel().getFirstname() != null ? u.getPersonnel().getFirstname() : "") + " "
                               + (u.getPersonnel().getLastname() != null ? u.getPersonnel().getLastname() : "")).trim()
                            : "";
                    d.setChefServiceNom(nom.isEmpty() ? u.getLogin() : nom);
                });
            }
        });
        return ResponseEntity.ok(divisions);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','CSA','DIRECTEUR')")
    @PostMapping("/divisions")
    public ResponseEntity<Division> creerDivision(@RequestBody Division division) {
        division.setActif(true);
        return ResponseEntity.ok(divisionRepo.save(division));
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','CSA','DIRECTEUR')")
    @PutMapping("/divisions/{id}")
    public ResponseEntity<Division> modifierDivision(@PathVariable String id, @RequestBody Division req) {
        Division division = divisionRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Division introuvable"));
        division.setLibelle(req.getLibelle());
        division.setChefServiceId(req.getChefServiceId());
        division.setActif(req.isActif());
        return ResponseEntity.ok(divisionRepo.save(division));
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','CSA','DIRECTEUR')")
    @DeleteMapping("/divisions/{id}")
    public ResponseEntity<Void> supprimerDivision(@PathVariable String id) {
        divisionRepo.findById(id).ifPresent(d -> { d.setActif(false); divisionRepo.save(d); });
        return ResponseEntity.noContent().build();
    }

    // ── Fonctions ──
    @GetMapping("/fonctions")
    public ResponseEntity<List<Fonction>> getFonctions() {
        return ResponseEntity.ok(fonctionRepo.findByActifTrue());
    }

    @GetMapping("/fonctions/all")
    public ResponseEntity<List<Fonction>> getAllFonctions() {
        return ResponseEntity.ok(fonctionRepo.findAll());
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','CSA','DIRECTEUR')")
    @PostMapping("/fonctions")
    public ResponseEntity<Fonction> creerFonction(@RequestBody Fonction fonction) {
        fonction.setActif(true);
        return ResponseEntity.ok(fonctionRepo.save(fonction));
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','CSA','DIRECTEUR')")
    @PutMapping("/fonctions/{id}")
    public ResponseEntity<Fonction> modifierFonction(@PathVariable String id, @RequestBody Fonction req) {
        Fonction fonction = fonctionRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Fonction introuvable"));
        fonction.setLibelle(req.getLibelle());
        fonction.setActif(req.isActif());
        return ResponseEntity.ok(fonctionRepo.save(fonction));
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','CSA','DIRECTEUR')")
    @DeleteMapping("/fonctions/{id}")
    public ResponseEntity<Void> supprimerFonction(@PathVariable String id) {
        fonctionRepo.findById(id).ifPresent(f -> { f.setActif(false); fonctionRepo.save(f); });
        return ResponseEntity.noContent().build();
    }

    // ── Voitures (véhicules réutilisables pour les missions) ──
    @GetMapping("/voitures")
    public ResponseEntity<List<Voiture>> getVoitures() {
        return ResponseEntity.ok(voitureRepo.findByActifTrue());
    }

    @GetMapping("/voitures/all")
    public ResponseEntity<List<Voiture>> getAllVoitures() {
        return ResponseEntity.ok(voitureRepo.findAll());
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','CSA','DIRECTEUR')")
    @PostMapping("/voitures")
    public ResponseEntity<Voiture> creerVoiture(@RequestBody Voiture voiture) {
        voiture.setActif(true);
        if (voiture.getCapacite() <= 0) voiture.setCapacite(4);
        resoudreProprietaire(voiture);
        return ResponseEntity.ok(voitureRepo.save(voiture));
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','CSA','DIRECTEUR')")
    @PutMapping("/voitures/{id}")
    public ResponseEntity<Voiture> modifierVoiture(@PathVariable String id, @RequestBody Voiture req) {
        Voiture voiture = voitureRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Voiture introuvable"));
        voiture.setImmatriculation(req.getImmatriculation());
        voiture.setMarque(req.getMarque());
        voiture.setActif(req.isActif());
        voiture.setProprietaireType(req.getProprietaireType());
        voiture.setProprietaireAgentId(req.getProprietaireAgentId());
        voiture.setProprietairePersonnelId(req.getProprietairePersonnelId());
        resoudreProprietaire(voiture);
        return ResponseEntity.ok(voitureRepo.save(voiture));
    }

    // Renseigne le nom du propriétaire (snapshot) selon proprietaireType, et nettoie les
    // références qui ne correspondent pas au type choisi.
    private void resoudreProprietaire(Voiture voiture) {
        ProprietaireVoiture type = voiture.getProprietaireType() != null ? voiture.getProprietaireType() : ProprietaireVoiture.OFFICE;
        voiture.setProprietaireType(type);

        if (type == ProprietaireVoiture.AGENT && voiture.getProprietaireAgentId() != null && !voiture.getProprietaireAgentId().isBlank()) {
            User agent = userRepository.findById(voiture.getProprietaireAgentId())
                    .orElseThrow(() -> new RuntimeException("Agent propriétaire introuvable"));
            voiture.setProprietaireAgentNom(agent.getPersonnel().getFirstname() + " " + agent.getPersonnel().getLastname());
            voiture.setProprietairePersonnelId(null);
            voiture.setProprietairePersonnelNom(null);
        } else if (type == ProprietaireVoiture.EXTERNE && voiture.getProprietairePersonnelId() != null && !voiture.getProprietairePersonnelId().isBlank()) {
            Personnel personnel = personnelRepo.findById(voiture.getProprietairePersonnelId())
                    .orElseThrow(() -> new RuntimeException("Personnel externe propriétaire introuvable"));
            voiture.setProprietairePersonnelNom(personnel.getFirstname() + " " + personnel.getLastname());
            voiture.setProprietaireAgentId(null);
            voiture.setProprietaireAgentNom(null);
        } else {
            voiture.setProprietaireType(ProprietaireVoiture.OFFICE);
            voiture.setProprietaireAgentId(null);
            voiture.setProprietaireAgentNom(null);
            voiture.setProprietairePersonnelId(null);
            voiture.setProprietairePersonnelNom(null);
        }
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','CSA','DIRECTEUR')")
    @DeleteMapping("/voitures/{id}")
    public ResponseEntity<Void> supprimerVoiture(@PathVariable String id) {
        voitureRepo.findById(id).ifPresent(v -> { v.setActif(false); voitureRepo.save(v); });
        return ResponseEntity.noContent().build();
    }

    // Agents (fiches Personnel, avec ou sans compte) des divisions dont l'utilisateur connecté
    // est chef de service — utilisé par l'écran des demandes d'autorisation d'absence pour
    // lister l'équipe d'un chef. On s'appuie sur Personnel (le référentiel du personnel), pas
    // sur User, pour couvrir aussi les agents sans compte.
    // Tout le personnel (fiches Personnel actives) : liste des agents d'un ticket restaurant.
    @GetMapping("/tous-agents")
    public ResponseEntity<List<Personnel>> tousLesAgents() {
        List<Personnel> agents = new ArrayList<>(personnelRepo.findByActifTrue());
        agents.sort(java.util.Comparator.comparing(a -> ((a.getLastname() != null ? a.getLastname() : "") + " " + (a.getFirstname() != null ? a.getFirstname() : "")).toUpperCase()));
        return ResponseEntity.ok(agents);
    }

    @GetMapping("/mes-agents")
    public ResponseEntity<List<Personnel>> mesAgents() {
        String login = SecurityContextHolder.getContext().getAuthentication().getName();
        User chef = userRepository.findByLogin(login).orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        List<String> divisionsDontJeSuisChef = divisionRepo.findByActifTrue().stream()
                .filter(d -> chef.getId().equals(d.getChefServiceId()))
                .map(Division::getId)
                .collect(java.util.stream.Collectors.toList());

        if (divisionsDontJeSuisChef.isEmpty()) {
            return ResponseEntity.ok(List.of());
        }

        // Agents externes (fiche Personnel autonome, sans compte)
        List<User> comptes = personnelCompteService.comptes();
        List<Personnel> agents = personnelRepo.findByDivision_IdInAndActifTrue(divisionsDontJeSuisChef).stream()
                .filter(p -> !personnelCompteService.aUnCompte(p, comptes))
                .collect(java.util.stream.Collectors.toCollection(ArrayList::new));

        // Agents internes (avec compte applicatif) de ces mêmes divisions — l'id renvoyé
        // est alors celui du User, pour que cet agent puisse ensuite retrouver ce qui le
        // concerne (ex. expressions de besoin) via ses propres écrans en lecture seule.
        userRepository.findByPersonnel_Division_IdIn(divisionsDontJeSuisChef).stream()
                .filter(u -> !u.getId().equals(chef.getId()))
                .forEach(u -> {
                    Personnel p = u.getPersonnel();
                    if (p != null) {
                        p.setId(u.getId());
                        agents.add(p);
                    }
                });

        return ResponseEntity.ok(agents);
    }

    // ── Personnels (identité + fonction, généralement externes — sans compte utilisateur).
    // Un chauffeur n'est pas un type à part : c'est un Personnel dont la fonction est "Chauffeur". ──
    // Fiches actives SANS compte (liste de création d'un compte) — une fiche déjà rattachée à un
    // compte n'y apparaît plus, mais reste dans /personnels/all (gestion du personnel).
    @GetMapping("/personnels")
    public ResponseEntity<List<Personnel>> getPersonnels() {
        List<User> comptes = personnelCompteService.comptes();
        return ResponseEntity.ok(personnelRepo.findByActifTrue().stream()
                .filter(p -> !personnelCompteService.aUnCompte(p, comptes))
                .collect(java.util.stream.Collectors.toList()));
    }

    @GetMapping("/personnels/all")
    public ResponseEntity<List<Personnel>> getAllPersonnels() {
        return ResponseEntity.ok(personnelRepo.findAll());
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','CSA','DIRECTEUR')")
    @PostMapping("/personnels")
    public ResponseEntity<Personnel> creerPersonnel(@RequestBody Personnel personnel) {
        personnel.setActif(true);
        personnel.setMatricule(sansEspaces(personnel.getMatricule()));
        personnel.setPhone(sansEspaces(personnel.getPhone()));
        return ResponseEntity.ok(personnelRepo.save(personnel));
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','CSA','DIRECTEUR')")
    @PutMapping("/personnels/{id}")
    public ResponseEntity<Personnel> modifierPersonnel(@PathVariable String id, @RequestBody Personnel req) {
        Personnel personnel = personnelRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Personnel introuvable"));
        personnel.setFirstname(req.getFirstname());
        personnel.setLastname(req.getLastname());
        personnel.setPhone(sansEspaces(req.getPhone()));
        personnel.setEmail(req.getEmail());
        personnel.setMatricule(sansEspaces(req.getMatricule()));
        personnel.setCivilite(req.getCivilite());
        personnel.setBank(req.getBank());
        personnel.setCode_bank(req.getCode_bank());
        personnel.setCode_agc(req.getCode_agc());
        personnel.setNum_compte(req.getNum_compte());
        personnel.setKey_rib(req.getKey_rib());
        personnel.setDivision(req.getDivision());
        personnel.setFonction(req.getFonction());
        personnel.setTypePersonnel(req.getTypePersonnel());
        personnel.setSoldeConges(req.getTypePersonnel() != null ? req.getTypePersonnel().joursConges() : null);
        personnel.setActif(req.isActif());
        return ResponseEntity.ok(personnelRepo.save(personnel));
    }

    // Retire tous les espaces (matricule/téléphone souvent copiés-collés avec des espaces
    // décoratifs depuis Excel, ex: "102 578/C", "77 818 53 39").
    private String sansEspaces(String value) {
        return value == null ? null : value.replaceAll("\\s+", "");
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','CSA','DIRECTEUR')")
    @DeleteMapping("/personnels/{id}")
    public ResponseEntity<Void> supprimerPersonnel(@PathVariable String id) {
        personnelRepo.findById(id).ifPresent(p -> { p.setActif(false); personnelRepo.save(p); });
        return ResponseEntity.noContent().build();
    }

    // Import Excel de fiches Personnel (sans compte). Les colonnes sont détectées PAR NOM D'EN-TÊTE
    // (insensible à la casse/accents/ordre), pas par position fixe — tolère les variantes réelles
    // (ex: "Prénoms", "NOM", "Contact", "Service / Division"). Colonnes reconnues : Prénom(s), Nom,
    // Téléphone/Contact, Email, Matricule, Civilité, Division/Service, Fonction, Type de personnel.
    // Obligatoires : Prénom, Nom, Téléphone, Email, Division, Fonction (présents dans un export RH
    // classique). Optionnels : Matricule, Civilité, Type de personnel (souvent absents des exports
    // existants). Division/Fonction sont auto-créées si leur libellé n'existe pas encore. Matricule
    // (si renseigné), email et téléphone doivent être uniques — doublons et lignes incomplètes ignorés.
    @PreAuthorize("hasAnyAuthority('ADMIN','CSA','DIRECTEUR')")
    @PostMapping("/personnels/import")
    public ResponseEntity<String> importerPersonnels(@RequestParam("file") MultipartFile file) {
        List<Division> divisions = new ArrayList<>(divisionRepo.findByActifTrue());
        List<Fonction> fonctions = new ArrayList<>(fonctionRepo.findByActifTrue());

        List<Personnel> existants = personnelRepo.findAll();
        java.util.Set<String> matriculesVus = existants.stream().map(Personnel::getMatricule)
                .filter(m -> m != null && !m.isBlank()).map(m -> sansEspaces(m).toLowerCase())
                .collect(java.util.stream.Collectors.toCollection(java.util.HashSet::new));
        java.util.Set<String> emailsVus = existants.stream().map(Personnel::getEmail)
                .filter(m -> m != null && !m.isBlank()).map(m -> m.trim().toLowerCase())
                .collect(java.util.stream.Collectors.toCollection(java.util.HashSet::new));
        java.util.Set<String> phonesVus = existants.stream().map(Personnel::getPhone)
                .filter(m -> m != null && !m.isBlank()).map(this::sansEspaces)
                .collect(java.util.stream.Collectors.toCollection(java.util.HashSet::new));

        List<Personnel> aImporter = new ArrayList<>();
        int incompletes = 0;
        int doublons = 0;

        try (XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            Row entete = sheet.getRow(sheet.getFirstRowNum());
            if (entete == null) {
                return ResponseEntity.badRequest().body("Fichier vide ou illisible");
            }

            java.util.Map<String, Integer> colonnes = new java.util.HashMap<>();
            for (Cell cell : entete) {
                String v = cellValue(cell);
                if (v != null && !v.isBlank()) colonnes.put(normaliser(v), cell.getColumnIndex());
            }
            Integer colPrenom = colonneParAlias(colonnes, "prenom", "prenoms", "prenom s");
            Integer colNom = colonneParAlias(colonnes, "nom");
            Integer colTelephone = colonneParAlias(colonnes, "telephone", "contact", "tel", "tel phone");
            Integer colEmail = colonneParAlias(colonnes, "email", "mail", "e mail");
            Integer colMatricule = colonneParAlias(colonnes, "matricule");
            Integer colCivilite = colonneParAlias(colonnes, "civilite");
            Integer colDivision = colonneParAlias(colonnes, "division", "service division", "service");
            Integer colFonction = colonneParAlias(colonnes, "fonction");
            Integer colType = colonneParAlias(colonnes, "type de personnel", "type personnel", "type");

            if (colPrenom == null || colNom == null || colTelephone == null || colEmail == null
                    || colDivision == null || colFonction == null) {
                return ResponseEntity.badRequest().body(
                        "Colonnes obligatoires introuvables dans l'en-tête (Prénom, Nom, Téléphone, Email, Division, Fonction) — "
                                + "en-têtes détectés : " + String.join(", ", colonnes.keySet()));
            }

            for (Row row : sheet) {
                if (row.getRowNum() == entete.getRowNum()) continue;

                String firstname = cellValue(row.getCell(colPrenom));
                String lastname = cellValue(row.getCell(colNom));
                String phone = sansEspaces(cellValue(row.getCell(colTelephone)));
                String email = cellValue(row.getCell(colEmail));
                String matricule = colMatricule != null ? sansEspaces(cellValue(row.getCell(colMatricule))) : null;
                Civilite civilite = colCivilite != null ? parseCivilite(cellValue(row.getCell(colCivilite))) : null;
                String divisionLibelle = cellValue(row.getCell(colDivision));
                String fonctionLibelle = cellValue(row.getCell(colFonction));
                TypePersonnel type = colType != null ? parseTypePersonnel(cellValue(row.getCell(colType))) : null;

                boolean ligneComplete = firstname != null && !firstname.isBlank()
                        && lastname != null && !lastname.isBlank()
                        && phone != null && !phone.isBlank()
                        && email != null && !email.isBlank()
                        && divisionLibelle != null && !divisionLibelle.isBlank()
                        && fonctionLibelle != null && !fonctionLibelle.isBlank();
                if (!ligneComplete) { incompletes++; continue; }

                String matriculeCle = matricule != null && !matricule.isBlank() ? matricule.toLowerCase() : null;
                String emailCle = email.trim().toLowerCase();
                String phoneCle = phone;
                if ((matriculeCle != null && matriculesVus.contains(matriculeCle))
                        || emailsVus.contains(emailCle) || phonesVus.contains(phoneCle)) {
                    doublons++; continue;
                }

                Division division = trouverOuCreerDivision(divisions, divisionLibelle);
                Fonction fonction = trouverOuCreerFonction(fonctions, fonctionLibelle);

                Personnel personnel = Personnel.builder()
                        .firstname(firstname)
                        .lastname(lastname)
                        .phone(phone)
                        .email(email)
                        .matricule(matricule)
                        .civilite(civilite)
                        .division(division)
                        .fonction(fonction)
                        .typePersonnel(type)
                        .soldeConges(type != null ? type.joursConges() : null)
                        .actif(true)
                        .build();

                aImporter.add(personnel);
                if (matriculeCle != null) matriculesVus.add(matriculeCle);
                emailsVus.add(emailCle);
                phonesVus.add(phoneCle);
            }
        } catch (IOException e) {
            return ResponseEntity.badRequest().body("Erreur lors de la lecture du fichier : " + e.getMessage());
        }

        if (aImporter.isEmpty()) {
            return ResponseEntity.badRequest().body("Aucune ligne valide trouvée dans le fichier (Prénom/Nom/Téléphone/Email/Division/Fonction obligatoires ; matricule/email/téléphone doivent être uniques)");
        }
        personnelRepo.saveAll(aImporter);
        String message = aImporter.size() + " personnel(s) importé(s) avec succès";
        if (incompletes > 0) message += " — " + incompletes + " ligne(s) incomplète(s) ignorée(s)";
        if (doublons > 0) message += " — " + doublons + " doublon(s) ignoré(s) (matricule/email/téléphone déjà utilisé)";
        return ResponseEntity.ok(message);
    }

    private Integer colonneParAlias(java.util.Map<String, Integer> colonnes, String... alias) {
        for (String a : alias) {
            Integer idx = colonnes.get(a);
            if (idx != null) return idx;
        }
        return null;
    }

    // Résout une Division par libellé (insensible à la casse), la crée si elle n'existe pas
    // encore — permet d'initialiser la liste des divisions directement depuis le fichier importé.
    private Division trouverOuCreerDivision(List<Division> divisions, String libelle) {
        return divisions.stream()
                .filter(d -> d.getLibelle().trim().equalsIgnoreCase(libelle.trim()))
                .findFirst()
                .orElseGet(() -> {
                    Division nouvelle = divisionRepo.save(Division.builder().libelle(libelle.trim()).actif(true).build());
                    divisions.add(nouvelle);
                    return nouvelle;
                });
    }

    private Fonction trouverOuCreerFonction(List<Fonction> fonctions, String libelle) {
        return fonctions.stream()
                .filter(f -> f.getLibelle().trim().equalsIgnoreCase(libelle.trim()))
                .findFirst()
                .orElseGet(() -> {
                    Fonction nouvelle = fonctionRepo.save(Fonction.builder().libelle(libelle.trim()).actif(true).build());
                    fonctions.add(nouvelle);
                    return nouvelle;
                });
    }

    // Normalise pour une comparaison tolérante : minuscules, sans accents, sans ponctuation,
    // espaces multiples réduits à un seul — permet d'accepter aussi bien "PERSONNEL_SECURITE"
    // (nom technique) que "Personnel de sécurité" (libellé naturel tapé dans Excel).
    private String normaliser(String value) {
        String sansAccents = java.text.Normalizer.normalize(value.trim().toLowerCase(), java.text.Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        return sansAccents.replaceAll("[^a-z0-9]+", " ").trim();
    }

    private Civilite parseCivilite(String value) {
        if (value == null || value.isBlank()) return null;
        String v = normaliser(value);
        return switch (v) {
            case "mr", "m", "monsieur" -> Civilite.Mr;
            case "mme", "madame" -> Civilite.Mme;
            case "mlle", "mademoiselle" -> Civilite.Mlle;
            default -> null;
        };
    }

    private TypePersonnel parseTypePersonnel(String value) {
        if (value == null || value.isBlank()) return null;
        String v = normaliser(value);
        return switch (v) {
            case "permanent" -> TypePersonnel.PERMANENT;
            case "personnel securite", "personnel de securite", "securite", "agent de securite" -> TypePersonnel.PERSONNEL_SECURITE;
            case "personnel appui", "personnel d appui", "appui" -> TypePersonnel.PERSONNEL_APPUI;
            case "externe" -> TypePersonnel.EXTERNE;
            default -> null;
        };
    }

    private String cellValue(Cell cell) {
        if (cell == null) return null;
        switch (cell.getCellType()) {
            case STRING -> {
                String v = cell.getStringCellValue();
                return v == null ? null : v.trim();
            }
            case NUMERIC -> {
                if (DateUtil.isCellDateFormatted(cell)) return cell.getDateCellValue().toString();
                double num = cell.getNumericCellValue();
                return num == (long) num ? String.valueOf((long) num) : String.valueOf(num);
            }
            case BOOLEAN -> {
                return String.valueOf(cell.getBooleanCellValue());
            }
            default -> {
                return null;
            }
        }
    }
}
