package com.officedubac.project.banque.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class BanqueResponse {
    private String id;
    private String name;
    private String codeBanque;
    private String NomComplet;
    private Long utiCree;
    protected LocalDateTime dateCreation;
    protected Long utiModifie;
    protected LocalDateTime dateModification;
}
