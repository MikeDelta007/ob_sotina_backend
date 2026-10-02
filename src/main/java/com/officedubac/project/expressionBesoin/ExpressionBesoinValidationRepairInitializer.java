package com.officedubac.project.expressionBesoin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

// Répare les expressions de besoin clôturées à tort par un bug de ExpressionBesoinService.valider()
// (corrigé ici-même) : quand le montant dépassait le seuil imposant la validation du Directeur, le
// statut passait à VALIDEE dès que CELUI-CI validait, sans jamais exiger celle du CSA — le dossier
// disparaissait alors de la liste "à valider" du CSA tout en affichant encore son étape en attente.
// Cible précisément cet état incohérent (VALIDEE + validationDirecteur sans validationCsa ni rejetCsa)
// et le rouvre en EN_ATTENTE pour que le CSA puisse enfin le traiter — idempotent, ne fait rien une
// fois tous les dossiers ainsi réparés.
@Slf4j
@Component
@RequiredArgsConstructor
public class ExpressionBesoinValidationRepairInitializer implements CommandLineRunner {

    private final MongoTemplate mongoTemplate;

    private static final String COLLECTION = "eb_expressions_besoin";

    @Override
    public void run(String... args) {
        Query query = new Query(Criteria.where("statut").is("VALIDEE")
                .and("validationDirecteur").is(true)
                .and("validationCsa").is(false)
                .and("rejetCsa").ne(true));

        long reparees = mongoTemplate.updateMulti(query, Update.update("statut", "EN_ATTENTE"), COLLECTION)
                .getModifiedCount();

        if (reparees > 0) {
            log.info("🔧 Réparation validation EB : {} dossier(s) rouverts en EN_ATTENTE (validés par le Directeur sans le CSA)", reparees);
        }
    }
}
