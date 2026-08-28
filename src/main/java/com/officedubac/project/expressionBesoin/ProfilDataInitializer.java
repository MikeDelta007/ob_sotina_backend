package com.officedubac.project.expressionBesoin;

import com.officedubac.project.models.Profil;
import com.officedubac.project.models.Role;
import com.officedubac.project.repository.ProfilRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Crée les Profil manquants pour les rôles du workflow "Expression de besoin"
 * (CHEF_SERVICE, CSA, DIRECTEUR, CHEF_COMPTABLE, AGENT_COMPTABLE) — sans Profil
 * existant, aucun compte ne peut se voir attribuer ces rôles (ParametrageService
 * .createUser() exige un Profil déjà en base pour le nom demandé).
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ProfilDataInitializer implements CommandLineRunner {

    private final ProfilRepository profilRepo;

    private static final Role[] ROLES_EB = {
        Role.CHEF_SERVICE, Role.CSA, Role.DIRECTEUR, Role.CHEF_COMPTABLE, Role.AGENT_COMPTABLE
    };

    @Override
    public void run(String... args) {
        for (Role role : ROLES_EB) {
            if (profilRepo.findByName(role.name()) == null) {
                profilRepo.save(Profil.builder().name(role).build());
                log.info("✅ Profil créé pour le rôle {}", role);
            }
        }
    }
}
