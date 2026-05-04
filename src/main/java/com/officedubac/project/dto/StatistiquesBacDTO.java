package com.officedubac.project.dto;

import java.util.LinkedHashMap;
import java.util.Map;

public class StatistiquesBacDTO {
    private Map<String, OptionStat> statsByOption = new LinkedHashMap<>();
    private long totalGeneral = 0;

    public Map<String, OptionStat> getStatsByOption() {
        return statsByOption;
    }

    public void setStatsByOption(Map<String, OptionStat> statsByOption) {
        this.statsByOption = statsByOption;
    }

    public long getTotalGeneral() {
        return totalGeneral;
    }

    public void setTotalGeneral(long totalGeneral) {
        this.totalGeneral = totalGeneral;
    }

    // OptionStat comme classe statique imbriquée
    public static class OptionStat {
        private long effectif = 0;
        private long filles = 0;
        private double pourcentageFilles = 0.0;
        private long publicCount = 0;
        private double pourcentagePublic = 0.0;
        private long individuels = 0;
        private double pourcentageIndividuels = 0.0;
        private double poidsRelatif = 0.0;

        // Getters et Setters
        public long getEffectif() { return effectif; }
        public void setEffectif(long effectif) { this.effectif = effectif; }

        public long getFilles() { return filles; }
        public void setFilles(long filles) { this.filles = filles; }

        public double getPourcentageFilles() { return pourcentageFilles; }
        public void setPourcentageFilles(double pourcentageFilles) { this.pourcentageFilles = pourcentageFilles; }

        public long getPublicCount() { return publicCount; }
        public void setPublicCount(long publicCount) { this.publicCount = publicCount; }

        public double getPourcentagePublic() { return pourcentagePublic; }
        public void setPourcentagePublic(double pourcentagePublic) { this.pourcentagePublic = pourcentagePublic; }

        public long getIndividuels() { return individuels; }
        public void setIndividuels(long individuels) { this.individuels = individuels; }

        public double getPourcentageIndividuels() { return pourcentageIndividuels; }
        public void setPourcentageIndividuels(double pourcentageIndividuels) { this.pourcentageIndividuels = pourcentageIndividuels; }

        public double getPoidsRelatif() { return poidsRelatif; }
        public void setPoidsRelatif(double poidsRelatif) { this.poidsRelatif = poidsRelatif; }
    }
}