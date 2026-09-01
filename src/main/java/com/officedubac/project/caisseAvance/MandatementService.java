package com.officedubac.project.caisseAvance;

import com.officedubac.project.expressionBesoin.ExpressionBesoinService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
@RequiredArgsConstructor
public class MandatementService {

    private final MandatementRepository    mandatementRepo;
    private final CaisseAvanceService      caisseService;
    private final GridFsTemplate           gridFsTemplate;
    private final ExpressionBesoinService  expressionBesoinService;

    private static final BigDecimal SEUIL_CHEQUE = BigDecimal.valueOf(100_000);

    // ═══════════════════════════════════════════════════════════════
    // MANDATEMENT SIMPLE — une seule facture
    // ═══════════════════════════════════════════════════════════════
    public Mandatement mandatementSimple(
            MandatementSimpleRequest req,
            MultipartFile pdfFacture,
            MultipartFile pdfCheque,
            MultipartFile pdfCni) {

        String username = getUsername();
        BigDecimal montant = req.getMontant();

        BigDecimal avance   = req.getTypePaiement() == Mandatement.TypePaiement.AVANCE
                              ? req.getMontantAvance() : montant;
        BigDecimal reliquat = montant.subtract(avance);

        // Le mode est déterminé par le montant réellement décaissé maintenant (l'avance),
        // pas par le montant total de la facture — une avance de 3 000 sur une facture
        // de 130 000 se paie en espèces, pas par chèque.
        Mandatement.ModePaiement mode = modeAuto(avance);

        // Décaissement
        CaisseAvanceService.DecaissementResult decaissement =
                caisseService.decaisser(avance, mode);

        // Numéro facture — séquence atomique par utilisateur/année, garantit l'unicité
        String numero = genererNumeroUnique(username);

        // Facture embedded
        Mandatement.FactureEmbedded facture = Mandatement.FactureEmbedded.builder()
                .numero(numero)
                .date(LocalDateTime.now())
                .montant(montant)
                .motifId(req.getMotifId())
                .motifLibelle(req.getMotifLibelle())
                .urlPdfFacture(saveFile(pdfFacture, "facture"))
                .urlPdfCheque(saveFile(pdfCheque,  "cheque"))
                .urlPdfCni(saveFile(pdfCni,         "cni"))
                .build();

        Mandatement mandatement = Mandatement.builder()
                .type(Mandatement.TypeMandatement.SIMPLE)
                .typePaiement(req.getTypePaiement())
                .montantTotal(montant)
                .montantAvance(avance)
                .montantReliquat(reliquat)
                .modePaiement(mode)
                .decaisse(true)
                .montantDecaisse(decaissement.getMontantDecaisse())
                .soldeAvant(decaissement.getSoldeAvant())
                .soldeApres(decaissement.getSoldeApres())
                .factures(List.of(facture))
                .description(req.getDescription())
                .beneficiaire(req.getBeneficiaire())
                .numeroCni(req.getNumeroCni())
                .numeroCheque(req.getNumeroCheque())
                .expressionBesoinId(req.getExpressionBesoinId())
                .creePar(username)
                .build();

        Mandatement saved = mandatementRepo.save(mandatement);

        // Si ce mandatement provient d'une expression de besoin traitée, la marquer
        // comme consommée pour qu'elle sorte de la liste déroulante de création.
        if (req.getExpressionBesoinId() != null && !req.getExpressionBesoinId().isBlank())
            expressionBesoinService.marquerUtilisee(req.getExpressionBesoinId(), saved.getId());

        logResultat(saved, decaissement);
        return saved;
    }

