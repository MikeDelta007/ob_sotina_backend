package com.officedubac.project.services;

import com.officedubac.project.dto.*;
import com.officedubac.project.exception.GlobalHandlerControllerException;
import com.officedubac.project.models.SourceCandidat;
import com.officedubac.project.repository.SourceCandidatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class StatsService
{
    private final MongoTemplate mongoTemplate;

    @Autowired
    private SourceCandidatRepository sourceCandidatRepository;

    public List<OperatorDailyCountDTO> countDailyByOperator(LocalDate startDate, LocalDate endDate, Integer session)
    {

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.plusDays(1).atStartOfDay().minusNanos(1);

        MatchOperation match = Aggregation.match(
                Criteria.where("dateOperation").gte(startDateTime).lte(endDateTime)
                        .and("session").is(session)
        );

        GroupOperation group = Aggregation.group("operator", "dateOperation")
                .sum(ConditionalOperators.when(Criteria.where("decision").is(1)).then(1).otherwise(0))
                .as("accepted")
                .sum(ConditionalOperators.when(Criteria.where("decision").is(2)).then(1).otherwise(0))
                .as("rejected");

        ProjectionOperation project = Aggregation.project()
                .and("_id.operator").as("operator")
                .and("_id.dateOperation").as("dateOperation")
                .andInclude("accepted", "rejected");

        SortOperation sort = Aggregation.sort(Sort.by(Sort.Direction.ASC, "dateOperation"));

        Aggregation aggregation = Aggregation.newAggregation(match, group, project, sort);

        AggregationResults<OperatorDailyCountDTO> results =
                mongoTemplate.aggregate(aggregation, "candidat", OperatorDailyCountDTO.class);

        return results.getMappedResults();
    }

    public List<MapDTO> getStatsParDepartement(int session)
    {
        MatchOperation match = Aggregation.match(Criteria.where("session").is(session));

        // Étape 1 : Groupement par département
        GroupOperation group = Aggregation.group("etablissement.departement.name")
                .count().as("totalCandidats")
                .sum(ConditionalOperators.when(Criteria.where("gender").is("M")).then(1).otherwise(0)).as("totalM")
                .sum(ConditionalOperators.when(Criteria.where("gender").is("F")).then(1).otherwise(0)).as("totalF")
                .sum(ConditionalOperators.when(Criteria.where("decision").is(0)).then(1).otherwise(0)).as("enAttente")
                .sum(ConditionalOperators.when(Criteria.where("decision").is(1)).then(1).otherwise(0)).as("valider")
                .sum(ConditionalOperators.when(Criteria.where("decision").is(2)).then(1).otherwise(0)).as("rejeter");

        // Étape 2 : Projection
        ProjectionOperation project = Aggregation.project()
                .and("_id").as("departement")
                .andInclude("totalCandidats", "totalM", "totalF", "enAttente", "valider", "rejeter");

        // Étape 3 : Tri
        SortOperation sort = Aggregation.sort(Sort.Direction.ASC, "departement");

        // Création du pipeline d'agrégation
        Aggregation aggregation = Aggregation.newAggregation(match, group, project, sort);

        // Exécution de l'agrégation
        AggregationResults<MapDTO> results = mongoTemplate.aggregate(aggregation, "candidat", MapDTO.class);

        // Retourner les résultats mappés
        return results.getMappedResults();
    }


    public GlobalStatVCDTO getGlobalStatVC(int session) {
        // Étape 0 : Filtrage par session
        MatchOperation match = Aggregation.match(Criteria.where("session").is(session));

        // Étape 1 : Groupement global (somme des champs numériques)
        GroupOperation group = Aggregation.group()
                .sum("count_1000_OB").as("vOB")
                .sum("count_5000").as("v5000")
                .sum("count_1000_EF").as("v100EF");

        // Étape 2 : Projection (renommer ou inclure les champs désirés)
        ProjectionOperation project = Aggregation.project()
                .andInclude("vOB", "v5000", "v100EF");

        // Création du pipeline d'agrégation
        Aggregation aggregation = Aggregation.newAggregation(match, group, project);

        // Exécution de l'agrégation
        GlobalStatVCDTO result = mongoTemplate.aggregate(
                aggregation, "compte_droits_inscription", GlobalStatVCDTO.class
        ).getUniqueMappedResult();

        // Retour du résultat unique (ou null si vide)
        return result != null ? result : new GlobalStatVCDTO(0, 0, 0);
    }

    public GlobalStatDTO getGlobalStat(int session) {
        // Étape 0 : Filtrage par session
        MatchOperation match = Aggregation.match(Criteria.where("session").is(session));

        // Étape 1 : Groupement global (pas par département)
        GroupOperation group = Aggregation.group()
                .count().as("candidats")
                .sum(ConditionalOperators.when(Criteria.where("gender").is("M")).then(1).otherwise(0)).as("male")
                .sum(ConditionalOperators.when(Criteria.where("gender").is("F")).then(1).otherwise(0)).as("female");

        // Étape 2 : Projection pour renommer proprement
        ProjectionOperation project = Aggregation.project()
                .andInclude("candidats", "male", "female");

        // Création du pipeline d'agrégation
        Aggregation aggregation = Aggregation.newAggregation(match, group, project);

        // Exécution de l'agrégation
        GlobalStatDTO result = mongoTemplate.aggregate(aggregation, "candidat", GlobalStatDTO.class)
                .getUniqueMappedResult();

        // Retour du résultat unique (ou null si vide)
        return result != null ? result : new GlobalStatDTO(0, 0, 0);
    }


    public List<StatAcademieDTO> getStatsParAcademie(int session)
    {
        MatchOperation match = Aggregation.match(Criteria.where("session").is(session));

        // Étape 1 : Groupement par département
        GroupOperation group = Aggregation.group("etablissement.inspectionAcademie.code")
                .sum(ConditionalOperators.when(Criteria.where("gender").is("M")).then(1).otherwise(0)).as("male")
                .sum(ConditionalOperators.when(Criteria.where("gender").is("F")).then(1).otherwise(0)).as("female");

        // Étape 2 : Projection
        ProjectionOperation project = Aggregation.project()
                .and("_id").as("inspectionAcademie")
                .andInclude( "male", "female");

        // Étape 3 : Tri
        SortOperation sort = Aggregation.sort(Sort.Direction.ASC, "inspectionAcademie");

        // Création du pipeline d'agrégation
        Aggregation aggregation = Aggregation.newAggregation(match, group, project, sort);

        // Exécution de l'agrégation
        AggregationResults<StatAcademieDTO> results = mongoTemplate.aggregate(aggregation, "candidat", StatAcademieDTO.class);

        // Retourner les résultats mappés
        return results.getMappedResults();
    }

    public List<StatHandicapDTO> getStatsParHandicap(int session)
    {
        MatchOperation match = Aggregation.match(Criteria.where("session").is(session));

        // Étape 1 : Groupement par département
        GroupOperation group = Aggregation.group("type_handicap")
                .sum(ConditionalOperators.when(Criteria.where("gender").is("M")).then(1).otherwise(0)).as("male")
                .sum(ConditionalOperators.when(Criteria.where("gender").is("F")).then(1).otherwise(0)).as("female");

        // Étape 2 : Projection
        ProjectionOperation project = Aggregation.project()
                .and("_id").as("handicap")
                .andInclude("male", "female");

        // Étape 3 : Tri
        SortOperation sort = Aggregation.sort(Sort.Direction.ASC, "handicap");

        // Création du pipeline d'agrégation
        Aggregation aggregation = Aggregation.newAggregation(match, group, project, sort);

        // Exécution de l'agrégation
        AggregationResults<StatHandicapDTO> results = mongoTemplate.aggregate(aggregation, "candidat", StatHandicapDTO.class);

        // Retourner les résultats mappés
        return results.getMappedResults();
    }

    public List<StatSerieDTO> getStatsParSerie(int session)
    {
        MatchOperation match = Aggregation.match(Criteria.where("session").is(session));

        // Étape 1 : Groupement par département
        GroupOperation group = Aggregation.group("serie.code")
                .sum(ConditionalOperators.when(Criteria.where("gender").is("M")).then(1).otherwise(0)).as("male")
                .sum(ConditionalOperators.when(Criteria.where("gender").is("F")).then(1).otherwise(0)).as("female");

        // Étape 2 : Projection
        ProjectionOperation project = Aggregation.project()
                .and("_id").as("serie")
                .andInclude("male", "female");

        // Étape 3 : Tri
        SortOperation sort = Aggregation.sort(Sort.Direction.ASC, "serie");

        // Création du pipeline d'agrégation
        Aggregation aggregation = Aggregation.newAggregation(match, group, project, sort);

        // Exécution de l'agrégation
        AggregationResults<StatSerieDTO> results = mongoTemplate.aggregate(aggregation, "candidat", StatSerieDTO.class);

        // Retourner les résultats mappés
        return results.getMappedResults();
    }


    public StatistiquesBacDTO calculerStatistiques()
    {
        StatistiquesBacDTO stats = new StatistiquesBacDTO();

        // Récupérer tous les candidats (vous pouvez optimiser avec des requêtes MongoDB)
        List<SourceCandidat> tousLesCandidats = sourceCandidatRepository.findAll();
        long totalGeneral = tousLesCandidats.size();
        stats.setTotalGeneral(totalGeneral);

        // Liste des séries
        String[] seriesOrder = {"STEG", "F6", "T1", "T2", "STIDD", "L'1", "L1A", "L1B", "L2", "LA", "L-AR",
                "S1", "S1A", "S2", "S2A", "S3", "S4", "S5"};

        // Calculer les stats par série
        for (String serie : seriesOrder) {
            List<SourceCandidat> candidatsSerie = tousLesCandidats.stream()
                    .filter(c -> serie.equals(c.getSerie()))
                    .toList();

            long effectif = candidatsSerie.size();
            if (effectif > 0) {
                long filles = candidatsSerie.stream()
                        .filter(c -> "F".equals(c.getGender()))
                        .count();

                long publicCount = candidatsSerie.stream()
                        .filter(c -> {
                            String etab = c.getEtablissement();
                            return etab != null && (etab.equalsIgnoreCase("public") ||
                                    etab.equals("Public") || etab.equals("PUBLIC"));
                        })
                        .count();

                long individuels = effectif - publicCount;

                StatistiquesBacDTO.OptionStat optionStat = new StatistiquesBacDTO.OptionStat();
                optionStat.setEffectif(effectif);
                optionStat.setFilles(filles);
                optionStat.setPourcentageFilles(filles * 100.0 / effectif);
                optionStat.setPublicCount(publicCount);
                optionStat.setPourcentagePublic(publicCount * 100.0 / effectif);
                optionStat.setIndividuels(individuels);
                optionStat.setPourcentageIndividuels(individuels * 100.0 / effectif);
                optionStat.setPoidsRelatif(totalGeneral > 0 ? (effectif * 100.0 / totalGeneral) : 0);

                stats.getStatsByOption().put(serie, optionStat);
            } else {
                // Si aucun candidat pour cette série, créer un objet vide
                StatistiquesBacDTO.OptionStat optionStat = new StatistiquesBacDTO.OptionStat();
                optionStat.setEffectif(0);
                optionStat.setFilles(0);
                optionStat.setPourcentageFilles(0);
                optionStat.setPublicCount(0);
                optionStat.setPourcentagePublic(0);
                optionStat.setIndividuels(0);
                optionStat.setPourcentageIndividuels(0);
                optionStat.setPoidsRelatif(0);
                stats.getStatsByOption().put(serie, optionStat);
            }
        }

        return stats;
    }









}
