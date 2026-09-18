package com.officedubac.project.banque;

import com.officedubac.project.banque.dto.BanqueAudit;
import com.officedubac.project.banque.dto.BanqueRequest;
import com.officedubac.project.banque.dto.BanqueResponse;
import com.officedubac.project.exception.BusinessResourceException;
import com.officedubac.project.exception.ResourceAlreadyExists;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
@Service
@Slf4j
@RequiredArgsConstructor
public class BanqueServiceImp implements BanqueService {
    private final BanqueMapper mapper;
    private final MongoTemplate mongoTemplate;
    private final BanqueRepository dao;
    @Override
    public List<BanqueResponse> all() throws BusinessResourceException {
        log.info("BanqueService::all");

        Query query = new Query().with(Sort.by(Sort.Order.desc("name")));  // Tri décroissant sur 'name'
        List<Banque> all = mongoTemplate.find(query, Banque.class);
        List<BanqueResponse> response = all.stream()
                .map(mapper::entiteToResponse)
                .collect(Collectors.toList());
        return response;
    }
    @Override
    public Optional<BanqueResponse> oneById(String id) throws NumberFormatException, BusinessResourceException {
        try {
            Banque one = dao.findById(id)
                    .orElseThrow(
                            () -> new BusinessResourceException("not-found", "Aucune Banque avec " + id + " trouvée.", HttpStatus.NOT_FOUND)
                    );
            log.info("Banque avec id: " + id + " trouvé. <oneById>");
            Optional<BanqueResponse> response;
            response = Optional.ofNullable(mapper.entiteToResponse(one));
            return response;
        } catch (NumberFormatException e) {
            log.warn("Paramétre id " + id + " non autorisé. <oneById>.");
            throw new BusinessResourceException("not-valid-param", "Paramétre " + id + " non autorisé.", HttpStatus.BAD_REQUEST);
        }
    }
    @Override
    @Transactional(readOnly = false)
    public BanqueResponse add(BanqueRequest req) throws BusinessResourceException {
        try {
            log.info("Debug 001-add:  " + req.toString());
            Banque one = mapper.requestToEntity(req);
            log.info("Debug 001-req_to_entity:  " + one.toString());
            BanqueResponse response = mapper.entiteToResponse(dao.save(one));
            log.info("Ajout " + response.getName()+ " effectué avec succés. <add>");
            return response;
        } catch (ResourceAlreadyExists | DataIntegrityViolationException e) {
            log.error("Erreur technique de creation Banque: donnée en doublon ou contrainte non respectée" + e.toString());
            throw new BusinessResourceException("data-error", "Donnée en doublon ou contrainte non respectée ", HttpStatus.CONFLICT);
        } catch (Exception ex) {
            log.error("Ajout Banque: Une erreur inattandue est rencontrée." + ex.getMessage());
            throw new BusinessResourceException("technical-error", "Erreur technique de création d'un Banque: " + req.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @Override
    @Transactional(readOnly = false)
    public BanqueResponse maj(BanqueRequest req, String id) throws NumberFormatException, NoSuchElementException, BusinessResourceException {
        try {
            Banque FonctionOptional = dao.findById(id)
                    .orElseThrow(
                            () -> new BusinessResourceException("not-found", "Aucun Banque avec " + id + " trouvé.", HttpStatus.NOT_FOUND)
                    );
            Banque oneBrute = mapper.requestToEntiteUp(FonctionOptional, req);
            BanqueResponse response = mapper.entiteToResponse(dao.save(oneBrute));
            log.info("Mise à jour " + response.getName() + " effectuée avec succés. <maj>");
            return response;
        } catch (NumberFormatException e) {
            log.warn("Paramétre id " + id + " non autorisé. <maj>.");
            throw new BusinessResourceException("not-valid-param", "Paramétre " + id + " non autorisé.", HttpStatus.BAD_REQUEST);
        } catch (ResourceAlreadyExists | DataIntegrityViolationException e) {
            log.error("Erreur technique de maj Banque: donnée en doublon ou contrainte non respectée" + e.toString());
            throw new BusinessResourceException("data-error", "Donnée en doublon ou contrainte non respectée ", HttpStatus.CONFLICT);
        } catch (Exception ex) {
            log.error("Maj imputation: Une erreur inattandue est rencontrée." + ex.toString());
            throw new BusinessResourceException("technical-error", "Erreur technique de mise à jour d'un Banque: " + req.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @Override
    @Transactional(readOnly = false)
    public String del(String id) throws NumberFormatException, BusinessResourceException {
        try {
            Banque oneBrute = dao.findById(id)
                    .orElseThrow(
                            () -> new BusinessResourceException("not-found", "Aucun Banque avec " + id + " trouvé.", HttpStatus.NOT_FOUND)
                    );
            dao.deleteById(id);
            log.info("Banque avec id & : " + id + " & " + oneBrute.getName() + " supprimé avec succés. <del>");
            String response;
            response = "Imputation: " + oneBrute.getName() + " supprimé avec succés. <del>";
            return response;
        } catch (NumberFormatException e) {
            log.warn("Paramétre id " + id + " non autorisé. <del>.");
            throw new BusinessResourceException("not-valid-param", "Paramétre " + id + " non autorisé.", HttpStatus.BAD_REQUEST);
        }
    }
    @Override
    public Optional<BanqueAudit> auditOneById(String id) throws NumberFormatException, BusinessResourceException {
        try {
            Banque oneBrute = dao.findById(id)
                    .orElseThrow(
                            () -> new BusinessResourceException("not-found", "Aucune Banque avec " + id + " trouvé.", HttpStatus.NOT_FOUND)
                    );
            log.info("Banque avec id: " + id + " trouvé. <auditOneById>");
           Optional<BanqueAudit> response;
            response = Optional.ofNullable(mapper.toEntiteAudit(oneBrute, Long.valueOf("1"), Long.valueOf("1") ));
            return response;
        } catch (NumberFormatException e) {
            log.warn("Paramétre id " + id + " non autorisé. <auditOneById>.");
            throw new BusinessResourceException("not-valid-param", "Paramétre " + id + " non autorisé.", HttpStatus.BAD_REQUEST);
        }

    }
    @Override
    public void verifyBanqueUnique(String name) throws BusinessResourceException {
        if(dao.findByName(name).isPresent()){
            throw new ResourceAlreadyExists("La Banque existe déjà.");
        }

    }
    @Override
    public Optional<Banque> findByLibelle(String name) throws BusinessResourceException {
        try {
            Optional<Banque> response = dao.findByName(name);
            log.info("Banque avec name: " + name + " trouvé. <BanqueBy name>");
            return response;
        } catch (Exception ex) {
            log.error("Banque by name: Une erreur inattandue est rencontrée." + ex.toString());
            throw new BusinessResourceException("not-found", "Banque avec name: " + name+ " non trouvé(e).", HttpStatus.NOT_FOUND);
        }
    }
    @Override
    public List<Banque> importExcel(InputStream inputStream) throws Exception {
        List<Banque> banques = new ArrayList<>();

        try (Workbook workbook = new XSSFWorkbook(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row != null) {
                    Banque banque = createBanqueFromRow(row);
                    if (banque != null) {
                        banques.add(banque);
                    }
                }
            }

            if (!banques.isEmpty()) {
                dao.saveAll(banques);
            }
        }

        return banques;
    }

    private Banque createBanqueFromRow(Row row) {
        try {
            String codeBanque = getCellValue(row.getCell(0));
            String name = getCellValue(row.getCell(1));
            String nomComplet = getCellValue(row.getCell(2));

            // Ne pas générer de valeurs par défaut, laisser null si vide
            // Juste trimmer les valeurs non null
            if (codeBanque != null) codeBanque = codeBanque.trim();
            if (name != null) name = name.trim();
            if (nomComplet != null) nomComplet = nomComplet.trim();

            // Accepter même si codeBanque ou name sont vides
            return Banque.builder()
                    .codeBanque(codeBanque) // peut être null ou vide
                    .name(name) // peut être null ou vide
                    .NomComplet(nomComplet) // peut être null ou vide
                    .dateCreation(LocalDateTime.now())
                    .utiCree(1L)
                    .build();

        } catch (Exception e) {
            System.err.println("Erreur ligne " + row.getRowNum() + ": " + e.getMessage());
            return null;
        }
    }

    private String getCellValue(Cell cell) {
        if (cell == null) return null;

        switch (cell.getCellType()) {
            case STRING:
                String value = cell.getStringCellValue();
                return value.isEmpty() ? null : value;
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    double num = cell.getNumericCellValue();
                    return num == Math.floor(num) ? String.valueOf((long) num) : String.valueOf(num);
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                try {
                    return cell.getStringCellValue();
                } catch (Exception e) {
                    return String.valueOf(cell.getNumericCellValue());
                }
            case BLANK:
                return null;
            default:
                return null;
        }
    }
}