    // ═══════════════════════════════════════════════════════════════
    // MANDATEMENT CUMULATIF — ensemble de paires (facture simple)
    // ═══════════════════════════════════════════════════════════════
    public Mandatement mandatementCumulatif(
            MandatementCumulatifRequest req,
            List<MultipartFile> pdfs,
            List<MultipartFile> cheques,
            List<MultipartFile> cnis) {

        String username = getUsername();

        // Total global
        BigDecimal total = req.getLignes().stream()
                .map(MandatementCumulatifRequest.Ligne::getMontant)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Le total d'un mandatement cumulatif (ensemble de petites factures) ne peut pas
        // dépasser le solde de la caisse, ni le seuil chèque (100 000) — un cumulatif
        // regroupe de petites factures destinées à un paiement en espèces.
        if (total.compareTo(SEUIL_CHEQUE) > 0)
            throw new RuntimeException("Le total du mandatement cumulatif (" + total
                    + ") dépasse 100 000 FCFA. Utilisez un mandatement simple pour un montant plus élevé.");

        if (!caisseService.soldeSuffisant(total))
            throw new RuntimeException("Le total du mandatement cumulatif (" + total
                    + ") dépasse le solde de la caisse. Approvisionnez la caisse avant de continuer.");

        BigDecimal avanceGlobale = req.getTypePaiement() == Mandatement.TypePaiement.AVANCE
                ? req.getMontantAvanceGlobal() : total;
        BigDecimal reliquatGlobal = total.subtract(avanceGlobale);

        // Le mode est déterminé par le montant réellement décaissé maintenant (l'avance globale),
        // pas par le total des factures.
        Mandatement.ModePaiement mode = modeAuto(avanceGlobale);

        // Un seul décaissement global
        CaisseAvanceService.DecaissementResult decaissement =
                caisseService.decaisser(avanceGlobale, mode);

        // Construire les factures embedded
        List<Mandatement.FactureEmbedded> factures = new ArrayList<>();

        for (int i = 0; i < req.getLignes().size(); i++) {
            MandatementCumulatifRequest.Ligne ligne = req.getLignes().get(i);
            String numero = genererNumeroUnique(username);

            // Avance proportionnelle par facture si mode AVANCE
            BigDecimal avanceLigne   = avanceGlobale;
            BigDecimal reliquatLigne = reliquatGlobal;
            if (req.getLignes().size() > 1 && req.getTypePaiement() == Mandatement.TypePaiement.AVANCE) {
                BigDecimal ratio = ligne.getMontant()
                        .divide(total, 6, RoundingMode.HALF_UP);
                avanceLigne   = avanceGlobale.multiply(ratio).setScale(0, RoundingMode.HALF_UP);
                reliquatLigne = ligne.getMontant().subtract(avanceLigne);
            }

            factures.add(Mandatement.FactureEmbedded.builder()
                    .numero(numero)
                    .date(LocalDateTime.now())
                    .montant(ligne.getMontant())
                    .motifId(ligne.getMotifId())
                    .motifLibelle(ligne.getMotifLibelle())
                    .urlPdfFacture(saveFile(pdfs    != null && i < pdfs.size()    ? pdfs.get(i)    : null, "facture"))
                    .urlPdfCheque(saveFile(cheques  != null && i < cheques.size() ? cheques.get(i) : null, "cheque"))
                    .urlPdfCni(saveFile(cnis        != null && i < cnis.size()    ? cnis.get(i)    : null, "cni"))
                    .build());
        }

        Mandatement mandatement = Mandatement.builder()
                .type(Mandatement.TypeMandatement.CUMULATIF)
                .typePaiement(req.getTypePaiement())
                .montantTotal(total)
                .montantAvance(avanceGlobale)
                .montantReliquat(reliquatGlobal)
                .modePaiement(mode)
                .decaisse(true)
                .montantDecaisse(decaissement.getMontantDecaisse())
                .soldeAvant(decaissement.getSoldeAvant())
                .soldeApres(decaissement.getSoldeApres())
                .factures(factures)
                .description(req.getDescription())
                .beneficiaire(req.getBeneficiaire())
                .numeroCni(req.getNumeroCni())
                .numeroCheque(req.getNumeroCheque())
                .expressionBesoinId(req.getExpressionBesoinId())
                .creePar(username)
                .build();

        Mandatement saved = mandatementRepo.save(mandatement);

        // Si ce mandatement provient d'une expression de besoin traitée, la marquer
        // comme consommée pour qu'elle sorte de la liste déroulante de création.
        if (req.getExpressionBesoinId() != null && !req.getExpressionBesoinId().isBlank())
            expressionBesoinService.marquerUtilisee(req.getExpressionBesoinId(), saved.getId());

        logResultat(saved, decaissement);
        return saved;
    }

    public List<Mandatement> getAll(Integer annee, Integer mois, Integer semaine) {
        return mandatementRepo.findAllByOrderByDateCreationDesc().stream()
                .filter(m -> PeriodeUtil.matchPeriode(
                        m.getDateCreation() != null ? m.getDateCreation().toLocalDate() : null,
                        annee, mois, semaine))
                .collect(java.util.stream.Collectors.toList());
    }

