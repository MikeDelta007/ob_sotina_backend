package com.officedubac.project.caisseAvance;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class CaisseAvanceService {

    private final CaisseAvanceRepository caisseRepo;
    private final ApprovisionnementRepository approvisionnementRepo;
    private final MandatementRepository mandatementRepo;

    private static final BigDecimal SEUIL_ALERTE = BigDecimal.valueOf(100_000);

    // ── Récupérer la caisse courante ──
    public CaisseAvance getCaisseCourante() {
        return caisseRepo.findTopByOrderByDateCreationDesc()
                .orElseThrow(() -> new RuntimeException("Caisse non initialisée"));
    }

    // ── Approvisionnement : ajoute un montant à la caisse et historise l'opération ──
    public Approvisionnement approvisionner(BigDecimal montant, LocalDate date, String description) {
        CaisseAvance caisse = caisseRepo.findTopByOrderByDateCreationDesc().orElse(null);
        backfillSoldeInitialSiNecessaire(caisse);

        BigDecimal soldeAvant = caisse != null ? caisse.getMontant() : BigDecimal.ZERO;
        BigDecimal soldeApres = soldeAvant.add(montant);

        if (caisse == null) {
            caisseRepo.save(CaisseAvance.builder()
                    .montant(soldeApres)
                    .date(date)
                    .description(description)
                    .build());
        } else {
            mettreAJourSolde(caisse.getId(), soldeApres);
        }

        Approvisionnement approv = Approvisionnement.builder()
                .montant(montant)
                .soldeAvant(soldeAvant)
                .soldeApres(soldeApres)
                .date(date)
                .description(description)
                .creePar(getUsername())
                .build();
        return approvisionnementRepo.save(approv);
    }

    public List<Approvisionnement> getAllApprovisionnements() {
        backfillSoldeInitialSiNecessaire(caisseRepo.findTopByOrderByDateCreationDesc().orElse(null));
        return approvisionnementRepo.findAllByOrderByDateCreationDesc();
    }

    // ── S'assure qu'aucune part du solde actuel ne reste "non tracée" dans l'historique ──
    // (ex : caisse initialisée via l'ancien système, avant la mise en place des approvisionnements)
    private void backfillSoldeInitialSiNecessaire(CaisseAvance caisse) {
        if (caisse == null) return;

        BigDecimal totalApprovisionne = approvisionnementRepo.findAll().stream()
                .map(Approvisionnement::getMontant)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalDecaisseEspeces = mandatementRepo.findAll().stream()
                .filter(Mandatement::isDecaisse)
                .map(Mandatement::getMontantDecaisse)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal soldeInitialNonTrace = caisse.getMontant()
                .subtract(totalApprovisionne)
                .add(totalDecaisseEspeces);

        if (soldeInitialNonTrace.compareTo(BigDecimal.ZERO) <= 0) return;

        Approvisionnement backfill = approvisionnementRepo.save(Approvisionnement.builder()
                .montant(soldeInitialNonTrace)
                .soldeAvant(BigDecimal.ZERO)
                .soldeApres(soldeInitialNonTrace)
                .date(caisse.getDate())
                .description("Solde initial de la caisse (avant mise en place du suivi des approvisionnements)")
                .creePar("—")
                .build());

        // Positionner cette entrée avant toutes les autres dans l'historique chronologique
        LocalDateTime datePlusAncienne = approvisionnementRepo.findAll().stream()
                .map(Approvisionnement::getDateCreation)
                .filter(Objects::nonNull)
                .min(LocalDateTime::compareTo)
                .orElse(LocalDateTime.now());
        backfill.setDateCreation(datePlusAncienne.minusSeconds(1));
        approvisionnementRepo.save(backfill);

        log.info("💰 Solde initial non tracé backfillé dans l'historique des approvisionnements : {} FCFA",
                soldeInitialNonTrace);
    }

    private String getUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    // ── Mettre à jour le solde (après décaissement) ──
    public CaisseAvance mettreAJourSolde(String id, BigDecimal nouveauSolde) {
        CaisseAvance caisse = caisseRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Caisse introuvable"));
        caisse.setMontant(nouveauSolde);
        caisseRepo.save(caisse);

        if (nouveauSolde.compareTo(BigDecimal.ZERO) == 0) {
            log.warn("⚠️ Caisse d'avance épuisée — initialisation requise");
        } else if (nouveauSolde.compareTo(SEUIL_ALERTE) <= 0) {
            log.warn("⚠️ Caisse d'avance faible : {} FCFA", nouveauSolde);
        }
        return caisse;
    }

    // ── Décaisser (espèces seulement) ──
    public DecaissementResult decaisser(BigDecimal montant, Mandatement.ModePaiement mode) {
        CaisseAvance caisse = getCaisseCourante();
        BigDecimal soldeAvant = caisse.getMontant();

        if (mode == Mandatement.ModePaiement.CHEQUE) {
            // Chèque → on ne touche pas la caisse
            return DecaissementResult.builder()
                    .montantDecaisse(BigDecimal.ZERO)
                    .soldeAvant(soldeAvant)
                    .soldeApres(soldeAvant)
                    .alerte(false)
                    .caisseVide(false)
                    .build();
        }

        BigDecimal soldeApres = soldeAvant.subtract(montant);
        if (soldeApres.compareTo(BigDecimal.ZERO) < 0)
            throw new RuntimeException("Solde insuffisant dans la caisse d'avance");

        mettreAJourSolde(caisse.getId(), soldeApres);

        return DecaissementResult.builder()
                .montantDecaisse(montant)
                .soldeAvant(soldeAvant)
                .soldeApres(soldeApres)
                .alerte(soldeApres.compareTo(BigDecimal.ZERO) > 0
                        && soldeApres.compareTo(SEUIL_ALERTE) <= 0)
                .caisseVide(soldeApres.compareTo(BigDecimal.ZERO) == 0)
                .build();
    }

    public boolean estAlerte() {
        BigDecimal solde = getCaisseCourante().getMontant();
        return solde.compareTo(SEUIL_ALERTE) <= 0;
    }

    // ── Le solde actuel couvre-t-il ce montant (pour un décaissement espèces) ? ──
    public boolean soldeSuffisant(BigDecimal montant) {
        CaisseAvance caisse = caisseRepo.findTopByOrderByDateCreationDesc().orElse(null);
        BigDecimal solde = caisse != null ? caisse.getMontant() : BigDecimal.ZERO;
        return solde.compareTo(montant) >= 0;
    }

    // ── DTO résultat décaissement ──
    @lombok.Data @lombok.Builder
    public static class DecaissementResult {
        private BigDecimal montantDecaisse;
        private BigDecimal soldeAvant;
        private BigDecimal soldeApres;
        private boolean alerte;
        private boolean caisseVide;
    }
}
