package com.officedubac.project.expressionBesoin;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

// Archivage des expressions de besoin, isolé de ExpressionBesoinService (création, validation,
// traitement) : c'est une préoccupation transverse, indépendante du statut d'origine
// (EN_ATTENTE, VALIDEE, REJETEE, TRAITEE) — une EB archivée sort de toutes les listes actives
// sans être supprimée, tant qu'elle n'est pas déjà liée à un mandatement.
@Service
@RequiredArgsConstructor
public class ExpressionBesoinArchiveService {

    private final ExpressionBesoinRepository expressionBesoinRepo;

    public ExpressionBesoin archiver(String id) {
        ExpressionBesoin eb = expressionBesoinRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Expression de besoin introuvable : " + id));

        if (eb.isArchivee())
            throw new RuntimeException("Cette expression de besoin est déjà archivée");
        if (eb.isUtiliseePourMandatement())
            throw new RuntimeException("Cette expression de besoin est déjà liée à un mandatement, elle ne peut pas être archivée");

        eb.setArchivee(true);
        eb.setArchiveePar(getUsername());
        eb.setDateArchivage(LocalDateTime.now());
        return expressionBesoinRepo.save(eb);
    }

    // Onglet dédié (Directeur/Admin) : toutes les EB archivées, quel que soit leur statut
    // d'origine — elles ne figurent plus jamais dans les autres listes une fois archivées.
    public List<ExpressionBesoin> getArchivees() {
        // nullsLast : une EB archivée sans dateArchivage (ne devrait plus arriver, cf.
        // ExpressionBesoinArchiveMigration) ne doit pas faire planter le tri pour autant.
        return expressionBesoinRepo.findAll().stream()
                .filter(ExpressionBesoin::isArchivee)
                .sorted(Comparator.comparing(ExpressionBesoin::getDateArchivage,
                        Comparator.nullsLast(Comparator.naturalOrder())).reversed())
                .toList();
    }

    private String getUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
