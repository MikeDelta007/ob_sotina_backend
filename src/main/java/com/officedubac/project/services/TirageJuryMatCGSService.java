package com.officedubac.project.services;

import com.officedubac.project.dto.*;
import com.officedubac.project.models.*;
import com.officedubac.project.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class TirageJuryMatCGSService
{
    private final SourceCandidatRepository sourceCandidatRepository;

    private final SourceCandidatCGSRepository sourceCandidatCGSRepository;

    private final RepartitionTirageCEPRepository repartitionTirageCEPRepository;

    private final RepartitionTirageCGSRepository repartitionTirageCGSRepository;

    private final RepartitionTirageCSRepository repartitionTirageCSRepository;

    private final FusionRepartitionTirageRepository fusionRepartitionTirageRepository;

    private final HoraireRequestRepository horaireRequestRepository;

    private final RegleMatiereRepository regleMatiereRepository;

    private final RegleMatiereCGSRepository regleMatiereCGSRepository;

    private final MongoTemplate mongoTemplate;


    public List<String> findDistinctSeriesByCentre(String centre, String discipline)
    {
        Query query = new Query();
        query.addCriteria(Criteria.where("centreCompo").is(centre).and("discipline").is(discipline));

        return mongoTemplate.findDistinct(query, "serie", SourceCandidatCGS.class, String.class);
    }

    public List<RepartitionTirageCGSDTO> repartitionParCGS()
    {

        // 🔹 charger les règles
        List<RegleMatiereCGS> regles = regleMatiereCGSRepository.findAll();

        // 🔹 indexer les règles par discipline (valeur)
        Map<String, List<RegleMatiereCGS>> reglesParValeur =
                regles.stream()
                        .collect(Collectors.groupingBy(RegleMatiereCGS::getValeur));

        // 🔹 récupérer les candidats
        List<SourceCandidatCGS> candidats = sourceCandidatCGSRepository.findAll();
        System.out.println(candidats.size());

        // 🔹 groupement centre + discipline
        Map<String, Map<String, List<SourceCandidatCGS>>> candidatsParCentre =
                candidats.stream()
                        .collect(Collectors.groupingBy(
                                SourceCandidatCGS::getCentreCompo,
                                Collectors.groupingBy(SourceCandidatCGS::getDiscipline)
                        ));

        // 🔹 nettoyage ancienne data
        repartitionTirageCGSRepository.deleteAll();

        List<RepartitionTirageCGS> entities = new ArrayList<>();
        List<RepartitionTirageCGSDTO> dtos = new ArrayList<>();


        try {

            for (Map.Entry<String, Map<String, List<SourceCandidatCGS>>> centreEntry : candidatsParCentre.entrySet()) {

                String centreEcrit = centreEntry.getKey();


                for (Map.Entry<String, List<SourceCandidatCGS>> disciplineEntry : centreEntry.getValue().entrySet()) {

                    String discipline = disciplineEntry.getKey();
                    List<SourceCandidatCGS> groupe = disciplineEntry.getValue();
                    List<String> series = findDistinctSeriesByCentre(centreEcrit, discipline);

                    if (groupe.isEmpty()) continue;

                    String academia = groupe.get(0).getAcademia();
                    Long session = groupe.get(0).getSession();
                    long effectif = groupe.size();

                    // 🔹 init compteurs
                    Map<String, GroupeMatiereCGS> compteurs = new HashMap<>();

                    regles.forEach(r ->
                            compteurs.put(r.getValeur(), new GroupeMatiereCGS(0.0, 0.0))
                    );

                    double effectif1ere = 0;
                    double effectifTle = 0;
                    // 🔹 calcul
                    for (SourceCandidatCGS c : groupe)
                    {
                        List<RegleMatiereCGS> rules = reglesParValeur.get(c.getDiscipline());
                        if (rules == null) continue;

                        for (RegleMatiereCGS r : rules)
                        {
                            if (!c.getLevel().equalsIgnoreCase(r.getLevel())) continue;

                            if ("PREMIERE".equalsIgnoreCase(c.getLevel())) {
                                effectif1ere++;
                            } else if ("TERMINALE".equalsIgnoreCase(c.getLevel())) {
                                effectifTle++;
                            }
                        }
                    }

                    // 🔹 DTO
                    RepartitionTirageCGSDTO dto = RepartitionTirageCGSDTO.builder()
                            .session(session)
                            .centreEcrit(centreEcrit)
                            .academia(academia)
                            .effectif(effectif)
                            .discipline(discipline)
                            .eff1ere(effectif1ere)
                            .effTle(effectifTle)
                            .build();

                    dtos.add(dto);

                    // 🔹 ENTITY
                    RepartitionTirageCGS entity = RepartitionTirageCGS.builder()
                            .session(session)
                            .centreEcrit(centreEcrit)
                            .academia(academia)
                            .effectif(effectif)
                            .discipline(discipline)
                            .series(series)
                            .eff1ere(effectif1ere)
                            .effTle(effectifTle)
                            .build();
                    entities.add(entity);
                }
            }

        } catch (Exception e) {
            System.out.println("Erreur répartition CGS : " + e.getMessage());
            e.printStackTrace();
        }

        // 🔹 sauvegarde
        repartitionTirageCGSRepository.saveAll(entities);

        // 🔹 tri final
        return dtos.stream()
                .sorted(Comparator.comparing(RepartitionTirageCGSDTO::getCentreEcrit))
                .toList();
    }


    public Map<String, List<RepartitionTirageCGS>> getAllRepTirage()
    {
        List<RepartitionTirageCGS> allRepTirage = repartitionTirageCGSRepository.findAll();
        return allRepTirage
                .stream()
                .filter(p -> p.getAcademia() != null)
                .collect(Collectors.groupingBy(RepartitionTirageCGS::getAcademia));
    }


    public Page<SourceCandidatCGSDTO> getListCandidats(int page, int size)
    {

        Pageable pageable = PageRequest.of(page, size);

        // 1️⃣ Récupérer la page de candidats depuis Mongo
        Page<SourceCandidatCGS> candidats = sourceCandidatCGSRepository.findAll(pageable);

        // 2️⃣ Hydrater chaque candidat avec son Sujet
        List<SourceCandidatCGSDTO> dtos = candidats.stream().map(c -> {

            SourceCandidatCGSDTO dto = new SourceCandidatCGSDTO();

            dto.setFirstname(c.getFirstname());
            dto.setLastname(c.getLastname());
            dto.setDate_birth(c.getDate_birth());
            dto.setPlace_birth(c.getPlace_birth());
            dto.setSession(c.getSession());
            dto.setGender(c.getGender());
            dto.setSerie(c.getSerie());
            dto.setSession(c.getSession());
            dto.setLevel(c.getLevel());
            dto.setAcademia(c.getAcademia());
            dto.setCentreCompo(c.getCentreCompo());
            dto.setEtab(c.getEtab());
            return dto;
        }).toList();

        // 3️⃣ Compter le total pour la pagination
        long total = sourceCandidatCGSRepository.count();

        // 4️⃣ Construire la Page
        return new PageImpl<>(dtos, pageable, total);
    }

    public Map<String, List<RepartitionTirageCGS>> getRepCGSByAcademie()
    {
        List<RepartitionTirageCGS> allUsers = repartitionTirageCGSRepository.findAll();
        return allUsers
                .stream()
                .filter(p -> p.getAcademia() != null)
                .collect(Collectors.groupingBy(RepartitionTirageCGS::getAcademia));
    }



    public List<Map<String, Object>> getCentreSummaryAllAcademies()
    {
        List<RepartitionTirageCGS> tirages = repartitionTirageCGSRepository.findAll();

        // 🔹 1. Grouper par académie
        Map<String, Map<String, List<RepartitionTirageCGS>>> data =
                tirages.stream()
                        .collect(Collectors.groupingBy(
                                RepartitionTirageCGS::getAcademia,
                                Collectors.groupingBy(RepartitionTirageCGS::getCentreEcrit)
                        ));

        List<Map<String, Object>> result = new ArrayList<>();

        // 🔹 2. Parcours académies
        for (Map.Entry<String, Map<String, List<RepartitionTirageCGS>>> acadEntry : data.entrySet()) {

            String academia = acadEntry.getKey();
            Map<String, List<RepartitionTirageCGS>> centresMap = acadEntry.getValue();

            Map<String, Object> academiaMap = new HashMap<>();
            academiaMap.put("academia", academia);

            List<Map<String, Object>> centresList = new ArrayList<>();

            // 🔹 3. Parcours centres
            for (Map.Entry<String, List<RepartitionTirageCGS>> centreEntry : centresMap.entrySet()) {

                String centre = centreEntry.getKey();
                List<RepartitionTirageCGS> tiragesCentre = centreEntry.getValue();

                Map<String, Object> centreMap = new HashMap<>();
                centreMap.put("centreEcrit", centre);

                List<Map<String, Object>> disciplinesList = new ArrayList<>();

                // 🔹 4. Parcours disciplines
                for (RepartitionTirageCGS r : tiragesCentre) {

                    Map<String, Object> disciplineMap = new HashMap<>();

                    long eff1 = r.getEff1ere() != null ? r.getEff1ere().longValue() : 0;
                    long effT = r.getEffTle() != null ? r.getEffTle().longValue() : 0;

                    disciplineMap.put("discipline", r.getDiscipline());
                    disciplineMap.put("eff1ere", eff1);
                    disciplineMap.put("effTle", effT);
                    disciplineMap.put("effectif", eff1 + effT);

                    disciplinesList.add(disciplineMap);
                }

                // 🔹 (optionnel) tri des disciplines
                disciplinesList.sort(Comparator.comparing(d -> (String) d.get("discipline")));

                centreMap.put("disciplines", disciplinesList);
                centresList.add(centreMap);
            }

            // 🔹 (optionnel) tri des centres
            centresList.sort(Comparator.comparing(c -> (String) c.get("centreEcrit")));

            academiaMap.put("centres", centresList);
            result.add(academiaMap);
        }

        // 🔹 (optionnel) tri académies
        result.sort(Comparator.comparing(a -> (String) a.get("academia")));

        return result;
    }

    /**
    public RepartitionCompleteCGSDTO construire(RepartitionTirageCGS rep)
    {
        // 1️Récupération des matières (déjà typées 👍)
        Map<String, GroupeMatiereCGS> matieres = rep.getMatieres();
        // Extraction des codes
        List<String> codes = new ArrayList<>(matieres.keySet());
        // Chargement des règles en une seule requête
        Map<String, RegleMatiereCGS> reglesMap = regleMatiereCGSRepository
                .findAllByValeurIn(codes)
                .stream()                     // now you can stream
                .collect(Collectors.toMap(
                        RegleMatiereCGS::getValeur,
                        Function.identity(),
                        (a, b) -> a
                ));
        // Construction des DTO
        List<MatiereComposeeCGSDTO> matieresFinales = new ArrayList<>();

        for (Map.Entry<String, GroupeMatiereCGS> entry : matieres.entrySet())
        {
            String code = entry.getKey();
            GroupeMatiereCGS valeur = entry.getValue();

            Double premier = valeur.getMatiere1ere();

            // Ignorer les enregistrements où premierGroupe est null ou 0
            if (premier == null || premier == 0.0) {
                continue;
            }

            Double second = valeur.getMatiereTle();
            RegleMatiereCGS regle = reglesMap.get(code);

            assert regle != null;
            MatiereComposeeCGSDTO dto = MatiereComposeeCGSDTO.builder()
                    .code(code)
                    .nom(regle.getValeur())
                    .series(rep.getSeries())
                    .premiere(premier)
                    .terminale(second)
                    .build();

            matieresFinales.add(dto);
        }

        // 5️⃣ Construction finale
        return RepartitionCompleteCGSDTO.builder()
                .centre(rep.getCentreEcrit())
                .academie(rep.getAcademia())
                .session(rep.getSession())
                .effectif(rep.getEffectif())
                .series(rep.getSeries())
                .matieres(matieresFinales)
                .build();
    }*/

}
