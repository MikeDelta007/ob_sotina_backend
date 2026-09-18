package com.officedubac.project.personnel;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DivisionDataInitializer implements CommandLineRunner {

    private final DivisionRepository divisionRepo;

    private static final List<String> DIVISIONS_DEFAUT = List.of(
        "Direction",
        "Division Scolarité",
        "Division Planification",
        "Division des Extrants",
        "Division Pédagogie",
        "Service Archives et Documents",
        "Service Informatique",
        "Service Comptabilité"
    );

    @Override
    public void run(String... args) {
        if (divisionRepo.count() == 0) {
            DIVISIONS_DEFAUT.forEach(libelle -> {
                divisionRepo.save(Division.builder().libelle(libelle).actif(true).build());
            });
            log.info("✅ {} divisions initialisées", DIVISIONS_DEFAUT.size());
        }
    }
}
