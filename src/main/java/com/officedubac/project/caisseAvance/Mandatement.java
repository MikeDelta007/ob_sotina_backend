package com.officedubac.project.caisseAvance;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Mandatement — entité parent.
 * Un mandatement simple contient une seule facture.
 * Un mandatement cumulatif contient N factures.
 * Les factures sont embedded dans le mandatement.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "ca_mandatements")
public class Mandatement {

    @Id
    private String id;

    private TypeMandatement type;   // SIMPLE | CUMULATIF

    // ── Règlement ──
    private TypePaiement typePaiement;      // TOTALITE | AVANCE
    private BigDecimal montantTotal;        // somme de toutes les factures
    private BigDecimal montantAvance;       // si AVANCE (#2)
    private BigDecimal montantReliquat;     // montantTotal - montantAvance (#2')

    // ── Mode de paiement (calculé auto selon montantTotal) ──
    private ModePaiement modePaiement;      // ESPECES ≤100k | CHEQUE >100k

    // ── Décaissement ──
    private boolean decaisse;
    private BigDecimal montantDecaisse;     // 0 si CHEQUE
    private BigDecimal soldeAvant;
    private BigDecimal soldeApres;

    // ── Reliquat (si typePaiement = AVANCE) ──
    private boolean reliquatPaye;
    private LocalDateTime dateReliquatPaye;
    private ModePaiement modePaiementReliquat;   // mode réel du paiement du reliquat
    private String urlPdfChequeReliquat;         // si modePaiementReliquat = CHEQUE
    private String urlPdfCniReliquat;            // si modePaiementReliquat = CHEQUE

    // ── Factures embedded ──
    // Simple  : 1 facture
    // Cumulatif : N factures
    private List<FactureEmbedded> factures;

    // ── Observations libres sur le mandatement ──
    private String description;

    // ── Auteur ──
    private String creePar;

    @CreatedDate
    private LocalDateTime dateCreation;
    @LastModifiedDate
    private LocalDateTime dateModification;

    // ════════════════════════════════════════
    // Facture embedded (pas de collection propre)
    // ════════════════════════════════════════
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FactureEmbedded {
        private String numero;           // 2026-N-FAC_001_username
        private LocalDateTime date;
        private BigDecimal montant;
        private String motifId;
        private String motifLibelle;
        // Pièces jointes
        private String urlPdfFacture;
        private String urlPdfCheque;    // si CHEQUE
        private String urlPdfCni;       // si CHEQUE
    }

    // ── Enums ──
    public enum TypeMandatement { SIMPLE, CUMULATIF }
    public enum TypePaiement    { TOTALITE, AVANCE }
    public enum ModePaiement    { ESPECES, CHEQUE }
}
