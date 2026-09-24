package com.officedubac.project.personnel;

import com.officedubac.project.models.User;
import com.officedubac.project.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

// Lien fiche Personnel <-> compte : une fiche n'est plus supprimée à la création d'un compte, le
// compte y est rattaché (User.personnelId). Une personne n'a qu'un seul compte, et une fiche déjà
// rattachée n'est plus proposée à la création d'un compte.
@Component
@RequiredArgsConstructor
public class PersonnelCompteService {

    private final UserRepository userRepository;

    public List<User> comptes() {
        return userRepository.findAll();
    }

    // Vrai si un compte est rattaché à cette fiche : par lien explicite, ou — pour les comptes
    // créés avant ce lien — par même matricule ou même adresse e-mail.
    public boolean aUnCompte(Personnel fiche, List<User> comptes) {
        return comptes.stream().anyMatch(u -> correspond(fiche, u));
    }

    public boolean aUnCompte(Personnel fiche) {
        return aUnCompte(fiche, comptes());
    }

    // Même chose en ignorant un compte (celui qu'on est en train de modifier)
    public boolean aUnAutreCompte(Personnel fiche, String idCompteIgnore) {
        return comptes().stream()
                .filter(u -> !u.getId().equals(idCompteIgnore))
                .anyMatch(u -> correspond(fiche, u));
    }

    private boolean correspond(Personnel fiche, User u) {
        if (fiche.getId() != null && fiche.getId().equals(u.getPersonnelId())) return true;
        Personnel p = u.getPersonnel();
        if (p == null || u.getPersonnelId() != null) return false;
        boolean memeMatricule = notBlank(fiche.getMatricule()) && fiche.getMatricule().trim().equalsIgnoreCase(p.getMatricule() == null ? "" : p.getMatricule().trim());
        boolean memeEmail = notBlank(fiche.getEmail()) && fiche.getEmail().trim().equalsIgnoreCase(p.getEmail() == null ? "" : p.getEmail().trim());
        return memeMatricule || memeEmail;
    }

    private boolean notBlank(String s) {
        return s != null && !s.isBlank();
    }
}
