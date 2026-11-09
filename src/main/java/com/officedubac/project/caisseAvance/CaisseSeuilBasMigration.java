package com.officedubac.project.caisseAvance;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

// Migration au démarrage : CaisseAvanceService.mettreAJourSolde() positionne désormais
// dateDepuisSeuilBas en temps réel à chaque mouvement de caisse, mais la caisse existante (créée
// avant ce champ) n'a jamais eu l'occasion de le poser. On reconstruit ici, une seule fois, la
// date de la dernière descente continue sous le seuil d'alerte à partir de l'historique des
// approvisionnements et décaissements déjà enregistrés — idempotent (ne fait rien si déjà posé
// ou si la caisse n'est pas actuellement sous le seuil).
@Slf4j
@Component
@Order(30)
@RequiredArgsConstructor
public class CaisseSeuilBasMigration implements CommandLineRunner {

    private final CaisseAvanceRepository caisseRepo;
    private final ApprovisionnementRepository approvisionnementRepo;
    private final MandatementRepository mandatementRepo;

    private static final BigDecimal SEUIL_ALERTE = BigDecimal.valueOf(100_000);

    private record Mouvement(LocalDateTime date, BigDecimal soldeApres) {}

    @Override
    public void run(String... args) {
        CaisseAvance caisse = caisseRepo.findTopByOrderByDateCreationDesc().orElse(null);
        if (caisse == null || caisse.getDateDepuisSeuilBas() != null) return;
        if (caisse.getMontant().compareTo(SEUIL_ALERTE) > 0) return;

        List<Mouvement> mouvements = new ArrayList<>();
        approvisionnementRepo.findAll().forEach(a -> {
            if (a.getSoldeApres() != null && a.getDateCreation() != null)
                mouvements.add(new Mouvement(a.getDateCreation(), a.getSoldeApres()));
        });
        mandatementRepo.findAll().forEach(m -> {
            if (m.isDecaisse() && m.getSoldeApres() != null && m.getDateCreation() != null)
                mouvements.add(new Mouvement(m.getDateCreation(), m.getSoldeApres()));
        });
        mouvements.sort(Comparator.comparing(Mouvement::date));

        LocalDateTime depuis = null;
        for (Mouvement mvt : mouvements) {
            if (mvt.soldeApres().compareTo(SEUIL_ALERTE) > 0) {
                depuis = null;
            } else if (depuis == null) {
                depuis = mvt.date();
            }
        }
        // Aucun mouvement historisé (caisse très ancienne) : on retient sa date de création
        // comme meilleure estimation disponible plutôt que de laisser le champ vide.
        if (depuis == null) depuis = caisse.getDateCreation();

        caisse.setDateDepuisSeuilBas(depuis);
        caisseRepo.save(caisse);
        log.info("🔄 Caisse d'avance : seuil d'alerte franchi depuis le {} (reconstruction au démarrage)", depuis);
    }
}
