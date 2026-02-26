package com.officedubac.project.services;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.stereotype.Service;

import javax.xml.transform.Source;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class TirageJuryMatService
{
    private final SourceCandidatRepository sourceCandidatRepository;

    private final RepartitionTirageCEPRepository repartitionTirageCEPRepository;

    private final RepartitionTirageCSRepository repartitionTirageCSRepository;

    private final FusionRepartitionTirageRepository fusionRepartitionTirageRepository;

    private final HoraireRequestRepository horaireRequestRepository;

    private final RegleMatiereRepository regleMatiereRepository;

    private final MongoTemplate mongoTemplate;

    private static final Set<String> SERIES_L = Set.of("L'1", "L1A", "L1B", "L2");
    private static final Set<String> SERIES_LA = Set.of("LA");
    private static final Set<String> SERIES_S = Set.of("S1", "S2", "S3", "S4", "S5");
    private static final Set<String> SERIES_SM = Set.of("S1", "S1A", "S3");
    private static final Set<String> SERIES_SE = Set.of("S2", "S2A", "S4", "S5");
    private static final Set<String> SERIES_SA = Set.of("S1A", "S2A");
    private static final Set<String> SERIES_STIDD = Set.of("STIDD");
    private static final Set<String> SERIES_STEG = Set.of("STEG");

    // Méthode principale

    private boolean hasSerie(SourceCandidat c, Collection<String> series) {
        return c.getSerie() != null && series.contains(c.getSerie());
    }

    private boolean hasCode(SourceCandidat c, String... codes) {
        return c.getSerie() != null && Arrays.asList(codes).contains(c.getSerie());
    }


    public List<RepartitionTirageCEPDTO> repartitionParCEP() {

        // 🔹 récupérer tous les candidats
        List<SourceCandidat> candidats = sourceCandidatRepository.findAll();

        // 🔹 charger les règles dynamiques depuis la base
        List<RegleMatiere> regles = regleMatiereRepository.findAll();

        // 🔹 regrouper les candidats par jury
        Map<Integer, List<SourceCandidat>> candidatsParJury =
                candidats.stream().collect(Collectors.groupingBy(SourceCandidat::getJury));

        // 🔹 supprimer l'ancienne répartition
        repartitionTirageCEPRepository.deleteAll();

        List<RepartitionTirageCEP> entities = new ArrayList<>();
        List<RepartitionTirageCEPDTO> dtos = new ArrayList<>();

        try {

            for (Map.Entry<Integer, List<SourceCandidat>> entry : candidatsParJury.entrySet()) {

                Integer jury = entry.getKey();
                List<SourceCandidat> groupe = entry.getValue();

                String centreEcrit = groupe.get(0).getCentreEcritPrincipal();
                String academia = groupe.get(0).getAcaCentEcrit();
                Integer session = groupe.get(0).getSession();
                long effectif = groupe.size();

                // 🔥 Map dynamique des compteurs initialisée à 0
                Map<String, Integer> compteurs = new HashMap<>();
                regles.forEach(r -> compteurs.put(r.getCode(), 0));

                // =====================================================
                // 🚀 UNE SEULE BOUCLE SUR TOUS LES CANDIDATS DU JURY
                // =====================================================
                for (SourceCandidat c : groupe)
                {
                    for (RegleMatiere r : regles)
                    {
                        boolean match = false;
                        // ===== règles par série =====
                        if ("SERIE".equalsIgnoreCase(r.getType()))
                        {
                            if (c.getSerie() != null &&
                                    r.getSeries() != null &&
                                    r.getSeries().contains(c.getSerie())) {
                                match = true;
                            }
                        }
                        // ===== règles optionnelles =====
                        else if ("OPTION".equalsIgnoreCase(r.getType())) {
                            String valeurChamp = switch (r.getChamp()) {
                                case "matiere1" -> c.getMatiere1();
                                case "matiere2" -> c.getMatiere2();
                                case "matiere3" -> c.getMatiere3();
                                default -> null;
                            };

                            if (valeurChamp != null &&
                                    valeurChamp.equalsIgnoreCase(r.getValeur())) {
                                match = true;
                            }
                        }

                        else if ("FACULTATIVE".equalsIgnoreCase(r.getType())) {
                            String valeurChamp2 = switch (r.getChamp()) {
                                case "eprFacListA" -> c.getEprFacListA();
                                case "eprFacListB" -> c.getEprFacListB();
                                default -> null;
                            };

                            if (valeurChamp2 != null &&
                                    valeurChamp2.equalsIgnoreCase(r.getValeur()))
                            {
                                match = true;
                            }
                        }

                        // 🔥 si le candidat correspond à la règle, incrémenter le compteur
                        if (match) {
                            compteurs.merge(r.getCode(), 1, Integer::sum);
                        }
                    }
                }

                // =====================================================
                // 🧠 Construire le DTO avec la map dynamique
                // =====================================================
                RepartitionTirageCEPDTO dto = RepartitionTirageCEPDTO.builder()
                        .jury(jury)
                        .session(session)
                        .centreEcrit(centreEcrit)
                        .academia(academia)
                        .effectif(effectif)
                        .matieres(new HashMap<>(compteurs))
                        .build();

                dtos.add(dto);

                // =====================================================
                // ⚡ Construire l'entité DB
                // =====================================================
                RepartitionTirageCEP entity = RepartitionTirageCEP.builder()
                        .jury(jury)
                        .session(session)
                        .centreEcrit(centreEcrit)
                        .academia(academia)
                        .effectif(effectif)
                        .matieres(new HashMap<>(compteurs))
                        .build();

                entities.add(entity);
            }

        } catch (Exception e) {
            System.out.println("Erreur répartition CEP : " + e.getMessage());
            e.printStackTrace();
        }

        // 🔹 sauvegarde en base
        repartitionTirageCEPRepository.saveAll(entities);

        // 🔹 retour trié par jury
        return dtos.stream()
                .sorted(Comparator.comparing(RepartitionTirageCEPDTO::getJury))
                .toList();
    }


    public List<RepartitionTirageCSDTO> repartitionParCS()
    {
        // 🔹 récupérer tous les candidats
        List<SourceCandidat> candidats = sourceCandidatRepository.findByCentreEcritSecondaireIsNotNull();
        // 🔹 charger les règles dynamiques depuis la base
        List<RegleMatiere> regles = regleMatiereRepository.findAll();

        // 🔹 regrouper les candidats par jury
        Map<Integer, List<SourceCandidat>> candidatsParJury =
                candidats.stream().collect(Collectors.groupingBy(SourceCandidat::getJury));

        // 🔹 supprimer l'ancienne répartition
        repartitionTirageCSRepository.deleteAll();

        List<RepartitionTirageCES> entities = new ArrayList<>();
        List<RepartitionTirageCSDTO> dtos = new ArrayList<>();

        try {

            for (Map.Entry<Integer, List<SourceCandidat>> entry : candidatsParJury.entrySet()) {

                Integer jury = entry.getKey();
                List<SourceCandidat> groupe = entry.getValue();

                String centreEcrit = groupe.get(0).getCentreEcritSecondaire();
                String academia = groupe.get(0).getAcaCentEcrit();
                Integer session = groupe.get(0).getSession();
                long effectif = groupe.size();

                // 🔥 Map dynamique des compteurs initialisée à 0
                Map<String, Integer> compteurs = new HashMap<>();
                regles.forEach(r -> compteurs.put(r.getCode(), 0));

                // =====================================================
                // 🚀 UNE SEULE BOUCLE SUR TOUS LES CANDIDATS DU JURY
                // =====================================================
                for (SourceCandidat c : groupe) {

                    for (RegleMatiere r : regles) {

                        boolean match = false;

                        // ===== règles par série =====
                        if ("SERIE".equalsIgnoreCase(r.getType())) {
                            if (c.getSerie() != null &&
                                    r.getSeries() != null &&
                                    r.getSeries().contains(c.getSerie())) {
                                match = true;
                            }
                        }

                        // ===== règles optionnelles =====
                        else if ("OPTION".equalsIgnoreCase(r.getType())) {
                            String valeurChamp = switch (r.getChamp()) {
                                case "matiere1" -> c.getMatiere1();
                                case "matiere2" -> c.getMatiere2();
                                case "matiere3" -> c.getMatiere3();
                                default -> null;
                            };

                            if (valeurChamp != null &&
                                    valeurChamp.equalsIgnoreCase(r.getValeur())) {
                                match = true;
                            }
                        }

                        else if ("FACULTATIVE".equalsIgnoreCase(r.getType())) {
                            String valeurChamp2 = switch (r.getChamp()) {
                                case "eprFacListA" -> c.getEprFacListA();
                                case "eprFacListB" -> c.getEprFacListB();
                                default -> null;
                            };

                            if (valeurChamp2 != null &&
                                    valeurChamp2.equalsIgnoreCase(r.getValeur()))
                            {
                                match = true;
                            }
                        }

                        // 🔥 si le candidat correspond à la règle, incrémenter le compteur
                        if (match) {
                            compteurs.merge(r.getCode(), 1, Integer::sum);
                        }
                    }
                }

                // =====================================================
                // 🧠 Construire le DTO avec la map dynamique
                // =====================================================
                RepartitionTirageCSDTO dto = RepartitionTirageCSDTO.builder()
                        .jury(jury)
                        .session(session)
                        .centreEcrit(centreEcrit)
                        .academia(academia)
                        .effectif(effectif)
                        .matieres(new HashMap<>(compteurs))
                        .build();

                dtos.add(dto);

                // =====================================================
                // ⚡ Construire l'entité DB
                // =====================================================
                RepartitionTirageCES entity = RepartitionTirageCES.builder()
                        .jury(jury)
                        .session(session)
                        .centreEcrit(centreEcrit)
                        .academia(academia)
                        .effectif(effectif)
                        .matieres(new HashMap<>(compteurs))
                        .build();

                entities.add(entity);
            }

        } catch (Exception e) {
            System.out.println("Erreur répartition CEP : " + e.getMessage());
            e.printStackTrace();
        }

        // 🔹 sauvegarde en base
        repartitionTirageCSRepository.saveAll(entities);

        // 🔹 retour trié par jury
        return dtos.stream()
                .sorted(Comparator.comparing(RepartitionTirageCSDTO::getJury))
                .toList();
    }


    public void unionCollections()
    {

        long countCep = mongoTemplate
                .getCollection("repartition_tirage_CEP")
                .countDocuments();

        long countCes = mongoTemplate
                .getCollection("repartition_tirage_CES")
                .countDocuments();

        log.info("Count CEP direct = {}", countCep);
        log.info("Count CES direct = {}", countCes);
        log.info("Database name = {}", mongoTemplate.getDb().getName());
        log.info("Mongo client = {}", mongoTemplate.getMongoDatabaseFactory());

        log.info("Collections = {}", mongoTemplate.getCollectionNames());
        log.info("Retrouver tirage CP...");
        List<Document> collectionA = mongoTemplate.findAll(Document.class, "repartition_tirage_CEP");
        log.info("collectionA size = {}", collectionA.size());
        log.info("Retrouver tirage CS...");
        List<Document> collectionB = mongoTemplate.findAll(Document.class, "repartition_tirage_CES");
        log.info("collectionB size = {}", collectionB.size());
        List<Document> all = new ArrayList<>();
        collectionA.forEach(doc -> doc.remove("_id"));
        collectionB.forEach(doc -> doc.remove("_id"));
        all.addAll(collectionA);
        all.addAll(collectionB);
        log.info("Total à insérer = {}", all.size());
        mongoTemplate.dropCollection("fusion_repartition_tirage");
        mongoTemplate.insert(all, "fusion_repartition_tirage");
    }




    public Page<SourceCandidatDTO> getListCandidats(int page, int size)
    {

        Pageable pageable = PageRequest.of(page, size);

        // 1️⃣ Récupérer la page de candidats depuis Mongo
        Page<SourceCandidat> candidats = sourceCandidatRepository.findAll(pageable);

        // 2️⃣ Hydrater chaque candidat avec son Sujet
        List<SourceCandidatDTO> dtos = candidats.stream().map(c -> {

            SourceCandidatDTO dto = new SourceCandidatDTO();

            dto.setFirstname(c.getFirstname());
            dto.setLastname(c.getLastname());
            dto.setDate_birth(c.getDate_birth());
            dto.setPlace_birth(c.getPlace_birth());
            dto.setSession(c.getSession());
            dto.setGender(c.getGender());
            dto.setMatiere1(c.getMatiere1());
            dto.setMatiere2(c.getMatiere2());
            dto.setMatiere3(c.getMatiere3());

            dto.setEprFacListA(c.getEprFacListA());
            dto.setEprFacListB(c.getEprFacListB());

            dto.setEtablissement(c.getEtablissement());
            dto.setSerie(c.getSerie());
            dto.setNationality(c.getNationality());
            dto.setCentreExamen(c.getCentreExamen());

            dto.setTableNum(c.getTableNum());
            dto.setSession(c.getSession());
            dto.setJury(c.getJury());

            dto.setCentreEcritPrincipal(c.getCentreEcritPrincipal());
            dto.setCentreEcritSecondaire(c.getCentreEcritSecondaire());

            dto.setAcaEtab(c.getAcaEtab());
            dto.setAcaCentEcrit(c.getAcaCentEcrit());

            // tu peux mapper d’autres champs ici si besoin

            return dto;
        }).toList();

        // 3️⃣ Compter le total pour la pagination
        long total = sourceCandidatRepository.count();

        // 4️⃣ Construire la Page
        return new PageImpl<>(dtos, pageable, total);
    }


    public Map<String, List<RepartitionTirageCEP>> getRepCPByAcademie()
    {
        List<RepartitionTirageCEP> allUsers = repartitionTirageCEPRepository.findAll();
        return allUsers
                .stream()
                .filter(p -> p.getAcademia() != null)
                .collect(Collectors.groupingBy(s -> s.getAcademia()));
    }


    public List<RepartitionTirageCEP> getRepCP_()
    {
        List<RepartitionTirageCEP> allRep = repartitionTirageCEPRepository.findAll();
        return allRep;
    }

    public Map<String, List<RepartitionTirageCES>> getRepCSByAcademie()
    {
        List<RepartitionTirageCES> allUsers = repartitionTirageCSRepository.findAll();
        return allUsers
                .stream()
                .filter(p -> p.getAcademia() != null)
                .collect(Collectors.groupingBy(s -> s.getAcademia()));
    }



    public Map<String, List<FusionRepartitionTirage>> getAllFusionRepTirage()
    {
        List<FusionRepartitionTirage> allRepTirage = fusionRepartitionTirageRepository.findAll();
        return allRepTirage
                .stream()
                .filter(p -> p.getAcademia() != null)
                .collect(Collectors.groupingBy(s -> s.getAcademia()));
    }

    public HoraireRequest getHoraires() {

        return horaireRequestRepository.findAll()
                .stream()
                .findFirst()
                .orElseGet(HoraireRequest::new);
    }

}
