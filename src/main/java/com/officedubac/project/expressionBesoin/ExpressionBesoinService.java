package com.officedubac.project.expressionBesoin;

import com.officedubac.project.caisseAvance.CaisseAvanceService;
import com.officedubac.project.models.Role;
import com.officedubac.project.models.User;
import com.officedubac.project.notification.WhatsAppService;
import com.officedubac.project.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExpressionBesoinService {

    private final ExpressionBesoinRepository expressionBesoinRepo;
    private final CaisseAvanceService        caisseService;
    private final GridFsTemplate             gridFsTemplate;
    private final UserRepository             userRepository;
    private final WhatsAppService            whatsAppService;

    // Au-delà de ce montant, la validation du Directeur est requise en plus de celle du CSA
    private static final BigDecimal SEUIL_VALIDATION_DIRECTEUR = BigDecimal.valueOf(20_000);

    // ═══════════════════════════════════════════════════════════════
    // CRÉATION / MODIFICATION (chef de service)
    // ═══════════════════════════════════════════════════════════════
    // La déclaration sur l'honneur n'est plus proposée. La facture proforma reste
    // optionnelle (case à cocher) : si le demandeur ne l'a pas, aucun fichier n'est
    // requis — il n'y a plus d'alternative à charger.
    public ExpressionBesoin creer(ExpressionBesoinRequest req, MultipartFile pdfFactureProforma) {
        boolean aProforma = Boolean.TRUE.equals(req.getAFacturePreformat());
        if (aProforma) validerPieceJointe(pdfFactureProforma);

        List<ExpressionBesoin.Ligne> lignes = construireLignes(req.getLignes());

        ExpressionBesoin eb = ExpressionBesoin.builder()
                .lignes(lignes)
                .montantInitial(totalLignes(lignes))
                .aFacturePreformat(aProforma)
                .urlPdfFactureProforma(aProforma ? saveFile(pdfFactureProforma, "facture-proforma") : null)
                .statut(ExpressionBesoin.Statut.EN_ATTENTE)
                .creePar(getUsername())
                .build();

        ExpressionBesoin saved = expressionBesoinRepo.save(eb);
        notifierValidateurs(saved);
        return saved;
    }

    public ExpressionBesoin modifier(String id, ExpressionBesoinRequest req, MultipartFile pdfFactureProforma) {
        ExpressionBesoin eb = getById(id);

        if (!eb.getCreePar().equals(getUsername()))
            throw new RuntimeException("Vous ne pouvez modifier que vos propres expressions de besoin");
        if (eb.getStatut() != ExpressionBesoin.Statut.EN_ATTENTE)
            throw new RuntimeException("Cette expression de besoin ne peut plus être modifiée");

        boolean aProforma = Boolean.TRUE.equals(req.getAFacturePreformat());
        boolean nouveauFichier = pdfFactureProforma != null && !pdfFactureProforma.isEmpty();
        if (aProforma && !nouveauFichier && eb.getUrlPdfFactureProforma() == null)
            validerPieceJointe(pdfFactureProforma);

        List<ExpressionBesoin.Ligne> lignes = construireLignes(req.getLignes());
        eb.setLignes(lignes);
        eb.setMontantInitial(totalLignes(lignes));
        eb.setAFacturePreformat(aProforma);
        if (aProforma) {
            if (nouveauFichier) eb.setUrlPdfFactureProforma(saveFile(pdfFactureProforma, "facture-proforma"));
        } else {
            eb.setUrlPdfFactureProforma(null);
        }
        eb.setUrlPdfDeclarationHonneur(null);

        return expressionBesoinRepo.save(eb);
    }

    private List<ExpressionBesoin.Ligne> construireLignes(List<ExpressionBesoinRequest.LigneRequest> req) {
        return req.stream().map(l -> {
            BigDecimal montant = l.getQuantite() != null
                    ? l.getPrixUnitaire().multiply(BigDecimal.valueOf(l.getQuantite()))
                    : l.getPrixUnitaire();
            return ExpressionBesoin.Ligne.builder()
                    .motifId(l.getMotifId())
                    .motifLibelle(l.getMotifLibelle())
                    .quantite(l.getQuantite())
                    .prixUnitaire(l.getPrixUnitaire())
                    .montant(montant)
                    .build();
        }).toList();
    }

    private BigDecimal totalLignes(List<ExpressionBesoin.Ligne> lignes) {
        return lignes.stream().map(ExpressionBesoin.Ligne::getMontant)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private void validerPieceJointe(MultipartFile pdfFactureProforma) {
        if (pdfFactureProforma == null || pdfFactureProforma.isEmpty())
            throw new RuntimeException("La facture proforma (PDF) est requise");
    }

    // Notifie systématiquement le(s) CSA, et en plus le(s) Directeur si le montant
    // initial dépasse le seuil imposant sa validation.
    private void notifierValidateurs(ExpressionBesoin eb) {
        userRepository.findByProfilName(Role.CSA)
                .forEach(u -> whatsAppService.envoyerNotificationValidation(u.getPhone()));

        if (eb.getMontantInitial().compareTo(SEUIL_VALIDATION_DIRECTEUR) > 0) {
            userRepository.findByProfilName(Role.DIRECTEUR)
                    .forEach(u -> whatsAppService.envoyerNotificationValidation(u.getPhone()));
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // VALIDATION (CSA / Directeur / Admin)
    // ═══════════════════════════════════════════════════════════════
    public ExpressionBesoin valider(String id, ValiderRequest req) {
        ExpressionBesoin eb = getById(id);

        if (eb.getStatut() != ExpressionBesoin.Statut.EN_ATTENTE)
            throw new RuntimeException("Cette expression de besoin n'est plus en attente de validation");

        // Le montant initial ne doit jamais dépasser le solde disponible de la caisse
        if (!caisseService.soldeSuffisant(eb.getMontantInitial()))
            throw new RuntimeException("Le montant initial (" + eb.getMontantInitial()
                    + ") dépasse le solde de la caisse. Validation impossible tant que la caisse n'est pas approvisionnée.");

        String username = getUsername();
        boolean estCsa = hasAuthority("CSA");
        boolean estDirecteur = hasAuthority("DIRECTEUR");
        if (!estCsa && !estDirecteur)
            throw new RuntimeException("Rôle non autorisé à valider une expression de besoin");

        // Toute ligne avec une quantité demandée doit recevoir une quantité accordée de la
        // part de CE validateur (CSA et Directeur renseignent chacun la leur séparément).
        List<Integer> quantitesAccordees = req.getQuantitesAccordees();
        for (int i = 0; i < eb.getLignes().size(); i++) {
            ExpressionBesoin.Ligne ligne = eb.getLignes().get(i);
            if (ligne.getQuantite() == null) continue;
            Integer accordee = quantitesAccordees != null && i < quantitesAccordees.size()
                    ? quantitesAccordees.get(i) : null;
            if (accordee == null)
                throw new RuntimeException("La quantité accordée est requise pour la ligne \""
                        + ligne.getMotifLibelle() + "\"");
            if (estCsa) ligne.setQuantiteAccordeeCsa(accordee);
            if (estDirecteur) ligne.setQuantiteAccordeeDirecteur(accordee);
        }
        recalculerMontants(eb);

        if (estCsa) {
            eb.setValidationCsa(true);
            eb.setValidateurCsa(username);
            eb.setDateValidationCsa(LocalDateTime.now());
        }
        if (estDirecteur) {
            eb.setValidationDirecteur(true);
            eb.setValidateurDirecteur(username);
            eb.setDateValidationDirecteur(LocalDateTime.now());
        }

        boolean directeurRequis = eb.getMontantInitial().compareTo(SEUIL_VALIDATION_DIRECTEUR) > 0;
        if (eb.isValidationCsa() && (!directeurRequis || eb.isValidationDirecteur())) {
            eb.setStatut(ExpressionBesoin.Statut.VALIDEE);
        }

        return expressionBesoinRepo.save(eb);
    }

    // Recalcule le montant de chaque ligne selon la quantité effective (celle du Directeur
    // si renseignée, sinon celle du CSA, sinon la quantité initiale demandée), puis le
    // montant initial global — tout changement de prix ou de quantité doit s'y répercuter.
    private void recalculerMontants(ExpressionBesoin eb) {
        for (ExpressionBesoin.Ligne ligne : eb.getLignes()) {
            if (ligne.getQuantite() == null) {
                ligne.setMontant(ligne.getPrixUnitaire());
                continue;
            }
            Integer quantiteEffective = ligne.getQuantiteAccordeeDirecteur() != null
                    ? ligne.getQuantiteAccordeeDirecteur()
                    : ligne.getQuantiteAccordeeCsa() != null ? ligne.getQuantiteAccordeeCsa() : ligne.getQuantite();
            ligne.setMontant(ligne.getPrixUnitaire().multiply(BigDecimal.valueOf(quantiteEffective)));
        }
        eb.setMontantInitial(totalLignes(eb.getLignes()));
    }

    public ExpressionBesoin rejeter(String id, String motif) {
        ExpressionBesoin eb = getById(id);
        if (eb.getStatut() == ExpressionBesoin.Statut.TRAITEE || eb.getStatut() == ExpressionBesoin.Statut.REJETEE)
            throw new RuntimeException("Cette expression de besoin ne peut plus être rejetée");

        eb.setStatut(ExpressionBesoin.Statut.REJETEE);
        eb.setMotifRejet(motif);
        eb.setRejetePar(getUsername());
        eb.setDateRejet(LocalDateTime.now());
        return expressionBesoinRepo.save(eb);
    }

    // ═══════════════════════════════════════════════════════════════
    // TRAITEMENT COMPTABLE (chef comptable / agent comptable)
    // ═══════════════════════════════════════════════════════════════
    public ExpressionBesoin traiter(String id, TraiterRequest req) {
        ExpressionBesoin eb = getById(id);
        if (eb.getStatut() != ExpressionBesoin.Statut.VALIDEE)
            throw new RuntimeException("Cette expression de besoin doit être validée avant d'être traitée");

        eb.setMontantReel(req.getMontantReel());
        eb.setBeneficiaire(req.getBeneficiaire());
        eb.setTraitePar(getUsername());
        eb.setDateTraitement(LocalDateTime.now());
        eb.setStatut(ExpressionBesoin.Statut.TRAITEE);
        return expressionBesoinRepo.save(eb);
    }

    // ── Appelée par MandatementService quand un mandatement référence cette EB ──
    public void marquerUtilisee(String id, String mandatementId) {
        ExpressionBesoin eb = getById(id);
        if (eb.getStatut() != ExpressionBesoin.Statut.TRAITEE || eb.isUtiliseePourMandatement())
            throw new RuntimeException("Cette expression de besoin n'est pas disponible pour un mandatement");
        eb.setUtiliseePourMandatement(true);
        eb.setMandatementId(mandatementId);
        expressionBesoinRepo.save(eb);
    }

    // ═══════════════════════════════════════════════════════════════
    // LECTURE
    // ═══════════════════════════════════════════════════════════════
    public ExpressionBesoin getById(String id) {
        return expressionBesoinRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Expression de besoin introuvable : " + id));
    }

    public List<ExpressionBesoin> getMesExpressions() {
        return expressionBesoinRepo.findByCreeParOrderByDateCreationDesc(getUsername());
    }

    // Ne montre que ce qu'il reste réellement à valider pour le rôle connecté :
    // un CSA qui a déjà validé un dossier (encore EN_ATTENTE du Directeur) ne doit plus le voir ici.
    public List<ExpressionBesoin> getAValider() {
        boolean estCsa = hasAuthority("CSA");
        boolean estDirecteur = hasAuthority("DIRECTEUR");
        return expressionBesoinRepo.findByStatutOrderByDateCreationDesc(ExpressionBesoin.Statut.EN_ATTENTE).stream()
                .filter(eb -> {
                    boolean directeurRequis = eb.getMontantInitial().compareTo(SEUIL_VALIDATION_DIRECTEUR) > 0;
                    if (estCsa && !eb.isValidationCsa()) return true;
                    if (estDirecteur && directeurRequis && !eb.isValidationDirecteur()) return true;
                    return false;
                })
                .toList();
    }

    // Dossiers déjà validés par le rôle connecté (qu'ils attendent encore l'autre validateur,
    // soient définitivement validés, ou déjà traités par la comptabilité).
    public List<ExpressionBesoin> getValidees() {
        boolean estCsa = hasAuthority("CSA");
        boolean estDirecteur = hasAuthority("DIRECTEUR");
        return expressionBesoinRepo.findAll().stream()
                .filter(eb -> eb.getStatut() != ExpressionBesoin.Statut.REJETEE)
                .filter(eb -> (estCsa && eb.isValidationCsa()) || (estDirecteur && eb.isValidationDirecteur()))
                .sorted(java.util.Comparator.comparing(ExpressionBesoin::getDateCreation).reversed())
                .toList();
    }

    public List<ExpressionBesoin> getATraiter() {
        return expressionBesoinRepo.findByStatutOrderByDateCreationDesc(ExpressionBesoin.Statut.VALIDEE);
    }

    public List<ExpressionBesoin> getTraitees() {
        return expressionBesoinRepo.findByStatutOrderByDateCreationDesc(ExpressionBesoin.Statut.TRAITEE);
    }

    public List<ExpressionBesoin> getDisponiblesPourMandatement() {
        return expressionBesoinRepo.findByStatutAndUtiliseePourMandatementFalseOrderByDateCreationDesc(ExpressionBesoin.Statut.TRAITEE);
    }

    // ── Utilitaires ──
    private String saveFile(MultipartFile file, String type) {
        if (file == null || file.isEmpty()) return null;
        try {
            ObjectId id = gridFsTemplate.store(file.getInputStream(), file.getOriginalFilename(), file.getContentType());
            log.info("📄 Upload {} : {}", type, id.toHexString());
            return "/api/v1/files/view/" + id.toHexString();
        } catch (IOException e) {
            throw new RuntimeException("Erreur upload fichier " + type, e);
        }
    }

    private boolean hasAuthority(String authority) {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(authority));
    }

    private String getUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
