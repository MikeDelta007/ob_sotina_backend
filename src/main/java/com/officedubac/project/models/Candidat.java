package com.officedubac.project.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "candidat")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Candidat
{
    @Id
    private String id;
    private Long session;
    @Indexed(unique = true)
    private String numEnrolement;
    private String dosNumber;
    private String dosNumber_by_session_and_etablissement;
    private String firstname;
    private String lastname;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date_birth;
    private String place_birth;
    private Gender gender;
    private String phone1;
    private String phone2;
    private String email;
    private int year_registry_num;
    private String registry_num;
    private int bac_do_count;
    private OrigineBfem origine_bfem;
    private int year_bfem;
    private String subject;
    private boolean handicap;
    private String type_handicap;
    private String eps;
    private boolean cdt_is_cgs;
    //0 - En cours, 1 - Validé, 2 - Rejeté

    private int decision;

    private Option matiere1;
    private Option matiere2;
    private Option matiere3;

    // Epreuve Facultative Liste A
    private ListeA eprFacListA;

    // Epreuve Facultative Liste B
    private Option eprFacListB;

    private TypeCandidat typeCandidat;
    private Etablissement etablissement;
    private CentreEtatCivil centreEtatCivil;
    private Serie serie;
    private Nationality nationality;
    private Nationality countryBirth;
    private ConcoursGeneral concoursGeneral;

    private CentreExamen centreExamen;

    private List<Rejet> rejets;

    private boolean alreadyBac;

    private String operator;
    private LocalDateTime dateOperation;

    private String codeEnrolementEC;
}
