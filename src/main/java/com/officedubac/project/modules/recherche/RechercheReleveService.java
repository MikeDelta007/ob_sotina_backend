package com.officedubac.project.modules.recherche;

import com.officedubac.project.modules.A1.model.RelevNoteA1;
import com.officedubac.project.modules.A2.model.RelevNoteA2;
import com.officedubac.project.modules.A3.model.RelevNoteA3;
import com.officedubac.project.modules.A4.model.RelevNoteA4;
import com.officedubac.project.modules.B.model.RelevNoteB;
import com.officedubac.project.modules.D.model.RelevNoteD;
import com.officedubac.project.modules.E.model.RelevNoteE;
import com.officedubac.project.modules.F1.model.RelevNoteF1;
import com.officedubac.project.modules.F7.model.RelevNoteF7;
import com.officedubac.project.modules.G.model.RelevNoteG;
import com.officedubac.project.modules.G1.model.RelevNoteG1;
import com.officedubac.project.modules.G2.model.RelevNoteG2;
import com.officedubac.project.modules.L1A.model.RelevNoteL1A;
import com.officedubac.project.modules.L1B.model.RelevNoteL1B;
import com.officedubac.project.modules.L2.model.RelevNoteL2;
import com.officedubac.project.modules.Lprime1.model.RelevNoteLPrime1;
import com.officedubac.project.modules.S1.model.RelevNoteS1;
import com.officedubac.project.modules.S2.model.RelevNoteS2;
import com.officedubac.project.modules.S3.model.RelevNoteS3;
import com.officedubac.project.modules.S4.model.RelevNoteS4;
import com.officedubac.project.modules.S5.model.RelevNoteS5;
import com.officedubac.project.modules.T1.model.RelevNoteT1;
import com.officedubac.project.modules.T2.model.RelevNoteT2;
import com.officedubac.project.modules.a1deuxiemepartie.model.ReleveA1DeuxiemePartie;
import com.officedubac.project.modules.a2deuxiemepartie.model.ReleveA2DeuxiemePartie;
import com.officedubac.project.modules.a3deuxiemepartie.model.ReleveA3DeuxiemePartie;
import com.officedubac.project.modules.c2emepartie.model.ReleveC2emePartie;
import com.officedubac.project.modules.ddeuxiemepartie.model.ReleveDDeuxiemePartie;
import com.officedubac.project.modules.f1deuxiemepartie.model.ReleveF1DeuxiemePartie;
import com.officedubac.project.modules.recherche.dto.RechercheReleveResultat;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Recherche un ou plusieurs relevés de notes (bottins) à travers les 29
 * séries, à partir de n'importe quelle combinaison de critères : N° de
 * table, année, nom, prénom, date de naissance, lieu de naissance.
 *
 * Interroge directement chaque collection MongoDB via {@link MongoTemplate},
 * sans passer par les 29 services de série : aucune modification des
 * modules existants n'est nécessaire.
 *
 * Le N° de table est comparé en égalité exacte (insensible à la casse).
 * Le nom, le prénom et le lieu de naissance sont comparés en "contient"
 * (insensible à la casse) : le nom et le prénom sont chacun recherchés dans
 * le champ combiné `candidat.nomPrenom` (la base ne distingue pas les deux).
 * La date de naissance est comparée en égalité exacte.
 */
@Service
public class RechercheReleveService {

    private static final int TAILLE_MAX_PAR_SERIE = 20;

    private final MongoTemplate mongoTemplate;

