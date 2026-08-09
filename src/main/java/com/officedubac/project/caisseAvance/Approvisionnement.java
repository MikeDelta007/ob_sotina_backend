package com.officedubac.project.caisseAvance;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "ca_approvisionnement")
public class Approvisionnement {

    @Id
    private String id;

    private BigDecimal montant;      // montant ajouté
    private BigDecimal soldeAvant;
    private BigDecimal soldeApres;
    private LocalDate date;
    private String description;
    private String creePar;

    @CreatedDate
    private LocalDateTime dateCreation;
}
