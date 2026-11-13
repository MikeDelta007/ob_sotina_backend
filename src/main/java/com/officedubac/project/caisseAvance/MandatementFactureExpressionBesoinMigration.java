package com.officedubac.project.caisseAvance;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

// Migration au démarrage : pour un mandatement SIMPLE, expressionBesoinId n'était historiquement
// renseigné que sur le Mandatement lui-même, jamais sur sa facture embarquée (FactureEmbedded) —
// contrairement à un cumulatif, où chaque ligne le porte déjà sur sa propre facture. Les écrans
// qui lisent ce champ au niveau de la facture (ex. le téléchargement de la décharge depuis la
// liste des mandatements) ne le voyaient donc jamais pour un simple. MandatementService renseigne
// désormais les deux à la création ; on reporte ici la valeur existante sur les mandatements déjà
// en base — idempotent, sans effet une fois tous migrés.
@Slf4j
@Component
@Order(50)
@RequiredArgsConstructor
public class MandatementFactureExpressionBesoinMigration implements CommandLineRunner {

    private final MandatementRepository mandatementRepo;

    @Override
    public void run(String... args) {
        int corriges = 0;
        for (Mandatement m : mandatementRepo.findAll()) {
            if (m.getType() != Mandatement.TypeMandatement.SIMPLE) continue;
            if (m.getExpressionBesoinId() == null || m.getExpressionBesoinId().isBlank()) continue;
            if (m.getFactures() == null || m.getFactures().isEmpty()) continue;

            Mandatement.FactureEmbedded facture = m.getFactures().get(0);
            if (facture.getExpressionBesoinId() != null) continue;

            facture.setExpressionBesoinId(m.getExpressionBesoinId());
            mandatementRepo.save(m);
            corriges++;
        }
        if (corriges > 0) {
            log.info("Mandatements simples : expressionBesoinId reporté sur la facture embarquée pour {} mandatement(s) au démarrage", corriges);
        }
    }
}
