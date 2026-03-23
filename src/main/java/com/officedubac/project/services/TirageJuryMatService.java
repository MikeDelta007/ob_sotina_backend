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
import java.util.function.Function;
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

    public List<RepartitionTirageCEPDTO> repartitionParCEP()
    {
        // 🔹 récupérer tous les candidats
        List<SourceCandidat> candidats = sourceCandidatRepository.findAll();

        // 🔹 charger les règles dynamiques depuis la base
        List<RegleMatiere> regles = regleMatiereRepository.findAll();

        // 🔹 regrouper les candidats par jury
        Map<Integer, List<SourceCandidat>> candidatsParJury = candidats.stream().collect(Collectors.groupingBy(SourceCandidat::getJury));

        // 🔹 supprimer l'ancienne répartition
        repartitionTirageCEPRepository.deleteAll();

        List<RepartitionTirageCEP> entities = new ArrayList<>();
        List<RepartitionTirageCEPDTO> dtos = new ArrayList<>();

        try
        {
            for (Map.Entry<Integer, List<SourceCandidat>> entry : candidatsParJury.entrySet())
            {
                Integer jury = entry.getKey();
                List<SourceCandidat> groupe = entry.getValue();

                String centreEcrit = groupe.get(0).getCentreEcritPrincipal();
                String academia = groupe.get(0).getAcaCentEcrit();
                Integer session = groupe.get(0).getSession();
                long effectif = groupe.size();

                Map<String, GroupeMatiere> compteurs = new HashMap<>();
                regles.forEach(r -> {
                    GroupeMatiere gm = new GroupeMatiere(0.0, 0.0); // Double si tu veux +0.5
                    compteurs.put(r.getCode(), gm);
                });

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

                        if (match) {
                            // Récupère ou crée l'objet GroupeMatiere pour cette matière
                            GroupeMatiere gm = compteurs.computeIfAbsent(r.getCode(), k -> new GroupeMatiere(0.0, 0.0));

                            if ("1ERGRP".equals(r.getGroupe()))
                            {
                                gm.setPremierGroupe(gm.getPremierGroupe() + 1);
                            }
                            else if ("1ER2NDGRP".equals(r.getGroupe()))
                            {
                                gm.setPremierGroupe(gm.getPremierGroupe() + 1);
                                gm.setSecondGroupe(Math.round((gm.getSecondGroupe() + 0.5) * 10) / 10.0);
                            }
                            else
                            {
                                gm.setPremierGroupe(gm.getPremierGroupe() + 1);
                            }
                            // Remets à jour la map (optionnel si l'objet est mutable)
                            compteurs.put(r.getCode(), gm);
                        }
                    }
                }

                RepartitionTirageCEPDTO dto = RepartitionTirageCEPDTO.builder()
                        .jury(jury)
                        .session(session)
                        .centreEcrit(centreEcrit)
                        .academia(academia)
                        .effectif(effectif)
                        .matieres(new HashMap<>(compteurs))
                        .build();

                dtos.add(dto);

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

                Map<String, GroupeMatiere> compteurs = new HashMap<>();
                regles.forEach(r -> {
                    GroupeMatiere gm = new GroupeMatiere(0.0, 0.0); // Double si tu veux +0.5
                    compteurs.put(r.getCode(), gm);
                });

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

                        if (match)
                        {
                            // Récupère ou crée l'objet GroupeMatiere pour cette matière
                            GroupeMatiere gm = compteurs.computeIfAbsent(r.getCode(), k -> new GroupeMatiere(0.0, 0.0));

                            if ("1ERGRP".equals(r.getGroupe()))
                            {
                                gm.setPremierGroupe(gm.getPremierGroupe() + 1);
                            }
                            else if ("1ER2NDGRP".equals(r.getGroupe()))
                            {
                                gm.setPremierGroupe(gm.getPremierGroupe() + 1);
                                gm.setSecondGroupe(Math.round((gm.getSecondGroupe() + 0.5) * 10) / 10.0);
                            }
                            else
                            {
                                gm.setPremierGroupe(gm.getPremierGroupe() + 1);
                            }
                            // Remets à jour la map (optionnel si l'objet est mutable)
                            compteurs.put(r.getCode(), gm);
                        }
                    }
                }

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
        /**
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
         */
        List<Document> collectionA = mongoTemplate.findAll(Document.class, "repartition_tirage_CEP");
        /**
        log.info("collectionA size = {}", collectionA.size());
        log.info("Retrouver tirage CS...");
         */
        List<Document> collectionB = mongoTemplate.findAll(Document.class, "repartition_tirage_CES");
        // log.info("collectionB size = {}", collectionB.size());
        List<Document> all = new ArrayList<>();
        collectionA.forEach(doc -> doc.remove("_id"));
        collectionB.forEach(doc -> doc.remove("_id"));
        all.addAll(collectionA);
        all.addAll(collectionB);
        // log.info("Total à insérer = {}", all.size());
        mongoTemplate.dropCollection("fusion_repartition_tirage");
        mongoTemplate.insert(all, "fusion_repartition_tirage");
    }

    public List<FusionRepartitionTirage> getAllTirage()
    {
        List<FusionRepartitionTirage> allRep = fusionRepartitionTirageRepository.findAll();
        return allRep;
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

    public List<Map<String, Object>> getJurySummaryAllAcademies(String codeMatiere, String groupeChoisi) {
        // Récupérer toutes les tirages qui contiennent la matière
        List<FusionRepartitionTirage> tirages = fusionRepartitionTirageRepository.findAll().stream()
                .filter(f -> f.getMatieres() != null && f.getMatieres().containsKey(codeMatiere))
                .collect(Collectors.toList());

        // Grouper par académie
        Map<String, List<FusionRepartitionTirage>> groupedByAcademia = tirages.stream()
                .collect(Collectors.groupingBy(FusionRepartitionTirage::getAcademia));

        List<Map<String, Object>> result = new ArrayList<>();

        // Boucle sur chaque académie
        for (Map.Entry<String, List<FusionRepartitionTirage>> entry : groupedByAcademia.entrySet()) {
            String academia = entry.getKey();
            List<FusionRepartitionTirage> tiragesAcademia = entry.getValue();

            // Transformer chaque tirage en map avec les infos et la valeur du groupe choisi
            List<Map<String, Object>> rows = tiragesAcademia.stream().map(f -> {
                        GroupeMatiere gm = f.getMatieres().get(codeMatiere);
                        double valeur = 0.0;
                        if (gm != null) {
                            if ("1ER".equalsIgnoreCase(groupeChoisi)) {
                                valeur = gm.getPremierGroupe();
                            } else if ("2ND".equalsIgnoreCase(groupeChoisi)) {
                                valeur = gm.getSecondGroupe();
                            }
                        }

                        // On ignore les valeurs nulles ou <= 0
                        if (valeur <= 0) return null;

                        Map<String, Object> map = new HashMap<>();
                        map.put("matiere", codeMatiere);
                        map.put("session", f.getSession());
                        map.put("jury", f.getJury());
                        map.put("centreEcrit", f.getCentreEcrit());
                        map.put("academia", f.getAcademia());
                        map.put("effectif", valeur);

                        return map;
                    })
                    .filter(Objects::nonNull) // supprimer les entrées null (valeur <= 0)
                    .collect(Collectors.toList());

            // Calcul de la ligne TOTAL pour cette académie
            double totalValeur = rows.stream()
                    .mapToDouble(m -> (Double) m.get("effectif"))
                    .sum();

            if (!rows.isEmpty()) { // n'ajouter le total que si au moins une ligne valide
                Map<String, Object> totalMap = new HashMap<>();
                totalMap.put("matiere", codeMatiere);
                totalMap.put("session", "TOTAL");
                totalMap.put("jury", "");
                totalMap.put("centreEcrit", "");
                totalMap.put("academia", academia);
                totalMap.put("effectif", totalValeur);

                // Tri des lignes de l’académie par effectif décroissant
                rows.sort((m1, m2) -> Double.compare((Double) m2.get("effectif"), (Double) m1.get("effectif")));

                // Ajouter les lignes de l’académie puis le total
                result.addAll(rows);
                result.add(totalMap);
            }
        }

        return result;
    }



    public RepartitionCompleteDTO construire(FusionRepartitionTirage rep) {
        // 1️⃣ Récupération des matières (déjà typées 👍)
        Map<String, GroupeMatiere> matieres = rep.getMatieres();
        // 2️⃣ Extraction des codes
        List<String> codes = new ArrayList<>(matieres.keySet());
        // 3️⃣ Chargement des règles en une seule requête
        Map<String, RegleMatiere> reglesMap = regleMatiereRepository
                .findAllByCodeIn(codes)
                .stream()                     // now you can stream
                .collect(Collectors.toMap(
                        RegleMatiere::getCode,
                        Function.identity(),
                        (a, b) -> a
                ));
        // 4️⃣ Construction des DTO
        List<MatiereComposeeDTO> matieresFinales = new ArrayList<>();

        for (Map.Entry<String, GroupeMatiere> entry : matieres.entrySet())
        {
            String code = entry.getKey();
            GroupeMatiere valeur = entry.getValue();

            Double premier = valeur.getPremierGroupe();

            // Ignorer les enregistrements où premierGroupe est null ou 0
            if (premier == null || premier == 0.0) {
                continue;
            }

            Double second = valeur.getSecondGroupe();
            RegleMatiere regle = reglesMap.get(code);

            MatiereComposeeDTO dto = MatiereComposeeDTO.builder()
                    .code(code)
                    .nom(regle != null ? regle.getValeur() : null)
                    .series(regle != null ? regle.getSeries() : null)
                    .premierGroupe(premier)
                    .secondGroupe(second)
                    .build();

            matieresFinales.add(dto);
        }

        // 5️⃣ Construction finale
        return RepartitionCompleteDTO.builder()
                .centre(rep.getCentreEcrit())
                .academie(rep.getAcademia())
                .session(rep.getSession())
                .jury(rep.getJury())
                .matieres(matieresFinales)
                .build();
    }

    private Double getDouble(Object value) {
        return value != null ? Double.valueOf(value.toString()) : null;
    }

}
