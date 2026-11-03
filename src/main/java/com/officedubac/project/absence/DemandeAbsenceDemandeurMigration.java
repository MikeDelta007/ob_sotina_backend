package com.officedubac.project.absence;

import com.officedubac.project.models.User;
import com.officedubac.project.personnel.Personnel;
import com.officedubac.project.personnel.PersonnelRepository;
import com.officedubac.project.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Optional;

// Migration au démarrage : avant la bascule du module Congés/Absences sur la fiche Personnel
// (plutôt que le compte User) comme identité du bénéficiaire, demandeurId stockait l'id du
// compte. Les demandes créées avant ce changement restent donc invisibles pour leur bénéficiaire
// (mesDemandes filtre maintenant par id Personnel). On corrige ici chaque demande dont
// demandeurId est encore un id de compte lié à une fiche personnel, en la faisant pointer vers
// cette fiche — idempotent, sans effet une fois toutes les demandes migrées.
@Slf4j
@Component
@Order(20)
@RequiredArgsConstructor
public class DemandeAbsenceDemandeurMigration implements CommandLineRunner {

    private final DemandeAbsenceRepository demandeRepo;
    private final UserRepository userRepository;
    private final PersonnelRepository personnelRepository;

    @Override
    public void run(String... args) {
        int corrigees = 0;
        for (DemandeAbsence demande : demandeRepo.findAll()) {
            if (personnelRepository.existsById(demande.getDemandeurId())) continue;

            Optional<User> compte = userRepository.findById(demande.getDemandeurId());
            if (compte.isEmpty() || compte.get().getPersonnelId() == null) continue;

            Personnel personnel = personnelRepository.findById(compte.get().getPersonnelId()).orElse(null);
            if (personnel == null) continue;

            demande.setDemandeurId(personnel.getId());
            demande.setDemandeurNom((personnel.getFirstname() + " " + personnel.getLastname()).trim());
            if (personnel.getDivision() != null) {
                demande.setDivisionId(personnel.getDivision().getId());
            }
            demandeRepo.save(demande);
            corrigees++;
        }
        if (corrigees > 0) {
            log.info("🔄 {} demande(s) de congé/absence migrée(s) vers l'id de fiche personnel au démarrage", corrigees);
        }
    }
}
