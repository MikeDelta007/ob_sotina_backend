package com.officedubac.project.ticketRestaurant;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class MotifTicketRestaurantDataInitializer implements CommandLineRunner {

    private final MotifTicketRestaurantRepository motifRepo;

    // Libellé → rôle(s) concerné(s) (vide = visible de tous) ; chaque motif n'est proposé qu'aux
    // comptes ayant l'un de ces rôles (profil principal ou droit supplémentaire).
    private static final Map<String, List<String>> MOTIFS_DEFAUT = new java.util.LinkedHashMap<>() {{
        put("Scolarité – Réception dossiers bac", List.of("SCOLARITE"));
        put("Planification – Création jury bac", List.of("PLANIFICATION"));
        put("Pédagogie – Confection épreuves", List.of("PEDAGOGIE"));
        put("Scolarité – Remise des PJ", List.of("SCOLARITE"));
        put("Scolarité – Réception de PJ", List.of("SCOLARITE"));
        put("Diplôme (CHEF DE SERVICE DIPLOMME) – Impression diplôme", List.of("CHEF_SERVICE_DIPLOME"));
        put("CSA – Formation superviseurs", List.of("CSA"));
        put("CSA – Réunion OB", List.of("CSA"));
        put("CSA – Cocktail – Pot départ", List.of("CSA"));
        put("CSA – Team Building", List.of("CSA"));
    }};

    @Override
    public void run(String... args) {
        if (motifRepo.count() == 0) {
            MOTIFS_DEFAUT.forEach((libelle, roles) ->
                    motifRepo.save(MotifTicketRestaurant.builder().libelle(libelle).roles(roles).actif(true).build()));
            log.info("✅ {} motifs de ticket restaurant initialisés", MOTIFS_DEFAUT.size());
        }
    }
}
