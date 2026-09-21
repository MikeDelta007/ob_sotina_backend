package com.officedubac.project.notification;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Nombre d'éléments en attente de l'action de l'utilisateur connecté, par module — utilisé
// pour les badges de notification dans le menu (comme les compteurs de messages non lus).
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationCounts {
    private int conges;
    private int absences;
    private int expressionBesoin;
}
