package com.officedubac.project.expressionBesoin;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "eb_expressions_besoin")
public class ExpressionBesoin {

    @Id
    private String id;

    // ── Désignation (réutilise les mêmes motifs que la caisse d'avance) ──
    private String motifId;
    private String motifLibelle;

    private BigDecimal montantInitial;

    // ── Justificatif : soit une facture proforma, soit une déclaration sur l'honneur ──
    private boolean aFacturePreformat;
    private String urlPdfFactureProforma;
    private String urlPdfDeclarationHonneur;

    private Statut statut;

    // ── Validation CSA ──
    private boolean validationCsa;
    private String validateurCsa;
    private LocalDateTime dateValidationCsa;

    // ── Validation Directeur (uniquement requise si montantInitial > seuil) ──
    private boolean validationDirecteur;
    private String validateurDirecteur;
    private LocalDateTime dateValidationDirecteur;

    // ── Rejet ──
    private String motifRejet;
    private String rejetePar;
    private LocalDateTime dateRejet;

    // ── Traitement comptable (montant réel + bénéficiaire) ──
    private BigDecimal montantReel;
    private String beneficiaire;
    private String traitePar;
    private LocalDateTime dateTraitement;

    // ── Consommation par un mandatement ──
    private boolean utiliseePourMandatement;
    private String mandatementId;

    private String creePar;

    @CreatedDate
    private LocalDateTime dateCreation;
    @LastModifiedDate
    private LocalDateTime dateModification;

    public enum Statut { EN_ATTENTE, VALIDEE, REJETEE, TRAITEE }
}
