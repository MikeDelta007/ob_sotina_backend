package com.officedubac.project.services;

import com.officedubac.project.dto.CandidatDTO;
import com.officedubac.project.dto.CandidatDecisionDTO;
import com.officedubac.project.models.AuditLog;
import com.officedubac.project.models.Candidat;
import com.officedubac.project.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuditService {

    @Autowired
    private final AuditLogRepository auditRepo;

    public void logOperation(String entityName,
                             String entityId,
                             String operationType,
                             Map<String, Object> oldValues,
                             String login,
                             String ipAddress) {
        AuditLog log = new AuditLog();
        log.setNatureOperation(entityName);
        log.setIdCandidate(entityId);
        log.setOperationType(operationType);
        log.setFieldValues(oldValues);
        log.setLogin(login);
        log.setIpAddress(ipAddress);

        auditRepo.save(log);
    }

    public List<AuditLog> getLogsByCandidateId(String candidateId) {
        return auditRepo.findByIdCandidate(candidateId);
    }

    public Map<String, Object> getDifferences(Candidat oldC, CandidatDecisionDTO newC) {
        Map<String, Object> differences = new LinkedHashMap<>();

        String dateNaissance = oldC.getDate_birth()
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));


        if (!Objects.equals(oldC.getDosNumber(), newC.getDosNumber())) {
            differences.put("N° de dossier", Map.of("ancienne-donnée", oldC.getDosNumber(), "nouvelle-donnée", newC.getDosNumber()));
        }
        if (!Objects.equals(oldC.getFirstname(), newC.getFirstname())) {
            differences.put("Prénom (s)", Map.of("ancienne-donnée", oldC.getFirstname(), "nouvelle-donnée", newC.getFirstname()));
        }
        if (!Objects.equals(oldC.getLastname(), newC.getLastname())) {
            differences.put("Nom", Map.of("ancienne-donnée", oldC.getLastname(), "nouvelle-donnée", newC.getLastname()));
        }
        if (!Objects.equals(dateNaissance, newC.getDate_birth())) {
            differences.put("Date de naissance", Map.of("ancienne-donnée", dateNaissance, "nouvelle-donnée", newC.getDate_birth()));
        }
        if (!Objects.equals(oldC.getPlace_birth(), newC.getPlace_birth())) {
            differences.put("Lieu de naissance", Map.of("ancienne-donnée", oldC.getPlace_birth(), "nouvelle-donnée", newC.getPlace_birth()));
        }
        if (!Objects.equals(oldC.getGender(), newC.getGender())) {
            differences.put("Sexe", Map.of("ancienne-donnée", oldC.getGender(), "nouvelle-donnée", newC.getGender()));
        }
        if (!Objects.equals(oldC.getPhone1(), newC.getPhone1())) {
            differences.put("Téléphone 1", Map.of("ancienne-donnée", oldC.getPhone1(), "nouvelle-donnée", newC.getPhone1()));
        }
        if (!Objects.equals(oldC.getPhone2(), newC.getPhone2())) {
            differences.put("Téléphone 2", Map.of("ancienne-donnée", oldC.getPhone2(), "nouvelle-donnée", newC.getPhone2()));
        }
        if (!Objects.equals(oldC.getEmail(), newC.getEmail())) {
            differences.put("Email", Map.of("ancienne-donnée", oldC.getEmail(), "nouvelle-donnée", newC.getEmail()));
        }
        if (!Objects.equals(oldC.getYear_registry_num(), newC.getYear_registry_num())) {
            differences.put("Année du registre", Map.of("ancienne-donnée", oldC.getYear_registry_num(), "nouvelle-donnée", newC.getYear_registry_num()));
        }
        if (!Objects.equals(oldC.getRegistry_num(), newC.getRegistry_num())) {
            differences.put("Numéro du registre", Map.of("ancienne-donnée", oldC.getRegistry_num(), "nouvelle-donnée", newC.getRegistry_num()));
        }
        if (!Objects.equals(oldC.getBac_do_count(), newC.getBac_do_count())) {
            differences.put("Nombre de BAC déjà passé", Map.of("ancienne-donnée", oldC.getBac_do_count(), "nouvelle-donnée", newC.getBac_do_count()));
        }
        if (!Objects.equals(oldC.getOrigine_bfem(), newC.getOrigine_bfem())) {
            differences.put("Origine BFEM", Map.of("ancienne-donnée", oldC.getOrigine_bfem(), "nouvelle-donnée", newC.getOrigine_bfem()));
        }
        if (!Objects.equals(oldC.getYear_bfem(), newC.getYear_bfem())) {
            differences.put("Année BFEM", Map.of("ancienne-donnée", oldC.getYear_bfem(), "nouvelle-donnée", newC.getYear_bfem()));
        }
        if (!Objects.equals(oldC.getSubject(), newC.getSubject())) {
            differences.put("Sujet", Map.of("ancienne-donnée", oldC.getSubject(), "nouvelle-donnée", newC.getSubject()));
        }
        if (!Objects.equals(oldC.isHandicap(), newC.isHandicap())) {
            differences.put("Handicap", Map.of("ancienne-donnée", oldC.isHandicap(), "nouvelle-donnée", newC.isHandicap()));
        }
        if (!Objects.equals(oldC.getType_handicap(), newC.getType_handicap())) {
            differences.put("Type de handicap", Map.of("ancienne-donnée", oldC.getType_handicap(), "nouvelle-donnée", newC.getType_handicap()));
        }
        if (!Objects.equals(oldC.getEps(), newC.getEps())) {
            differences.put("EPS", Map.of("ancienne-donnée", oldC.getEps(), "nouvelle-donnée", newC.getEps()));
        }
        if (!Objects.equals(oldC.isCdt_is_cgs(), newC.isCdt_is_cgs())) {
            differences.put("Candidat CGS ?", Map.of("ancienne-donnée", oldC.isCdt_is_cgs(), "nouvelle-donnée", newC.isCdt_is_cgs()));
        }
        if (!Objects.equals(oldC.getDecision(), newC.getDecision())) {
            differences.put("Décision", Map.of("ancienne-donnée", oldC.getDecision(), "nouvelle-donnée", newC.getDecision()));
        }

        // Objets complexes → soit tu compares leur toString(), soit tu descends dans leurs champs
        if (!Objects.equals(oldC.getMatiere1(), newC.getMatiere1())) {
            differences.put("Matière 1", Map.ofEntries(
                    Map.entry("ancienne-donnée", Objects.toString(oldC.getMatiere1(), "null")),
                    Map.entry("nouvelle-donnée", Objects.toString(newC.getMatiere1(), "null"))
            ));
        }

        if (!Objects.equals(oldC.getMatiere2(), newC.getMatiere2())) {
            differences.put("Matière 2", Map.ofEntries(
                    Map.entry("ancienne-donnée", Objects.toString(oldC.getMatiere2(), "null")),
                    Map.entry("nouvelle-donnée", Objects.toString(newC.getMatiere2(), "null"))
            ));
        }

        if (!Objects.equals(oldC.getMatiere3(), newC.getMatiere3())) {
            differences.put("Matière 3", Map.ofEntries(
                    Map.entry("ancienne-donnée", Objects.toString(oldC.getMatiere3(), "null")),
                    Map.entry("nouvelle-donnée", Objects.toString(newC.getMatiere3(), "null"))
            ));
        }

        if (!Objects.equals(oldC.getEprFacListA(), newC.getEprFacListA())) {
            differences.put("Épreuve facultative Liste A", Map.ofEntries(
                    Map.entry("ancienne-donnée", Objects.toString(oldC.getEprFacListA(), "null")),
                    Map.entry("nouvelle-donnée", Objects.toString(newC.getEprFacListA(), "null"))
            ));
        }

        if (!Objects.equals(oldC.getEprFacListA(), newC.getEprFacListA())) {
            differences.put("Épreuve facultative Liste B", Map.ofEntries(
                    Map.entry("ancienne-donnée", Objects.toString(oldC.getEprFacListB(), "null")),
                    Map.entry("nouvelle-donnée", Objects.toString(newC.getEprFacListB(), "null"))
            ));
        }

        if (!Objects.equals(oldC.getCentreEtatCivil(), newC.getCentreEtatCivil())) {
            differences.put("Centre état civil", Map.of("ancienne-donnée", oldC.getCentreEtatCivil(), "nouvelle-donnée", newC.getCentreEtatCivil()));
        }
        if (!Objects.equals(oldC.getSerie(), newC.getSerie())) {
            differences.put("Série", Map.of("ancienne-donnée", oldC.getSerie(), "nouvelle-donnée", newC.getSerie()));
        }
        if (!Objects.equals(oldC.getNationality(), newC.getNationality())) {
            differences.put("Nationalité", Map.of("ancienne-donnée", oldC.getNationality(), "nouvelle-donnée", newC.getNationality()));
        }
        if (!Objects.equals(oldC.getCountryBirth(), newC.getCountryBirth())) {
            differences.put("Pays de naissance", Map.of("ancienne-donnée", oldC.getCountryBirth(), "nouvelle-donnée", newC.getCountryBirth()));
        }
        if (!Objects.equals(oldC.getConcoursGeneral(), newC.getConcoursGeneral())) {
            differences.put("Concours Général", Map.of("ancienne-donnée", oldC.getConcoursGeneral(), "nouvelle-donnée", newC.getConcoursGeneral()));
        }

        return differences;
    }

}
