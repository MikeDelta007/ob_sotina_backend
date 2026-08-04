package com.officedubac.project.modules.A4.model;

/**
 * Le bloc d'identité du candidat sur le formulaire A4 est un encadré libre
 * (pas de lignes à blancs individuelles comme sur A1/A3) : une légende à
 * gauche ("1e ligne : Mr/Mme/Mlle Prénom NOM, Etabl. d'orig., Nationalité"
 * / "2e ligne : date et lieu de naissance" / "3e ligne : Série, Options et
 * Langues choisies, Epr. fac., Educ. phys." / "4e ligne : EAF") explique ce
 * qui doit être écrit à la main dans l'encadré à droite. On modélise donc
 * ce bloc comme 4 lignes de texte libre correspondant chacune à une ligne
 * du formulaire, plutôt que des champs unitaires.
 */
public class Candidat {

    /** Mr/Mme/Mlle + Prénom + NOM + Etabl. d'orig. ou Ind. + Nationalité */
    private String ligne1IdentiteEtablissement;

    /** Date et lieu de naissance */
    private String ligne2Naissance;

    /** Série, Options et Langues choisies, Epr. facultative, Educ. physique (Apte/Inapte) */
    private String ligne3SerieOptions;

    /** EAF : matières subies en session, centre, notes/20, établissement d'origine */
    private String ligne4Eaf;

    public String getLigne1IdentiteEtablissement() { return ligne1IdentiteEtablissement; }
    public void setLigne1IdentiteEtablissement(String v) { this.ligne1IdentiteEtablissement = v; }

    public String getLigne2Naissance() { return ligne2Naissance; }
    public void setLigne2Naissance(String ligne2Naissance) { this.ligne2Naissance = ligne2Naissance; }

    public String getLigne3SerieOptions() { return ligne3SerieOptions; }
    public void setLigne3SerieOptions(String ligne3SerieOptions) { this.ligne3SerieOptions = ligne3SerieOptions; }

    public String getLigne4Eaf() { return ligne4Eaf; }
    public void setLigne4Eaf(String ligne4Eaf) { this.ligne4Eaf = ligne4Eaf; }
}