    public RechercheReleveService(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public List<RechercheReleveResultat> rechercher(
            String numeroTable, Integer annee, String nom, String prenom,
            LocalDate dateNaissance, String lieuNaissance
    ) {
        boolean auMoinsUnCritere = estRempli(numeroTable) || annee != null || estRempli(nom)
                || estRempli(prenom) || dateNaissance != null || estRempli(lieuNaissance);
        if (!auMoinsUnCritere) {
            return List.of();
        }

        Query avecAnnee = construireQuery(numeroTable, annee, nom, prenom, dateNaissance, lieuNaissance, true);
        Query sansAnnee = construireQuery(numeroTable, null, nom, prenom, dateNaissance, lieuNaissance, false);

        List<RechercheReleveResultat> resultats = new ArrayList<>();

        // ---- Séries avec année de session ----
        for (RelevNoteA1 r : mongoTemplate.find(avecAnnee, RelevNoteA1.class)) {
            resultats.add(resultat("a1", r.getId(), r.getCandidat() == null ? null : r.getCandidat().getNumeroTable(),
                    r.getCandidat() == null ? null : r.getCandidat().getNomPrenom(),
                    r.getCandidat() == null ? null : r.getCandidat().getDateNaissance(),
                    r.getCandidat() == null ? null : r.getCandidat().getLieuNaissance(), r.getAnnee()));
        }
        for (RelevNoteA2 r : mongoTemplate.find(avecAnnee, RelevNoteA2.class)) {
            resultats.add(resultat("a2", r.getId(), r.getCandidat() == null ? null : r.getCandidat().getNumeroTable(),
                    r.getCandidat() == null ? null : r.getCandidat().getNomPrenom(),
                    r.getCandidat() == null ? null : r.getCandidat().getDateNaissance(),
                    r.getCandidat() == null ? null : r.getCandidat().getLieuNaissance(), r.getAnnee()));
        }
        for (RelevNoteA3 r : mongoTemplate.find(avecAnnee, RelevNoteA3.class)) {
            resultats.add(resultat("a3", r.getId(), r.getCandidat() == null ? null : r.getCandidat().getNumeroTable(),
                    r.getCandidat() == null ? null : r.getCandidat().getNomPrenom(),
                    r.getCandidat() == null ? null : r.getCandidat().getDateNaissance(),
                    r.getCandidat() == null ? null : r.getCandidat().getLieuNaissance(), r.getAnnee()));
        }
        for (RelevNoteA4 r : mongoTemplate.find(avecAnnee, RelevNoteA4.class)) {
            resultats.add(resultat("a4", r.getId(), r.getCandidat() == null ? null : r.getCandidat().getNumeroTable(),
                    r.getCandidat() == null ? null : r.getCandidat().getNomPrenom(),
                    r.getCandidat() == null ? null : r.getCandidat().getDateNaissance(),
                    r.getCandidat() == null ? null : r.getCandidat().getLieuNaissance(), r.getAnnee()));
        }
        for (RelevNoteB r : mongoTemplate.find(avecAnnee, RelevNoteB.class)) {
            resultats.add(resultat("b", r.getId(), r.getCandidat() == null ? null : r.getCandidat().getNumeroTable(),
                    r.getCandidat() == null ? null : r.getCandidat().getNomPrenom(),
                    r.getCandidat() == null ? null : r.getCandidat().getDateNaissance(),
                    r.getCandidat() == null ? null : r.getCandidat().getLieuNaissance(), r.getAnnee()));
        }
        for (RelevNoteD r : mongoTemplate.find(avecAnnee, RelevNoteD.class)) {
            resultats.add(resultat("d", r.getId(), r.getCandidat() == null ? null : r.getCandidat().getNumeroTable(),
                    r.getCandidat() == null ? null : r.getCandidat().getNomPrenom(),
                    r.getCandidat() == null ? null : r.getCandidat().getDateNaissance(),
                    r.getCandidat() == null ? null : r.getCandidat().getLieuNaissance(), r.getAnnee()));
        }
        for (RelevNoteE r : mongoTemplate.find(avecAnnee, RelevNoteE.class)) {
            resultats.add(resultat("e", r.getId(), r.getCandidat() == null ? null : r.getCandidat().getNumeroTable(),
                    r.getCandidat() == null ? null : r.getCandidat().getNomPrenom(),
                    r.getCandidat() == null ? null : r.getCandidat().getDateNaissance(),
                    r.getCandidat() == null ? null : r.getCandidat().getLieuNaissance(), r.getAnnee()));
        }
        for (RelevNoteF1 r : mongoTemplate.find(avecAnnee, RelevNoteF1.class)) {
            resultats.add(resultat("f1", r.getId(), r.getCandidat() == null ? null : r.getCandidat().getNumeroTable(),
                    r.getCandidat() == null ? null : r.getCandidat().getNomPrenom(),
                    r.getCandidat() == null ? null : r.getCandidat().getDateNaissance(),
                    r.getCandidat() == null ? null : r.getCandidat().getLieuNaissance(), r.getAnnee()));
        }
        for (RelevNoteF7 r : mongoTemplate.find(avecAnnee, RelevNoteF7.class)) {
            resultats.add(resultat("f7", r.getId(), r.getCandidat() == null ? null : r.getCandidat().getNumeroTable(),
                    r.getCandidat() == null ? null : r.getCandidat().getNomPrenom(),
                    r.getCandidat() == null ? null : r.getCandidat().getDateNaissance(),
                    r.getCandidat() == null ? null : r.getCandidat().getLieuNaissance(), r.getAnnee()));
        }
        for (RelevNoteG r : mongoTemplate.find(avecAnnee, RelevNoteG.class)) {
            resultats.add(resultat("g", r.getId(), r.getCandidat() == null ? null : r.getCandidat().getNumeroTable(),
                    r.getCandidat() == null ? null : r.getCandidat().getNomPrenom(),
                    r.getCandidat() == null ? null : r.getCandidat().getDateNaissance(),
                    r.getCandidat() == null ? null : r.getCandidat().getLieuNaissance(), r.getAnnee()));
        }
        for (RelevNoteG1 r : mongoTemplate.find(avecAnnee, RelevNoteG1.class)) {
            resultats.add(resultat("g1", r.getId(), r.getCandidat() == null ? null : r.getCandidat().getNumeroTable(),
                    r.getCandidat() == null ? null : r.getCandidat().getNomPrenom(),
                    r.getCandidat() == null ? null : r.getCandidat().getDateNaissance(),
                    r.getCandidat() == null ? null : r.getCandidat().getLieuNaissance(), r.getAnnee()));
        }
        for (RelevNoteG2 r : mongoTemplate.find(avecAnnee, RelevNoteG2.class)) {
            resultats.add(resultat("g2", r.getId(), r.getCandidat() == null ? null : r.getCandidat().getNumeroTable(),
                    r.getCandidat() == null ? null : r.getCandidat().getNomPrenom(),
                    r.getCandidat() == null ? null : r.getCandidat().getDateNaissance(),
                    r.getCandidat() == null ? null : r.getCandidat().getLieuNaissance(), r.getAnnee()));
        }
        for (RelevNoteLPrime1 r : mongoTemplate.find(avecAnnee, RelevNoteLPrime1.class)) {
            resultats.add(resultat("lprime1", r.getId(), r.getCandidat() == null ? null : r.getCandidat().getNumeroTable(),
                    r.getCandidat() == null ? null : r.getCandidat().getNomPrenom(),
                    r.getCandidat() == null ? null : r.getCandidat().getDateNaissance(),
                    r.getCandidat() == null ? null : r.getCandidat().getLieuNaissance(), r.getAnnee()));
        }
        for (RelevNoteL1A r : mongoTemplate.find(avecAnnee, RelevNoteL1A.class)) {
            resultats.add(resultat("l1a", r.getId(), r.getCandidat() == null ? null : r.getCandidat().getNumeroTable(),
                    r.getCandidat() == null ? null : r.getCandidat().getNomPrenom(),
                    r.getCandidat() == null ? null : r.getCandidat().getDateNaissance(),
                    r.getCandidat() == null ? null : r.getCandidat().getLieuNaissance(), r.getAnnee()));
        }
        for (RelevNoteL1B r : mongoTemplate.find(avecAnnee, RelevNoteL1B.class)) {
            resultats.add(resultat("l1b", r.getId(), r.getCandidat() == null ? null : r.getCandidat().getNumeroTable(),
                    r.getCandidat() == null ? null : r.getCandidat().getNomPrenom(),
                    r.getCandidat() == null ? null : r.getCandidat().getDateNaissance(),
                    r.getCandidat() == null ? null : r.getCandidat().getLieuNaissance(), r.getAnnee()));
        }
        for (RelevNoteL2 r : mongoTemplate.find(avecAnnee, RelevNoteL2.class)) {
            resultats.add(resultat("l2", r.getId(), r.getCandidat() == null ? null : r.getCandidat().getNumeroTable(),
                    r.getCandidat() == null ? null : r.getCandidat().getNomPrenom(),
                    r.getCandidat() == null ? null : r.getCandidat().getDateNaissance(),
                    r.getCandidat() == null ? null : r.getCandidat().getLieuNaissance(), r.getAnnee()));
        }
        for (RelevNoteS1 r : mongoTemplate.find(avecAnnee, RelevNoteS1.class)) {
            resultats.add(resultat("s1", r.getId(), r.getCandidat() == null ? null : r.getCandidat().getNumeroTable(),
                    r.getCandidat() == null ? null : r.getCandidat().getNomPrenom(),
                    r.getCandidat() == null ? null : r.getCandidat().getDateNaissance(),
                    r.getCandidat() == null ? null : r.getCandidat().getLieuNaissance(), r.getAnnee()));
        }
        for (RelevNoteS2 r : mongoTemplate.find(avecAnnee, RelevNoteS2.class)) {
            resultats.add(resultat("s2", r.getId(), r.getCandidat() == null ? null : r.getCandidat().getNumeroTable(),
                    r.getCandidat() == null ? null : r.getCandidat().getNomPrenom(),
                    r.getCandidat() == null ? null : r.getCandidat().getDateNaissance(),
                    r.getCandidat() == null ? null : r.getCandidat().getLieuNaissance(), r.getAnnee()));
        }
        for (RelevNoteS3 r : mongoTemplate.find(avecAnnee, RelevNoteS3.class)) {
            resultats.add(resultat("s3", r.getId(), r.getCandidat() == null ? null : r.getCandidat().getNumeroTable(),
                    r.getCandidat() == null ? null : r.getCandidat().getNomPrenom(),
                    r.getCandidat() == null ? null : r.getCandidat().getDateNaissance(),
                    r.getCandidat() == null ? null : r.getCandidat().getLieuNaissance(), r.getAnnee()));
        }
        for (RelevNoteS4 r : mongoTemplate.find(avecAnnee, RelevNoteS4.class)) {
            resultats.add(resultat("s4", r.getId(), r.getCandidat() == null ? null : r.getCandidat().getNumeroTable(),
                    r.getCandidat() == null ? null : r.getCandidat().getNomPrenom(),
                    r.getCandidat() == null ? null : r.getCandidat().getDateNaissance(),
                    r.getCandidat() == null ? null : r.getCandidat().getLieuNaissance(), r.getAnnee()));
        }
        for (RelevNoteS5 r : mongoTemplate.find(avecAnnee, RelevNoteS5.class)) {
            resultats.add(resultat("s5", r.getId(), r.getCandidat() == null ? null : r.getCandidat().getNumeroTable(),
                    r.getCandidat() == null ? null : r.getCandidat().getNomPrenom(),
                    r.getCandidat() == null ? null : r.getCandidat().getDateNaissance(),
                    r.getCandidat() == null ? null : r.getCandidat().getLieuNaissance(), r.getAnnee()));
        }
        for (RelevNoteT1 r : mongoTemplate.find(avecAnnee, RelevNoteT1.class)) {
            resultats.add(resultat("t1", r.getId(), r.getCandidat() == null ? null : r.getCandidat().getNumeroTable(),
                    r.getCandidat() == null ? null : r.getCandidat().getNomPrenom(),
                    r.getCandidat() == null ? null : r.getCandidat().getDateNaissance(),
                    r.getCandidat() == null ? null : r.getCandidat().getLieuNaissance(), r.getAnnee()));
        }
        for (RelevNoteT2 r : mongoTemplate.find(avecAnnee, RelevNoteT2.class)) {
            resultats.add(resultat("t2", r.getId(), r.getCandidat() == null ? null : r.getCandidat().getNumeroTable(),
                    r.getCandidat() == null ? null : r.getCandidat().getNomPrenom(),
                    r.getCandidat() == null ? null : r.getCandidat().getDateNaissance(),
                    r.getCandidat() == null ? null : r.getCandidat().getLieuNaissance(), r.getAnnee()));
        }

        // ---- Séries "2ème partie" : pas de champ année ----
        for (ReleveA1DeuxiemePartie r : mongoTemplate.find(sansAnnee, ReleveA1DeuxiemePartie.class)) {
            resultats.add(resultat("a1-2eme-partie", r.getId(), r.getCandidat() == null ? null : r.getCandidat().getNumeroTable(),
                    r.getCandidat() == null ? null : r.getCandidat().getNomPrenom(),
                    r.getCandidat() == null ? null : r.getCandidat().getDateNaissance(),
                    r.getCandidat() == null ? null : r.getCandidat().getLieuNaissance(), null));
        }
        for (ReleveA2DeuxiemePartie r : mongoTemplate.find(sansAnnee, ReleveA2DeuxiemePartie.class)) {
            resultats.add(resultat("a2-2eme-partie", r.getId(), r.getCandidat() == null ? null : r.getCandidat().getNumeroTable(),
                    r.getCandidat() == null ? null : r.getCandidat().getNomPrenom(),
                    r.getCandidat() == null ? null : r.getCandidat().getDateNaissance(),
                    r.getCandidat() == null ? null : r.getCandidat().getLieuNaissance(), null));
        }
        for (ReleveA3DeuxiemePartie r : mongoTemplate.find(sansAnnee, ReleveA3DeuxiemePartie.class)) {
            resultats.add(resultat("a3-2eme-partie", r.getId(), r.getCandidat() == null ? null : r.getCandidat().getNumeroTable(),
                    r.getCandidat() == null ? null : r.getCandidat().getNomPrenom(),
                    r.getCandidat() == null ? null : r.getCandidat().getDateNaissance(),
                    r.getCandidat() == null ? null : r.getCandidat().getLieuNaissance(), null));
        }
        for (ReleveC2emePartie r : mongoTemplate.find(sansAnnee, ReleveC2emePartie.class)) {
            resultats.add(resultat("c-2eme-partie", r.getId(), r.getCandidat() == null ? null : r.getCandidat().getNumeroTable(),
                    r.getCandidat() == null ? null : r.getCandidat().getNomPrenom(),
                    r.getCandidat() == null ? null : r.getCandidat().getDateNaissance(),
                    r.getCandidat() == null ? null : r.getCandidat().getLieuNaissance(), null));
        }
        for (ReleveDDeuxiemePartie r : mongoTemplate.find(sansAnnee, ReleveDDeuxiemePartie.class)) {
            resultats.add(resultat("d-2eme-partie", r.getId(), r.getCandidat() == null ? null : r.getCandidat().getNumeroTable(),
                    r.getCandidat() == null ? null : r.getCandidat().getNomPrenom(),
                    r.getCandidat() == null ? null : r.getCandidat().getDateNaissance(),
                    r.getCandidat() == null ? null : r.getCandidat().getLieuNaissance(), null));
        }
        for (ReleveF1DeuxiemePartie r : mongoTemplate.find(sansAnnee, ReleveF1DeuxiemePartie.class)) {
            resultats.add(resultat("f1-2eme-partie", r.getId(), r.getCandidat() == null ? null : r.getCandidat().getNumeroTable(),
                    r.getCandidat() == null ? null : r.getCandidat().getNomPrenom(),
                    r.getCandidat() == null ? null : r.getCandidat().getDateNaissance(),
                    r.getCandidat() == null ? null : r.getCandidat().getLieuNaissance(), null));
        }

        return resultats;
    }

    // ---------------------------------------------------------------
    // Construction de la requête MongoDB
    // ---------------------------------------------------------------

    private Query construireQuery(
            String numeroTable, Integer annee, String nom, String prenom,
            LocalDate dateNaissance, String lieuNaissance, boolean supporteAnnee
    ) {
        List<Criteria> criteres = new ArrayList<>();
        if (estRempli(numeroTable)) {
            criteres.add(Criteria.where("candidat.numeroTable")
                    .regex("^" + Pattern.quote(numeroTable.trim()) + "$", "i"));
        }
        if (supporteAnnee && annee != null) {
            criteres.add(Criteria.where("annee").is(annee));
        }
        if (estRempli(nom)) {
            criteres.add(Criteria.where("candidat.nomPrenom").regex(Pattern.quote(nom.trim()), "i"));
        }
        if (estRempli(prenom)) {
            criteres.add(Criteria.where("candidat.nomPrenom").regex(Pattern.quote(prenom.trim()), "i"));
        }
        if (dateNaissance != null) {
            criteres.add(Criteria.where("candidat.dateNaissance").is(dateNaissance));
        }
        if (estRempli(lieuNaissance)) {
            criteres.add(Criteria.where("candidat.lieuNaissance").regex(Pattern.quote(lieuNaissance.trim()), "i"));
        }

        Query query = new Query();
        if (!criteres.isEmpty()) {
            query.addCriteria(new Criteria().andOperator(criteres.toArray(new Criteria[0])));
        }
        query.limit(TAILLE_MAX_PAR_SERIE);
        return query;
    }

    private boolean estRempli(String valeur) {
        return valeur != null && !valeur.isBlank();
    }

    private RechercheReleveResultat resultat(
            String serieKey, String id, String numeroTable, String nomPrenom,
            LocalDate dateNaissance, String lieuNaissance, Integer annee
    ) {
        return new RechercheReleveResultat(serieKey, id, numeroTable, nomPrenom, dateNaissance, lieuNaissance, annee);
    }
}
