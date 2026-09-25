package com.officedubac.project.notification;

import com.officedubac.project.absence.DemandeAbsenceService;
import com.officedubac.project.absence.TypeAbsence;
import com.officedubac.project.expressionBesoin.ExpressionBesoinService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// Compteurs pour les badges de notification du menu (congés, autorisations d'absence,
// expressions de besoin) : chaque compteur ne reflète que ce qu'il reste à faire pour
// l'utilisateur connecté — les méthodes de service réutilisées (aValider, getAValider,
// getATraiter) filtrent déjà selon son rôle.
@RestController
// Hors de /api/v1/notifications/** : ce chemin est en permitAll dans SecurityConfig (hérité), ce qui
// ferait répondre 500 (et non 401/403) à une requête sans jeton.
@RequestMapping("/api/v1/menu")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class NotificationCountsResource {

    private final DemandeAbsenceService demandeAbsenceService;
    private final ExpressionBesoinService expressionBesoinService;

    @GetMapping("/notification-counts")
    public ResponseEntity<NotificationCounts> counts() {
        int conges = demandeAbsenceService.aValider(TypeAbsence.CONGE).size();
        int absences = demandeAbsenceService.aValider(TypeAbsence.AUTORISATION).size();

        int expressionBesoin = 0;
        if (hasAuthority("CSA") || hasAuthority("DIRECTEUR")) {
            expressionBesoin = expressionBesoinService.getAValider().size();
        } else if (hasAuthority("CHEF_COMPTABLE") || hasAuthority("AGENT_COMPTABLE")) {
            expressionBesoin = expressionBesoinService.getATraiter().size();
        }

        return ResponseEntity.ok(new NotificationCounts(conges, absences, expressionBesoin));
    }

    private boolean hasAuthority(String authority) {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(authority));
    }
}