    // ═══════════════════════════════════════════════════════════════
    // PAIEMENT DU RELIQUAT (mandatements en mode AVANCE)
    // ═══════════════════════════════════════════════════════════════
    public Mandatement payerReliquat(String id, MultipartFile pdfCheque, MultipartFile pdfCni) {
        Mandatement m = mandatementRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Mandatement introuvable : " + id));

        if (m.getMontantReliquat() == null || m.getMontantReliquat().compareTo(BigDecimal.ZERO) <= 0)
            throw new RuntimeException("Aucun reliquat à payer pour ce mandatement");
        if (m.isReliquatPaye())
            throw new RuntimeException("Le reliquat de ce mandatement est déjà payé");

        // Le reliquat peut lui aussi être réglé en espèces ou par chèque, selon son
        // propre montant et le solde de la caisse au moment du paiement.
        Mandatement.ModePaiement mode = modeAuto(m.getMontantReliquat());

        if (mode == Mandatement.ModePaiement.CHEQUE
                && ((pdfCheque == null || pdfCheque.isEmpty()) || (pdfCni == null || pdfCni.isEmpty())))
            throw new RuntimeException("Le paiement du reliquat par chèque nécessite le chèque et la CNI en pièces jointes");

        CaisseAvanceService.DecaissementResult decaissement =
                caisseService.decaisser(m.getMontantReliquat(), mode);

        m.setReliquatPaye(true);
        m.setDateReliquatPaye(LocalDateTime.now());
        m.setModePaiementReliquat(mode);
        if (mode == Mandatement.ModePaiement.CHEQUE) {
            m.setUrlPdfChequeReliquat(saveFile(pdfCheque, "cheque-reliquat"));
            m.setUrlPdfCniReliquat(saveFile(pdfCni, "cni-reliquat"));
        }

        Mandatement saved = mandatementRepo.save(m);
        log.info("✅ Reliquat payé — mandatement {} — montant={} mode={} solde après={}",
                saved.getId(), saved.getMontantReliquat(), mode, decaissement.getSoldeApres());
        return saved;
    }

    // ── Détermine le mode selon le montant, avec repli automatique sur chèque
    //    si la caisse ne peut pas couvrir un décaissement espèces ──
    private Mandatement.ModePaiement modeAuto(BigDecimal montant) {
        if (montant.compareTo(SEUIL_CHEQUE) > 0) return Mandatement.ModePaiement.CHEQUE;
        return caisseService.soldeSuffisant(montant)
                ? Mandatement.ModePaiement.ESPECES
                : Mandatement.ModePaiement.CHEQUE;
    }

    // ── Numéro : 2026-N-FAC_483920_username ──
    private String genererNumero(String username, int index) {
        return Year.now().getValue() + "-N-FAC_"
                + String.format("%06d", index) + "_"
                + username.toLowerCase().replaceAll("\\s+", "_");
    }

    // ── Génère un numéro à partir d'un index aléatoire (un seul pool global, pas de
    //    compteur par utilisateur/année à maintenir), et vérifie son unicité réelle en
    //    base avant de l'accepter — auto-réparateur en cas de collision. ──
    private String genererNumeroUnique(String username) {
        String numero;
        int tentatives = 0;
        do {
            numero = genererNumero(username, ThreadLocalRandom.current().nextInt(1, 1_000_000));
            if (++tentatives > 10000)
                throw new RuntimeException("Impossible de générer un numéro de facture unique");
        } while (mandatementRepo.existsByFacturesNumero(numero));
        return numero;
    }

    // ── Sauvegarde fichier sur GridFS (mêmes infra que FileController) ──
    private String saveFile(MultipartFile file, String type) {
        if (file == null || file.isEmpty()) return null;
        try {
            ObjectId id = gridFsTemplate.store(
                    file.getInputStream(), file.getOriginalFilename(), file.getContentType());
            log.info("📄 Upload {} : {}", type, id.toHexString());
            return "/api/v1/files/view/" + id.toHexString();
        } catch (IOException e) {
            throw new RuntimeException("Erreur upload fichier " + type, e);
        }
    }

    private void logResultat(Mandatement m, CaisseAvanceService.DecaissementResult d) {
        log.info("✅ Mandatement {} [{}] — total={} mode={} solde après={}",
                m.getType(), m.getId(), m.getMontantTotal(), m.getModePaiement(), d.getSoldeApres());
        if (d.isCaisseVide())  log.warn("🔴 Caisse vide après décaissement");
        else if (d.isAlerte()) log.warn("🟡 Caisse faible : {}", d.getSoldeApres());
    }

    private String getUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
