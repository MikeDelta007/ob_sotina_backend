package com.officedubac.project.expressionBesoin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

// Migration au démarrage : plusieurs champs d'ExpressionBesoin sont des booléens primitifs Java
// (boolean, pas Boolean) — un document où l'un d'eux vaut explicitement null en base (plutôt
// qu'absent ou false) fait planter sa désérialisation avec une NullPointerException d'unboxing.
// getValidees()/getMontantReduit() chargent tous les documents (findAll()) avant de filtrer et
// tombent alors sur ce genre de document corrompu, contrairement à getATraiter()/getTraitees()
// qui filtrent par statut au niveau Mongo et ne le rencontrent jamais — d'où un 500 silencieux
// propre à ces deux écrans. On corrige ici, en écriture directe (sans désérialiser l'entité, pour
// ne pas provoquer la même exception pendant la migration elle-même), tout document où l'un de
// ces champs vaut null — idempotent, sans effet une fois la base assainie.
@Slf4j
@Component
@Order(40)
@RequiredArgsConstructor
public class ExpressionBesoinBooleanNullMigration implements CommandLineRunner {

    private final MongoTemplate mongoTemplate;

    private static final String COLLECTION = "eb_expressions_besoin";
    private static final String[] CHAMPS_BOOLEENS_PRIMITIFS = {
            "requiertSatisfaction", "aFacturePreformat", "beneficiaireMoiMeme",
            "validationCsa", "rejetCsa", "validationDirecteur",
            "utiliseePourMandatement", "satisfactionConfirmee", "archivee",
    };

    @Override
    public void run(String... args) {
        long corriges = 0;
        for (String champ : CHAMPS_BOOLEENS_PRIMITIFS) {
            Query requete = Query.query(Criteria.where(champ).is(null));
            Update maj = Update.update(champ, false);
            corriges += mongoTemplate.updateMulti(requete, maj, COLLECTION).getModifiedCount();
        }
        if (corriges > 0) {
            log.info("🔄 {} champ(s) booléen(s) null corrigé(s) en false sur des expressions de besoin au démarrage", corriges);
        }
    }
}
