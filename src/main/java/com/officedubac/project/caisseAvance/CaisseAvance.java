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

    @CreatedDate
    private LocalDateTime dateCreation;
    @LastModifiedDate
    private LocalDateTime dateModification;
}
