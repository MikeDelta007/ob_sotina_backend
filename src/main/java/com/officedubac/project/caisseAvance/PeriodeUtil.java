package com.officedubac.project.caisseAvance;

import java.time.LocalDate;
import java.time.temporal.WeekFields;

/**
 * Filtrage par période (année / mois / semaine ISO) pour les listes et exports
 * des approvisionnements et mandatements.
 */
public class PeriodeUtil {

    private PeriodeUtil() {}

    public static boolean matchPeriode(LocalDate date, Integer annee, Integer mois, Integer semaine) {
        if (annee == null && mois == null && semaine == null) return true;
        if (date == null) return false;
        if (annee != null && date.getYear() != annee) return false;
        if (mois != null && date.getMonthValue() != mois) return false;
        if (semaine != null && date.get(WeekFields.ISO.weekOfWeekBasedYear()) != semaine) return false;
        return true;
    }
}
