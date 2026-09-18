package com.officedubac.project.banque.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class BanqueRequest {
    private String name;
    private String codeBanque;
    private String NomComplet;
}
