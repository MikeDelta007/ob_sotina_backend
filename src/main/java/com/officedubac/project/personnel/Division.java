package com.officedubac.project.personnel;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "personnel_divisions")
public class Division {

    @Id
    private String id;

    private String libelle;

    // Id du User désigné comme chef de cette division (valide en premier les demandes d'absence)
    private String chefServiceId;

    private boolean actif = true;

    // Nom du chef, calculé à la lecture (non stocké) pour l'affichage de la liste des divisions
    @org.springframework.data.annotation.Transient
    private String chefServiceNom;

    @CreatedDate
    private LocalDateTime dateCreation;
    @LastModifiedDate
    private LocalDateTime dateModification;
}
