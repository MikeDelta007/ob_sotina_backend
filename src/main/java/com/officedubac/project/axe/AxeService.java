package com.officedubac.project.axe;

import com.officedubac.project.models.Etablissement;
import com.officedubac.project.repository.EtablissementRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AxeService {

    private final AxeRepository axeRepo;
    private final EtablissementRepository etablissementRepo;

    // ═══════════════════════════════════════════════════════════════
    // CRUD
    // ═══════════════════════════════════════════════════════════════
    public List<Axe> getAll() {
        return axeRepo.findByActifTrueOrderByNumeroAsc();
    }

    public List<Axe> getAllAdmin() {
        return axeRepo.findByOrderByNumeroAsc();
    }

    public Axe getById(String id) {
        return axeRepo.findById(id).orElseThrow(() -> new RuntimeException("Axe introuvable : " + id));
    }

    public Axe creer(AxeRequest req) {
        if (axeRepo.findByNumero(req.getNumero()).isPresent())
            throw new RuntimeException("Un axe numéro " + req.getNumero() + " existe déjà");

        Axe axe = Axe.builder()
                .numero(req.getNumero())
                .itineraire(req.getItineraire())
                .actif(true)
                .etablissements(resoudreLignes(req.getEtablissements()))
                .creePar(getUsername())
                .build();
        return axeRepo.save(axe);
    }

    public Axe modifier(String id, AxeRequest req) {
        Axe axe = getById(id);
        axeRepo.findByNumero(req.getNumero())
                .filter(a -> !a.getId().equals(id))
                .ifPresent(a -> { throw new RuntimeException("Un axe numéro " + req.getNumero() + " existe déjà"); });

        axe.setNumero(req.getNumero());
        axe.setItineraire(req.getItineraire());
        axe.setEtablissements(resoudreLignes(req.getEtablissements()));
        return axeRepo.save(axe);
    }

    public void supprimer(String id) {
        Axe axe = getById(id);
        axe.setActif(false);
        axeRepo.save(axe);
    }

    private List<Axe.AxeEtablissement> resoudreLignes(List<AxeRequest.LigneRequest> lignes) {
        if (lignes == null) return List.of();
        List<Axe.AxeEtablissement> resultat = new ArrayList<>();
        for (AxeRequest.LigneRequest ligne : lignes) {
            Etablissement etab = etablissementRepo.findById(ligne.getEtablissementId())
                    .orElseThrow(() -> new RuntimeException("Établissement introuvable : " + ligne.getEtablissementId()));
            resultat.add(Axe.AxeEtablissement.builder()
                    .etablissementId(etab.getId())
                    .etablissementNom(etab.getName())
                    .centreSecondaire(ligne.isCentreSecondaire())
                    .relais(ligne.getRelais())
                    .build());
        }
        return resultat;
    }

    // ═══════════════════════════════════════════════════════════════
    // IMPORT EXCEL
    // ═══════════════════════════════════════════════════════════════
    // Format attendu : un classeur avec les onglets "Récapitulatif" (Axe, Itinéraire) et
    // "Détail" (Axe, Établissement (scan), Centre secondaire (CS), Relais, Établissement trouvé
    // en base, ID établissement) — exactement la forme du fichier généré pour la vérification
    // des données avant import. Un axe déjà existant (même numéro) est entièrement remplacé par
    // le contenu du fichier ; les autres champs (id, dateCreation) sont préservés en mettant à
    // jour l'entité existante plutôt qu'en la recréant.
    private static class AxeDraft {
        String itineraire;
        final List<Axe.AxeEtablissement> etablissements = new ArrayList<>();
    }

    public AxeImportResult importer(MultipartFile fichier) {
        Map<Integer, AxeDraft> brouillons = new LinkedHashMap<>();
        List<String> avertissements = new ArrayList<>();
        int lignesImportees = 0;
        int lignesIgnorees = 0;

        try (XSSFWorkbook workbook = new XSSFWorkbook(fichier.getInputStream())) {
            Sheet recap = feuille(workbook, "Récapitulatif", "Recapitulatif");
            if (recap != null) {
                Map<String, Integer> colonnes = entetes(recap);
                Integer colAxe = colonneParAlias(colonnes, "axe");
                Integer colItineraire = colonneParAlias(colonnes, "itineraire");
                if (colAxe != null) {
                    for (Row row : recap) {
                        if (row.getRowNum() == recap.getFirstRowNum()) continue;
                        String axeStr = cellValue(row.getCell(colAxe));
                        if (axeStr == null || axeStr.isBlank() || !axeStr.matches("-?\\d+")) continue;
                        int numero = Integer.parseInt(axeStr);
                        AxeDraft brouillon = brouillons.computeIfAbsent(numero, n -> new AxeDraft());
                        brouillon.itineraire = colItineraire != null ? cellValue(row.getCell(colItineraire)) : null;
                    }
                }
            }

            Sheet detail = feuille(workbook, "Détail", "Detail");
            if (detail == null)
                throw new RuntimeException("Onglet \"Détail\" introuvable dans le fichier");

            Map<String, Integer> colonnes = entetes(detail);
            Integer colAxe = colonneParAlias(colonnes, "axe");
            Integer colCS = colonneParAlias(colonnes, "centre secondaire cs", "cs");
            Integer colRelais = colonneParAlias(colonnes, "relais");
            Integer colNomBase = colonneParAlias(colonnes, "etablissement trouve en base");
            Integer colId = colonneParAlias(colonnes, "id etablissement");
            Integer colNomScan = colonneParAlias(colonnes, "etablissement scan", "etablissement");

            if (colAxe == null)
                throw new RuntimeException("Colonne \"Axe\" introuvable dans l'onglet Détail");

            for (Row row : detail) {
                if (row.getRowNum() == detail.getFirstRowNum()) continue;
                String axeStr = cellValue(row.getCell(colAxe));
                if (axeStr == null || axeStr.isBlank() || !axeStr.matches("-?\\d+")) continue;
                int numero = Integer.parseInt(axeStr);

                String nomScan = colNomScan != null ? cellValue(row.getCell(colNomScan)) : null;
                String idEtab = colId != null ? cellValue(row.getCell(colId)) : null;
                String nomBase = colNomBase != null ? cellValue(row.getCell(colNomBase)) : null;
                boolean cs = colCS != null && "oui".equalsIgnoreCase(trim(cellValue(row.getCell(colCS))));
                String relais = colRelais != null ? cellValue(row.getCell(colRelais)) : null;

                Etablissement etab = null;
                if (idEtab != null && !idEtab.isBlank()) {
                    etab = etablissementRepo.findById(idEtab.trim()).orElse(null);
                }
                if (etab == null && nomBase != null && !nomBase.isBlank()) {
                    etab = etablissementRepo.findByName(nomBase.trim());
                }
                if (etab == null) {
                    lignesIgnorees++;
                    avertissements.add("Axe " + numero + ", ligne " + (row.getRowNum() + 1)
                            + " : établissement non résolu (\"" + (nomScan != null ? nomScan : nomBase) + "\")");
                    continue;
                }

                AxeDraft brouillon = brouillons.computeIfAbsent(numero, n -> new AxeDraft());
                brouillon.etablissements.add(Axe.AxeEtablissement.builder()
                        .etablissementId(etab.getId())
                        .etablissementNom(etab.getName())
                        .centreSecondaire(cs)
                        .relais(relais)
                        .build());
                lignesImportees++;
            }
        } catch (IOException e) {
            throw new RuntimeException("Fichier illisible : " + e.getMessage(), e);
        }

        String username = getUsername();
        int axesImportes = 0;
        for (Map.Entry<Integer, AxeDraft> entry : brouillons.entrySet()) {
            AxeDraft brouillon = entry.getValue();
            if (brouillon.etablissements.isEmpty()) continue;

            Axe axe = axeRepo.findByNumero(entry.getKey()).orElseGet(() -> Axe.builder()
                    .numero(entry.getKey()).actif(true).creePar(username).build());
            axe.setItineraire(brouillon.itineraire);
            axe.setEtablissements(brouillon.etablissements);
            axeRepo.save(axe);
            axesImportes++;
        }

        return AxeImportResult.builder()
                .axesImportes(axesImportes)
                .lignesImportees(lignesImportees)
                .lignesIgnorees(lignesIgnorees)
                .avertissements(avertissements)
                .build();
    }

    // ── Utilitaires fichier ──
    private Sheet feuille(XSSFWorkbook workbook, String... noms) {
        for (String nom : noms) {
            Sheet s = workbook.getSheet(nom);
            if (s != null) return s;
        }
        return null;
    }

    private Map<String, Integer> entetes(Sheet sheet) {
        Map<String, Integer> colonnes = new HashMap<>();
        Row entete = sheet.getRow(sheet.getFirstRowNum());
        if (entete == null) return colonnes;
        for (Cell cell : entete) {
            String v = cellValue(cell);
            if (v != null && !v.isBlank()) colonnes.put(normaliser(v), cell.getColumnIndex());
        }
        return colonnes;
    }

    private Integer colonneParAlias(Map<String, Integer> colonnes, String... alias) {
        for (String a : alias) {
            Integer idx = colonnes.get(a);
            if (idx != null) return idx;
        }
        return null;
    }

    // Normalise pour une comparaison tolérante : minuscules, sans accents, sans ponctuation,
    // espaces multiples réduits à un seul.
    private String normaliser(String value) {
        String sansAccents = Normalizer.normalize(value.trim().toLowerCase(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        return sansAccents.replaceAll("[^a-z0-9]+", " ").trim();
    }

    private String trim(String v) {
        return v == null ? null : v.trim();
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

    private String getUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
