package com.officedubac.project.axe;

import com.officedubac.project.models.User;
import com.officedubac.project.personnel.Division;
import com.officedubac.project.personnel.DivisionRepository;
import com.officedubac.project.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.text.Normalizer;

// Autorisation dédiée à la gestion des axes : au-delà des rôles de supervision globale
// (Admin/Directeur/Assistante Directeur/CSA), seul le chef de la division Hygiène et Sécurité
// y a accès — pas n'importe quel chef de service, contrairement au reste de l'application où
// CHEF_SERVICE suffit. Le statut de chef vient toujours de la division (chefServiceId), jamais
// du rôle — ce chef peut avoir n'importe quel rôle applicatif.
@Component("axeAuthService")
@RequiredArgsConstructor
public class AxeAuthorization {

    private final UserRepository userRepository;
    private final DivisionRepository divisionRepository;

    public boolean estChefHygieneSecurite() {
        String login = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByLogin(login).orElse(null);
        if (user == null) return false;

        return divisionRepository.findByActifTrue().stream()
                .filter(d -> estDivisionHygieneSecurite(d.getLibelle()))
                .anyMatch(d -> user.getId().equals(d.getChefServiceId()));
    }

    private boolean estDivisionHygieneSecurite(String libelle) {
        if (libelle == null) return false;
        String normalise = Normalizer.normalize(libelle.toLowerCase(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        return normalise.contains("hygiene") && normalise.contains("securite");
    }
}
