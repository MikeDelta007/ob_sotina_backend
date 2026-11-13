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

import java.time.LocalDateTime;

// Migration au démarrage, à côté de ExpressionBesoinArchiveService : une EB déjà archivée
// (archivee=true) sans dateArchivage fait planter getArchivees(), qui trie avec
// Comparator.comparing(ExpressionBesoin::getDateArchivage) — une valeur null n'y est pas
// comparable. Peut arriver sur une EB archivée par un chemin de code antérieur à l'ajout de ce
// champ, ou via une modification directe en base. Corrige aussi archiveePar par cohérence.
// Écriture directe (sans désérialiser l'entité), idempotent.
@Slf4j
@Component
@Order(45)
@RequiredArgsConstructor
public class ExpressionBesoinArchiveMigration implements CommandLineRunner {

    private final MongoTemplate mongoTemplate;

    private static final String COLLECTION = "eb_expressions_besoin";

    @Override
    public void run(String... args) {
        Query dateManquante = Query.query(Criteria.where("archivee").is(true).and("dateArchivage").is(null));
        long dates = mongoTemplate.updateMulti(dateManquante, Update.update("dateArchivage", LocalDateTime.now()), COLLECTION)
                .getModifiedCount();

        Query auteurManquant = Query.query(Criteria.where("archivee").is(true).and("archiveePar").is(null));
        long auteurs = mongoTemplate.updateMulti(auteurManquant, Update.update("archiveePar", "—"), COLLECTION)
                .getModifiedCount();

        if (dates > 0 || auteurs > 0) {
            log.info("Archivage EB : {} date(s) d'archivage et {} auteur(s) manquant(s) corrigé(s) au démarrage", dates, auteurs);
        }
    }
}
