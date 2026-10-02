package com.officedubac.project.ticketCarburant;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class MotifCarburantDataInitializer implements CommandLineRunner {

    private final MotifCarburantRepository motifRepo;

    private static final List<String> MOTIFS_DEFAUT = List.of(
        "Mission",
        "Remise de diplômes",
        "Supervision des examens",
        "Convoyage de documents",
        "Transport de matériel",
        "Réunion de travail"
    );

    @Override
    public void run(String... args) {
        if (motifRepo.count() == 0) {
            MOTIFS_DEFAUT.forEach(libelle ->
                    motifRepo.save(MotifCarburant.builder().libelle(libelle).actif(true).build()));
            log.info("✅ {} motifs de ticket carburant initialisés", MOTIFS_DEFAUT.size());
        }
    }
}
