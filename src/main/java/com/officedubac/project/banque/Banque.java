package com.officedubac.project.banque;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
@Document(collection = "banque")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Banque {
    @Id
    private String id;
    @Indexed(unique = true)
    private  String name;
    private String codeBanque;
    private String NomComplet;
    private Long utiCree;
    private LocalDateTime dateCreation;
    private Long utiModifie;
    private LocalDateTime dateModification;
}
