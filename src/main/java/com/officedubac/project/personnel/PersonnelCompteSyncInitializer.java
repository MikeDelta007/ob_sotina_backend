package com.officedubac.project.personnel;

import com.officedubac.project.models.User;
import com.officedubac.project.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

// Filet de sécurité au démarrage : un compte garde sa propre copie embarquée de sa fiche
// Personnel (User.personnel), utilisée au login et par tout ce qui lit le compte plutôt que la
// fiche elle-même. Si cette copie a dérivé (modification de la fiche par un chemin qui ne passe
// pas par PersonnelResource.modifierPersonnel, ex. import Excel, script direct en base...), ce
// correctif resynchronise chaque compte sur l'état actuel de sa fiche — idempotent, sans effet
// si tout est déjà à jour.
@Slf4j
@Component
@Order(10)
@RequiredArgsConstructor
public class PersonnelCompteSyncInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PersonnelRepository personnelRepo;

    @Override
    public void run(String... args) {
        int corriges = 0;
        for (User u : userRepository.findAll()) {
            if (u.getPersonnelId() == null) continue;
            Personnel actuel = personnelRepo.findById(u.getPersonnelId()).orElse(null);
            if (actuel == null || actuel.equals(u.getPersonnel())) continue;
            u.setPersonnel(actuel);
            userRepository.save(u);
            corriges++;
        }
        if (corriges > 0) {
            log.info("🔄 {} compte(s) resynchronisé(s) avec leur fiche personnel au démarrage", corriges);
        }
    }
}
