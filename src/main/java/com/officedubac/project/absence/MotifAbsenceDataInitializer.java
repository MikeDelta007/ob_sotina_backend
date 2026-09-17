package com.officedubac.project.absence;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class MotifAbsenceDataInitializer implements CommandLineRunner {

    private final MotifAbsenceRepository motifAbsenceRepo;

    private static final List<String> MOTIFS_DEFAUT = List.of(
        "Baptême",
        "Fêtes religieuses",
        "Mariage",
        "Décès",
        "Séminaire",
        "Conférence",
        "Formation"
    );

    @Override
    public void run(String... args) {
        if (motifAbsenceRepo.count() == 0) {
            MOTIFS_DEFAUT.forEach(libelle ->
                motifAbsenceRepo.save(MotifAbsence.builder().libelle(libelle).actif(true).build())
            );
            log.info("✅ {} motifs d'absence initialisés", MOTIFS_DEFAUT.size());
        }
    }
}
