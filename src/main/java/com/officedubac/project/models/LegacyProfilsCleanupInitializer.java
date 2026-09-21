package com.officedubac.project.models;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.List;

// Purge les comptes et profils portant un rôle hérité de l'ancien module examens, retiré de
// l'enum Role (voir StringToRoleConverter, qui tolère ces valeurs en les lisant comme null
// plutôt que de planter) — à exécuter aussi en production. Ciblés par leur nom brut (String) et
// non via l'enum Role, puisque ces valeurs n'y existent plus ; idempotent (ne fait rien si plus
// aucun compte/profil ne les porte).
@Slf4j
@Component
@RequiredArgsConstructor
public class LegacyProfilsCleanupInitializer implements CommandLineRunner {

    private final MongoTemplate mongoTemplate;

    private static final List<String> ROLES_LEGACY = List.of(
            "FINANCE_COMPTA", "AGENT_DE_SAISIE", "RECEPTIONNISTE", "SCOLARITE", "DEMSG", "INSPECTEUR_ACADEMIE"
    );

    @Override
    public void run(String... args) {
        long comptesSupprimes = mongoTemplate
                .remove(new Query(Criteria.where("profil.name").in(ROLES_LEGACY)), "user")
                .getDeletedCount();
        long profilsSupprimes = mongoTemplate
                .remove(new Query(Criteria.where("name").in(ROLES_LEGACY)), "profil")
                .getDeletedCount();

        if (comptesSupprimes > 0 || profilsSupprimes > 0) {
            log.info("🧹 Nettoyage rôles legacy {} : {} compte(s) et {} profil(s) supprimé(s)",
                    ROLES_LEGACY, comptesSupprimes, profilsSupprimes);
        }
    }
}
