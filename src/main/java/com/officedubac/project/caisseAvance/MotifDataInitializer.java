package com.officedubac.project.caisseAvance;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class MotifDataInitializer implements CommandLineRunner {

    private final MotifRepository motifRepo;

    private static final List<String> MOTIFS_DEFAUT = List.of(
        "Payage autoroute",
        "Crédit téléphonique",
        "Pause café",
        "Achat eau",
        "Repas",
        "Plomberie",
        "Menuisier métallique",
        "Produit d'entretien"
    );

    @Override
    public void run(String... args) {
        if (motifRepo.count() == 0) {
            MOTIFS_DEFAUT.forEach(libelle -> {
                motifRepo.save(Motif.builder().libelle(libelle).actif(true).build());
            });
            log.info("✅ {} motifs caisse d'avance initialisés", MOTIFS_DEFAUT.size());
        }
    }
}
