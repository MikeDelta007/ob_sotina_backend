package com.officedubac.project.personnel;

import com.officedubac.project.models.User;
import com.officedubac.project.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

// Réinitialise le solde de congés de tous les agents chaque 1er janvier à minuit,
// selon leur typePersonnel (PERMANENT : 30j, PERSONNEL_APPUI : 10j).
@Slf4j
@Component
@RequiredArgsConstructor
public class CongesResetScheduler {

    private final UserRepository userRepository;

    @Scheduled(cron = "0 0 0 1 1 *")
    public void reinitialiserSoldesConges() {
        List<User> users = userRepository.findAll().stream()
                .filter(u -> u.getTypePersonnel() != null)
                .toList();

        users.forEach(u -> u.setSoldeConges(u.getTypePersonnel().joursConges()));
        userRepository.saveAll(users);

        log.info("Réinitialisation annuelle des soldes de congés effectuée pour {} agent(s)", users.size());
    }
}
