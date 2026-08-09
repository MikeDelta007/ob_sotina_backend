package com.officedubac.project.modules.A2.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Bloc "EPR. FACULTATIVES" du formulaire : Langue / Dessin, Musique ou Couture.
 * Seuls les points AU-DESSUS de la moyenne (10/20) comptent, et uniquement
 * en cas de mention BIEN ou TRES BIEN (règle imprimée sur le formulaire).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EpreuveFacultative {

    private TypeFacultative type;
    private Integer note;
    private Integer pointsAuDessusMoyenne;
}
