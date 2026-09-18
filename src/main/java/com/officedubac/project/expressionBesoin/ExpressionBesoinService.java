package com.officedubac.project.expressionBesoin;

import com.officedubac.project.caisseAvance.CaisseAvanceService;
import com.officedubac.project.caisseAvance.Motif;
import com.officedubac.project.caisseAvance.MotifRepository;
import com.officedubac.project.models.Role;
import com.officedubac.project.models.User;
import com.officedubac.project.notification.WhatsAppService;
import com.officedubac.project.personnel.Personnel;
import com.officedubac.project.personnel.PersonnelRepository;
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
    private final PersonnelRepository        personnelRepository;
    private final WhatsAppService            whatsAppService;
    private final MotifRepository            motifRepository;

    // Au-delà de ce montant, la validation du Directeur est requise en plus de celle du CSA
    private static final BigDecimal SEUIL_VALIDATION_DIRECTEUR = BigDecimal.valueOf(20_000);

    // ═══════════════════════════════════════════════════════════════
    // CRÉATION / MODIFICATION (chef de service)
    // ═══════════════════════════════════════════════════════════════
    // Facture proforma optionnelle (case à cocher) : si cochée, la facture proforma est
    // requise ; sinon, une déclaration sur l'honneur est requise à la place.
    public ExpressionBesoin creer(ExpressionBesoinRequest req, MultipartFile pdfFactureProforma, MultipartFile pdfDeclarationHonneur) {
        boolean aProforma = Boolean.TRUE.equals(req.getAFacturePreformat());
        validerPieceJointe(aProforma, pdfFactureProforma, pdfDeclarationHonneur);

        User createur = userRepository.findByLogin(getUsername())
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        BigDecimal montant = montantLigne(req.getPrixUnitaire(), req.getQuantite());
        boolean requiertSatisfaction = requiertSatisfaction(req.getMotifId());
        Beneficiaire beneficiaire = resoudreBeneficiaire(req, createur);

        ExpressionBesoin eb = ExpressionBesoin.builder()
                .motifId(req.getMotifId())
                .motifLibelle(req.getMotifLibelle())
                .quantite(req.getQuantite())
                .prixUnitaire(req.getPrixUnitaire())
                .montantInitial(montant)
                .requiertSatisfaction(requiertSatisfaction)
                .aFacturePreformat(aProforma)
                .urlPdfFactureProforma(aProforma ? saveFile(pdfFactureProforma, "facture-proforma") : null)
                .urlPdfDeclarationHonneur(!aProforma ? saveFile(pdfDeclarationHonneur, "declaration-honneur") : null)
                .statut(ExpressionBesoin.Statut.EN_ATTENTE)
                .beneficiaireId(beneficiaire.id())
                .beneficiaireNom(beneficiaire.nom())
                .beneficiaireMoiMeme(beneficiaire.moiMeme())
                .creePar(createur.getLogin())
                .creeParNom(nomComplet(createur))
                .build();

        ExpressionBesoin saved = expressionBesoinRepo.save(eb);
        notifierValidateurs(saved);
        return saved;
    }

    public ExpressionBesoin modifier(String id, ExpressionBesoinRequest req, MultipartFile pdfFactureProforma, MultipartFile pdfDeclarationHonneur) {
        ExpressionBesoin eb = getById(id);

        if (!eb.getCreePar().equals(getUsername()))
            throw new RuntimeException("Vous ne pouvez modifier que vos propres expressions de besoin");
        if (eb.getStatut() != ExpressionBesoin.Statut.EN_ATTENTE)
            throw new RuntimeException("Cette expression de besoin ne peut plus être modifiée");

        User createur = userRepository.findByLogin(getUsername())
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        boolean aProforma = Boolean.TRUE.equals(req.getAFacturePreformat());
        boolean nouveauFichier = aProforma
                ? (pdfFactureProforma != null && !pdfFactureProforma.isEmpty())
                : (pdfDeclarationHonneur != null && !pdfDeclarationHonneur.isEmpty());
        boolean choixInchange = aProforma == eb.isAFacturePreformat();
        boolean fichierExistant = aProforma ? eb.getUrlPdfFactureProforma() != null : eb.getUrlPdfDeclarationHonneur() != null;
        if (!nouveauFichier && !(choixInchange && fichierExistant))
            validerPieceJointe(aProforma, pdfFactureProforma, pdfDeclarationHonneur);

        Beneficiaire beneficiaire = resoudreBeneficiaire(req, createur);

        eb.setMotifId(req.getMotifId());
        eb.setMotifLibelle(req.getMotifLibelle());
        eb.setQuantite(req.getQuantite());
        eb.setPrixUnitaire(req.getPrixUnitaire());
        eb.setMontantInitial(montantLigne(req.getPrixUnitaire(), req.getQuantite()));
        eb.setRequiertSatisfaction(requiertSatisfaction(req.getMotifId()));
        eb.setBeneficiaireId(beneficiaire.id());
        eb.setBeneficiaireNom(beneficiaire.nom());
        eb.setBeneficiaireMoiMeme(beneficiaire.moiMeme());
        eb.setAFacturePreformat(aProforma);
        if (aProforma) {
            if (nouveauFichier) eb.setUrlPdfFactureProforma(saveFile(pdfFactureProforma, "facture-proforma"));
            eb.setUrlPdfDeclarationHonneur(null);
        } else {
            if (nouveauFichier) eb.setUrlPdfDeclarationHonneur(saveFile(pdfDeclarationHonneur, "declaration-honneur"));
            eb.setUrlPdfFactureProforma(null);
        }

        return expressionBesoinRepo.save(eb);
    }

    private record Beneficiaire(String id, String nom, boolean moiMeme) {}

    // Le créateur peut se désigner lui-même bénéficiaire, ou choisir un agent de sa
    // division (liste identique à celle utilisée pour les congés/autorisations, cf.
    // /personnel/mes-agents) — un agent externe (Personnel autonome, sans compte) ou un
    // agent disposant d'un compte (User) : dans ce dernier cas, l'id stocké est celui du
    // User, ce qui permet à cet agent de retrouver ensuite les expressions le concernant
    // via /expression-besoin/liees-a-moi.
    private Beneficiaire resoudreBeneficiaire(ExpressionBesoinRequest req, User createur) {
        if (req.isBeneficiaireMoiMeme() || req.getBeneficiaireId() == null || req.getBeneficiaireId().isBlank())
            return new Beneficiaire(createur.getId(), nomComplet(createur), true);

        String id = req.getBeneficiaireId();
        Personnel externe = personnelRepository.findById(id).orElse(null);
        if (externe != null) return new Beneficiaire(externe.getId(), nomComplet(externe), false);

        User interne = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bénéficiaire introuvable"));
        return new Beneficiaire(interne.getId(), nomComplet(interne), false);
    }

    private BigDecimal montantLigne(BigDecimal prixUnitaire, Integer quantite) {
        return quantite != null ? prixUnitaire.multiply(BigDecimal.valueOf(quantite)) : prixUnitaire;
    }

    // Snapshot au moment de la création : un motif ultérieurement modifié ne doit pas
    // changer rétroactivement l'exigence de satisfaction d'une EB déjà créée.
    private boolean requiertSatisfaction(String motifId) {
        return motifRepository.findById(motifId).map(Motif::isRequiertSatisfaction).orElse(false);
    }

    private void validerPieceJointe(boolean aProforma, MultipartFile pdfFactureProforma, MultipartFile pdfDeclarationHonneur) {
        if (aProforma && (pdfFactureProforma == null || pdfFactureProforma.isEmpty()))
            throw new RuntimeException("La facture proforma (PDF) est requise");
        if (!aProforma && (pdfDeclarationHonneur == null || pdfDeclarationHonneur.isEmpty()))
            throw new RuntimeException("La déclaration sur l'honneur (PDF) est requise");
    }

    // Notifie systématiquement le(s) CSA, et en plus le(s) Directeur si le montant
    // initial dépasse le seuil imposant sa validation.
    private void notifierValidateurs(ExpressionBesoin eb) {
        userRepository.findByProfilName(Role.CSA)
                .forEach(u -> whatsAppService.envoyerNotificationValidation(u.getPersonnel().getPhone()));

        if (eb.getMontantInitial().compareTo(SEUIL_VALIDATION_DIRECTEUR) > 0) {
            userRepository.findByProfilName(Role.DIRECTEUR)
                    .forEach(u -> whatsAppService.envoyerNotificationValidation(u.getPersonnel().getPhone()));
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

        User validateur = userRepository.findByLogin(getUsername())
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
        boolean estCsa = hasAuthority("CSA");
        boolean estDirecteur = hasAuthority("DIRECTEUR");
        if (!estCsa && !estDirecteur)
            throw new RuntimeException("Rôle non autorisé à valider une expression de besoin");

        // Toute EB avec une quantité demandée doit recevoir une quantité accordée de la
        // part de CE validateur (CSA et Directeur renseignent chacun la leur séparément).
        if (eb.getQuantite() != null) {
            if (req.getQuantiteAccordee() == null)
                throw new RuntimeException("La quantité accordée est requise pour \"" + eb.getMotifLibelle() + "\"");
            if (estCsa) eb.setQuantiteAccordeeCsa(req.getQuantiteAccordee());
            if (estDirecteur) eb.setQuantiteAccordeeDirecteur(req.getQuantiteAccordee());
        }
        recalculerMontant(eb);

        if (estCsa) {
            eb.setValidationCsa(true);
            eb.setValidateurCsa(validateur.getLogin());
            eb.setValidateurCsaNom(nomComplet(validateur));
            eb.setDateValidationCsa(LocalDateTime.now());
        }
        if (estDirecteur) {
            eb.setValidationDirecteur(true);
            eb.setValidateurDirecteur(validateur.getLogin());
            eb.setValidateurDirecteurNom(nomComplet(validateur));
            eb.setDateValidationDirecteur(LocalDateTime.now());
        }

        // Le Directeur est toujours l'étape décisionnaire finale quand sa validation est
        // requise — même si le CSA a rejeté au préalable, la chaîne continue jusqu'à lui.
        // Sinon (petit montant), le CSA seul suffit.
        boolean directeurRequis = eb.getMontantInitial().compareTo(SEUIL_VALIDATION_DIRECTEUR) > 0;
        if (directeurRequis ? eb.isValidationDirecteur() : eb.isValidationCsa()) {
            eb.setStatut(ExpressionBesoin.Statut.VALIDEE);
        }

        return expressionBesoinRepo.save(eb);
    }

    // Recalcule le montant selon la quantité effective (celle du Directeur si renseignée,
    // sinon celle du CSA, sinon la quantité initiale demandée) — tout changement de prix
    // ou de quantité doit s'y répercuter.
    private void recalculerMontant(ExpressionBesoin eb) {
        if (eb.getQuantite() == null) {
            eb.setMontantInitial(eb.getPrixUnitaire());
            return;
        }
        Integer quantiteEffective = eb.getQuantiteAccordeeDirecteur() != null
                ? eb.getQuantiteAccordeeDirecteur()
                : eb.getQuantiteAccordeeCsa() != null ? eb.getQuantiteAccordeeCsa() : eb.getQuantite();
        eb.setMontantInitial(eb.getPrixUnitaire().multiply(BigDecimal.valueOf(quantiteEffective)));
    }

    // Comme pour les congés/autorisations : un rejet du CSA n'interrompt la chaîne que si
    // le Directeur doit aussi se prononcer (montant > seuil) — dans ce cas, elle continue
    // jusqu'à lui, seul décisionnaire final. Sinon, le rejet du CSA est immédiatement
    // définitif puisqu'aucune autre étape n'est prévue.
    public ExpressionBesoin rejeter(String id, String motif) {
        ExpressionBesoin eb = getById(id);
        if (eb.getStatut() != ExpressionBesoin.Statut.EN_ATTENTE)
            throw new RuntimeException("Cette expression de besoin ne peut plus être rejetée");

        User rejetant = userRepository.findByLogin(getUsername())
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
        boolean estCsa = hasAuthority("CSA");
        boolean estDirecteur = hasAuthority("DIRECTEUR");
        if (!estCsa && !estDirecteur)
            throw new RuntimeException("Rôle non autorisé à rejeter une expression de besoin");

        boolean directeurRequis = eb.getMontantInitial().compareTo(SEUIL_VALIDATION_DIRECTEUR) > 0;

        if (estCsa) {
            eb.setRejetCsa(true);
            eb.setMotifRejetCsa(motif);
            eb.setRejeteParCsaNom(nomComplet(rejetant));
            eb.setDateRejetCsa(LocalDateTime.now());
            if (!directeurRequis) {
                finaliserRejet(eb, motif, rejetant);
            }
        } else {
            // Le Directeur tranche toujours définitivement, y compris après un rejet du CSA.
            finaliserRejet(eb, motif, rejetant);
        }

        return expressionBesoinRepo.save(eb);
    }

    private void finaliserRejet(ExpressionBesoin eb, String motif, User rejetant) {
        eb.setStatut(ExpressionBesoin.Statut.REJETEE);
        eb.setMotifRejet(motif);
        eb.setRejetePar(rejetant.getLogin());
        eb.setRejeteParNom(nomComplet(rejetant));
        eb.setDateRejet(LocalDateTime.now());
    }

    // ═══════════════════════════════════════════════════════════════
    // TRAITEMENT COMPTABLE (chef comptable / agent comptable)
    // ═══════════════════════════════════════════════════════════════
    public ExpressionBesoin traiter(String id, TraiterRequest req) {
        ExpressionBesoin eb = getById(id);
        if (eb.getStatut() != ExpressionBesoin.Statut.VALIDEE)
            throw new RuntimeException("Cette expression de besoin doit être validée avant d'être traitée");

        User traitant = userRepository.findByLogin(getUsername())
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        eb.setMontantReel(req.getMontantReel());
        eb.setTraitePar(traitant.getLogin());
        eb.setTraiteParNom(nomComplet(traitant));
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
    // SATISFACTION DU DEMANDEUR
    // ═══════════════════════════════════════════════════════════════
    public ExpressionBesoin confirmerSatisfaction(String id) {
        ExpressionBesoin eb = getById(id);
        if (!eb.getCreePar().equals(getUsername()))
            throw new RuntimeException("Seul le demandeur d'origine peut confirmer sa satisfaction");
        if (eb.getStatut() != ExpressionBesoin.Statut.TRAITEE)
            throw new RuntimeException("La satisfaction ne peut être confirmée qu'une fois l'expression de besoin traitée");
        if (!eb.isRequiertSatisfaction())
            throw new RuntimeException("Aucune confirmation de satisfaction n'est requise pour cette expression de besoin");

        eb.setSatisfactionConfirmee(true);
        eb.setDateSatisfaction(LocalDateTime.now());
        return expressionBesoinRepo.save(eb);
    }

    // Appelée par MandatementService avant tout décaissement lié à cette EB.
    public void verifierSatisfactionPourDecaissement(String id) {
        ExpressionBesoin eb = getById(id);
        if (eb.isRequiertSatisfaction() && !eb.isSatisfactionConfirmee())
            throw new RuntimeException("Le demandeur doit confirmer sa satisfaction avant tout décaissement pour l'expression de besoin \""
                    + id + "\"");
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

    // Lecture seule pour un agent simple : les expressions de besoin où il a été déclaré
    // bénéficiaire par son chef de service (il n'en crée jamais lui-même).
    public List<ExpressionBesoin> getLieesAMoi() {
        User moi = userRepository.findByLogin(getUsername())
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
        return expressionBesoinRepo.findByBeneficiaireIdOrderByDateCreationDesc(moi.getId());
    }

    // Ne montre que ce qu'il reste réellement à valider pour le rôle connecté :
    // un CSA qui a déjà validé un dossier (encore EN_ATTENTE du Directeur) ne doit plus le voir ici.
    public List<ExpressionBesoin> getAValider() {
        boolean estCsa = hasAuthority("CSA");
        boolean estDirecteur = hasAuthority("DIRECTEUR");
        return expressionBesoinRepo.findByStatutOrderByDateCreationDesc(ExpressionBesoin.Statut.EN_ATTENTE).stream()
                .filter(eb -> {
                    boolean directeurRequis = eb.getMontantInitial().compareTo(SEUIL_VALIDATION_DIRECTEUR) > 0;
                    if (estCsa && !eb.isValidationCsa() && !eb.isRejetCsa()) return true;
                    if (estDirecteur && directeurRequis && !eb.isValidationDirecteur()) return true;
                    return false;
                })
                .toList();
    }

    // Dossiers rejetés (un rejet est définitif) — onglet dédié pour le CSA/Directeur.
    public List<ExpressionBesoin> getRejetees() {
        return expressionBesoinRepo.findByStatutOrderByDateCreationDesc(ExpressionBesoin.Statut.REJETEE);
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

    // Exclut les EB qui exigent une confirmation de satisfaction non encore donnée —
    // évite au comptable de sélectionner une EB dont le décaissement échouera.
    public List<ExpressionBesoin> getDisponiblesPourMandatement() {
        return expressionBesoinRepo.findByStatutAndUtiliseePourMandatementFalseOrderByDateCreationDesc(ExpressionBesoin.Statut.TRAITEE)
                .stream()
                .filter(eb -> !eb.isRequiertSatisfaction() || eb.isSatisfactionConfirmee())
                .toList();
    }

    // ── Utilitaires ──
    private String nomComplet(User u) {
        if (u.getPersonnel() == null) return u.getLogin();
        String nom = (u.getPersonnel().getFirstname() + " " + u.getPersonnel().getLastname()).trim();
        return nom.isEmpty() ? u.getLogin() : nom;
    }

    private String nomComplet(Personnel p) {
        String nom = ((p.getFirstname() != null ? p.getFirstname() : "") + " "
                + (p.getLastname() != null ? p.getLastname() : "")).trim();
        return nom.isEmpty() ? "—" : nom;
    }

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
