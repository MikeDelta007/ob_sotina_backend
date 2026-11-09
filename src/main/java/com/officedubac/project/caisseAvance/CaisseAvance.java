package com.officedubac.project.caisseAvance;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "ca_caisse")
public class CaisseAvance {

    @Id
    private String id;

    private BigDecimal montant;       // solde courant
    private LocalDate date;
    private String description;

    // Depuis quand le solde est resté en continu ≤ au seuil d'alerte (100 000) — remis à null
    // dès que le solde repasse au-dessus, fixé à la première descente sous le seuil sinon.
    private LocalDateTime dateDepuisSeuilBas;

    @CreatedDate
    private LocalDateTime dateCreation;
    @LastModifiedDate
    private LocalDateTime dateModification;
}
