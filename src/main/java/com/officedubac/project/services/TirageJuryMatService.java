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

        List<SourceCandidat> candidats = sourceCandidatRepository.findAll();

        // Regroupement par JURY
        Map<Integer, List<SourceCandidat>> candidatsParJury =
                candidats.stream().collect(Collectors.groupingBy(SourceCandidat::getJury));

        // Suppression ancienne répartition
        repartitionTirageCEPRepository.deleteAll();

        List<RepartitionTirageCEP> entities = new ArrayList<>();
        List<RepartitionTirageCEPDTO> dtos = new ArrayList<>();

        try
        {
            for (Map.Entry<Integer, List<SourceCandidat>> entry : candidatsParJury.entrySet()) {

                Integer jury = entry.getKey();
                List<SourceCandidat> groupe = entry.getValue();

                String centreEcrit = groupe.get(0).getCentreEcritPrincipal();
                String academia = groupe.get(0).getAcaCentEcrit();
                Integer session = groupe.get(0).getSession();
                long effectif = groupe.size();

                // Calcul des effectifs
                long frenchL = groupe.stream().filter(c -> hasSerie(c, SERIES_L)).count();
                long frenchS = groupe.stream().filter(c -> hasSerie(c, SERIES_S)).count();
                long frenchLA = groupe.stream().filter(c -> hasCode(c, "LA")).count();
                long frenchSA = groupe.stream().filter(c -> hasSerie(c, SERIES_SA)).count();
                long englishS = groupe.stream().filter(c -> hasSerie(c, SERIES_S) || hasSerie(c, SERIES_SA)).count();
                long mathL = groupe.stream().filter(c -> hasSerie(c, SERIES_L) || hasSerie(c, SERIES_LA)).count();
                long mathSM = groupe.stream().filter(c -> hasSerie(c, SERIES_SM) || hasSerie(c, SERIES_SM)).count();
                long pcSM = groupe.stream().filter(c -> hasSerie(c, SERIES_SM) || hasSerie(c, SERIES_SM)).count();
                long mathSE = groupe.stream().filter(c -> hasSerie(c, SERIES_SE) || hasSerie(c, SERIES_SE)).count();
                long pcSE = groupe.stream().filter(c -> hasSerie(c, SERIES_SE) || hasSerie(c, SERIES_SE)).count();
                long svtSE = groupe.stream().filter(c -> hasSerie(c, SERIES_SE) || hasSerie(c, SERIES_SE)).count();
                long svtSM = groupe.stream().filter(c -> hasSerie(c, SERIES_SM) || hasSerie(c, SERIES_SM)).count();
                long philoL = groupe.stream().filter(c -> hasSerie(c, SERIES_L) || hasSerie(c, SERIES_LA)).count();
                long philoS = groupe.stream().filter(c -> hasSerie(c, SERIES_S) || hasSerie(c, SERIES_SA)).count();
                long hg = groupe.stream().filter(c -> c.getSerie() != null).count();
                long lla = groupe.stream().filter(c -> hasCode(c, "S1A", "S2A", "LA", "L-AR")).count();

                long allemandLV1 = groupe.stream()
                        .filter(c -> c.getMatiere1() != null && c.getMatiere1().equalsIgnoreCase("Allemand"))
                        .count();

                long allemandLV2 = groupe.stream()
                        .filter(c -> c.getMatiere2() != null && c.getMatiere2().equalsIgnoreCase("Allemand"))
                        .count();

                long anglaisLV1 = groupe.stream()
                        .filter(c -> c.getMatiere1() != null && c.getMatiere1().equalsIgnoreCase("Anglais"))
                        .count();

                long anglaisLV2 = groupe.stream()
                        .filter(c -> c.getMatiere2() != null && c.getMatiere2().equalsIgnoreCase("Anglais"))
                        .count();

                long arabeModerneLV1 = groupe.stream()
                        .filter(c -> c.getMatiere1() != null && c.getMatiere1().equalsIgnoreCase("Arabe Moderne"))
                        .count();

                long arabeModerneLV2 = groupe.stream()
                        .filter(c -> c.getMatiere2() != null && c.getMatiere2().equalsIgnoreCase("Arabe Moderne"))
                        .count();

                long economie = groupe.stream()
                        .filter(c -> c.getMatiere2() != null && c.getMatiere2().equalsIgnoreCase("Economie"))
                        .count();

                long italien = groupe.stream()
                        .filter(c -> c.getMatiere2() != null && c.getMatiere2().equalsIgnoreCase("Italien"))
                        .count();

                long russe = groupe.stream()
                        .filter(c -> c.getMatiere2() != null && c.getMatiere2().equalsIgnoreCase("Russe"))
                        .count();

                long latin = groupe.stream()
                        .filter(c -> c.getMatiere2() != null && c.getMatiere2().equalsIgnoreCase("Latin"))
                        .count();

                long espagnolLV1 = groupe.stream()
                        .filter(c -> c.getMatiere1() != null && c.getMatiere1().equalsIgnoreCase("Espagnol"))
                        .count();

                long espagnolLV2 = groupe.stream()
                        .filter(c -> c.getMatiere2() != null && c.getMatiere2().equalsIgnoreCase("Espagnol"))
                        .count();

                long portugaisLV1 = groupe.stream()
                        .filter(c -> c.getMatiere1() != null && c.getMatiere1().equalsIgnoreCase("Portugais"))
                        .count();

                long portugaisLV2 = groupe.stream()
                        .filter(c -> c.getMatiere2() != null && c.getMatiere2().equalsIgnoreCase("Portugais"))
                        .count();

                long pcL = groupe.stream()
                        .filter(c -> c.getMatiere3() != null && c.getMatiere3().equalsIgnoreCase("Sciences Physiques"))
                        .count();

                long svtL = groupe.stream()
                        .filter(c -> c.getMatiere3() != null && c.getMatiere3().equalsIgnoreCase("Sciences de la vie et de la Terre"))
                        .count();

                long gelec = groupe.stream()
                        .filter(c -> c.getMatiere1() != null && c.getMatiere1().equalsIgnoreCase("Génie Electrique"))
                        .count();

                long gemec = groupe.stream()
                        .filter(c -> c.getMatiere1() != null && c.getMatiere1().equalsIgnoreCase("Génie mécanique"))
                        .count();

                long mo = groupe.stream()
                        .filter(c -> c.getMatiere1() != null && c.getMatiere1().equalsIgnoreCase("Management des organisations"))
                        .count();

                long ses = groupe.stream()
                        .filter(c -> c.getMatiere1() != null && c.getMatiere1().equalsIgnoreCase("Sciences Economiques et Sociales"))
                        .count();

                long gcf = groupe.stream()
                        .filter(c -> c.getMatiere1() != null && c.getMatiere1().equalsIgnoreCase("Gestion comptable et financière (Etude de cas)"))
                        .count();

                // DTO
                RepartitionTirageCEPDTO dto = RepartitionTirageCEPDTO.builder()
                        .jury(jury)
                        .session(session)
                        .centreEcrit(centreEcrit)
                        .academia(academia)
                        .effectif(effectif)
                        .frenchL(frenchL)
                        .frenchS(frenchS)
                        .frenchLA(frenchLA)
                        .frenchSA(frenchSA)
                        .englishS(englishS)
                        .mathL(mathL)
                        .mathSM(mathSM)
                        .mathSE(mathSE)
                        .pcSE(pcSE)
                        .pcSM(pcSM)
                        .svtSE(svtSE)
                        .svtSM(svtSM)
                        .philoL(philoL)
                        .philoS(philoS)
                        .hg(hg)
                        .lla(lla)
                        .allemendLV1(allemandLV1)
                        .allemendLV2(allemandLV2)
                        .anglaisLV1(anglaisLV1)
                        .anglaisLV2(anglaisLV2)
                        .arabeModerneLV1(arabeModerneLV1)
                        .arabeModerneLV2(arabeModerneLV2)
                        .economie(economie)
                        .espagnolLV1(espagnolLV1)
                        .espagnolLV2(espagnolLV2)
                        .italien(italien)
                        .latin(latin)
                        .portugaisLV1(portugaisLV1)
                        .portugaisLV2(portugaisLV2)
                        .russe(russe)
                        .pcL(pcL)
                        .svtL(svtL)
                        .mo(mo)
                        .ses(ses)
                        .gcf(gcf)
                        .gelec(gelec)
                        .gemec(gemec)
                        .build();

                dtos.add(dto);

                // Entity DB
                RepartitionTirageCEP entity = RepartitionTirageCEP.builder()
                        .jury(jury)
                        .session(session)
                        .centreEcrit(centreEcrit)
                        .academia(academia)
                        .effectif(effectif)
                        .frenchL(frenchL)
                        .frenchS(frenchS)
                        .frenchLA(frenchLA)
                        .frenchSA(frenchSA)
                        .englishS(englishS)
                        .mathL(mathL)
                        .mathSM(mathSM)
                        .mathSE(mathSE)
                        .pcSE(pcSE)
                        .pcSM(pcSM)
                        .svtSE(svtSE)
                        .svtSM(svtSM)
                        .philoL(philoL)
                        .philoS(philoS)
                        .hg(hg)
                        .lla(lla)
                        .allemendLV1(allemandLV1)
                        .allemendLV2(allemandLV2)
                        .anglaisLV1(anglaisLV1)
                        .anglaisLV2(anglaisLV2)
                        .arabeModerneLV1(arabeModerneLV1)
                        .arabeModerneLV2(arabeModerneLV2)
                        .economie(economie)
                        .espagnolLV1(espagnolLV1)
                        .espagnolLV2(espagnolLV2)
                        .italien(italien)
                        .latin(latin)
                        .portugaisLV1(portugaisLV1)
                        .portugaisLV2(portugaisLV2)
                        .russe(russe)
                        .pcL(pcL)
                        .svtL(svtL)
                        .mo(mo)
                        .ses(ses)
                        .gcf(gcf)
                        .gelec(gelec)
                        .gemec(gemec)
                        .build();

                entities.add(entity);
            }

        }
        catch (Exception e)
        {
            System.out.println(e);
        }
        // Sauvegarde en base
        repartitionTirageCEPRepository.saveAll(entities);

        // Retour trié
        return dtos.stream()
                .sorted(Comparator.comparing(RepartitionTirageCEPDTO::getJury))
                .toList();
    }

    public List<RepartitionTirageCSDTO> repartitionParCS() {

        List<SourceCandidat> candidats = sourceCandidatRepository.findByCentreEcritSecondaireIsNotNull();

        // Regroupement par JURY
        Map<Integer, List<SourceCandidat>> candidatsParJury =
                candidats.stream().collect(Collectors.groupingBy(SourceCandidat::getJury));

        // Suppression ancienne répartition
        repartitionTirageCSRepository.deleteAll();

        List<RepartitionTirageCES> entities = new ArrayList<>();
        List<RepartitionTirageCSDTO> dtos = new ArrayList<>();

        for (Map.Entry<Integer, List<SourceCandidat>> entry : candidatsParJury.entrySet()) {

            Integer jury = entry.getKey();
            List<SourceCandidat> groupe = entry.getValue();

            String centreEcrit = groupe.get(0).getCentreEcritSecondaire();
            String academia = groupe.get(0).getAcaCentEcrit();
            Integer session = groupe.get(0).getSession();
            long effectif = groupe.size();

            // Calcul des effectifs
            long frenchL = groupe.stream().filter(c -> hasSerie(c, SERIES_L)).count();
            long frenchS = groupe.stream().filter(c -> hasSerie(c, SERIES_S)).count();
            long frenchLA = groupe.stream().filter(c -> hasCode(c, "LA")).count();
            long frenchSA = groupe.stream().filter(c -> hasSerie(c, SERIES_SA)).count();
            long englishS = groupe.stream().filter(c -> hasSerie(c, SERIES_S) || hasSerie(c, SERIES_SA)).count();
            long mathL = groupe.stream().filter(c -> hasSerie(c, SERIES_L) || hasSerie(c, SERIES_LA)).count();
            long mathSM = groupe.stream().filter(c -> hasSerie(c, SERIES_SM) || hasSerie(c, SERIES_SM)).count();
            long pcSM = groupe.stream().filter(c -> hasSerie(c, SERIES_SM) || hasSerie(c, SERIES_SM)).count();
            long mathSE = groupe.stream().filter(c -> hasSerie(c, SERIES_SE) || hasSerie(c, SERIES_SE)).count();
            long pcSE = groupe.stream().filter(c -> hasSerie(c, SERIES_SE) || hasSerie(c, SERIES_SE)).count();
            long svtSE = groupe.stream().filter(c -> hasSerie(c, SERIES_SE) || hasSerie(c, SERIES_SE)).count();
            long svtSM = groupe.stream().filter(c -> hasSerie(c, SERIES_SM) || hasSerie(c, SERIES_SM)).count();
            long philoL = groupe.stream().filter(c -> hasSerie(c, SERIES_L) || hasSerie(c, SERIES_LA)).count();
            long philoS = groupe.stream().filter(c -> hasSerie(c, SERIES_S) || hasSerie(c, SERIES_SA)).count();
            long hg = groupe.stream().filter(c -> c.getSerie() != null).count();
            long lla = groupe.stream().filter(c -> hasCode(c, "S1A", "S2A", "LA")).count();

            long allemandLV1 = groupe.stream()
                    .filter(c -> c.getMatiere1() != null && c.getMatiere1().equalsIgnoreCase("Allemand"))
                    .count();

            long allemandLV2 = groupe.stream()
                    .filter(c -> c.getMatiere2() != null && c.getMatiere2().equalsIgnoreCase("Allemand"))
                    .count();

            long anglaisLV1 = groupe.stream()
                    .filter(c -> c.getMatiere1() != null && c.getMatiere1().equalsIgnoreCase("Anglais"))
                    .count();

            long anglaisLV2 = groupe.stream()
                    .filter(c -> c.getMatiere2() != null && c.getMatiere2().equalsIgnoreCase("Anglais"))
                    .count();

            long arabeModerneLV1 = groupe.stream()
                    .filter(c -> c.getMatiere1() != null && c.getMatiere1().equalsIgnoreCase("Arabe Moderne"))
                    .count();

            long arabeModerneLV2 = groupe.stream()
                    .filter(c -> c.getMatiere2() != null && c.getMatiere2().equalsIgnoreCase("Arabe Moderne"))
                    .count();

            long economie = groupe.stream()
                    .filter(c -> c.getMatiere2() != null && c.getMatiere2().equalsIgnoreCase("Economie"))
                    .count();

            long italien = groupe.stream()
                    .filter(c -> c.getMatiere2() != null && c.getMatiere2().equalsIgnoreCase("Italien"))
                    .count();

            long russe = groupe.stream()
                    .filter(c -> c.getMatiere2() != null && c.getMatiere2().equalsIgnoreCase("Russe"))
                    .count();

            long latin = groupe.stream()
                    .filter(c -> c.getMatiere2() != null && c.getMatiere2().equalsIgnoreCase("Latin"))
                    .count();

            long espagnolLV1 = groupe.stream()
                    .filter(c -> c.getMatiere1() != null && c.getMatiere1().equalsIgnoreCase("Espagnol"))
                    .count();

            long espagnolLV2 = groupe.stream()
                    .filter(c -> c.getMatiere2() != null && c.getMatiere2().equalsIgnoreCase("Espagnol"))
                    .count();

            long portugaisLV1 = groupe.stream()
                    .filter(c -> c.getMatiere1() != null && c.getMatiere1().equalsIgnoreCase("Portugais"))
                    .count();

            long portugaisLV2 = groupe.stream()
                    .filter(c -> c.getMatiere2() != null && c.getMatiere2().equalsIgnoreCase("Portugais"))
                    .count();

            long pcL = groupe.stream()
                    .filter(c -> c.getMatiere3() != null && c.getMatiere3().equalsIgnoreCase("Sciences Physiques"))
                    .count();

            long svtL = groupe.stream()
                    .filter(c -> c.getMatiere3() != null && c.getMatiere3().equalsIgnoreCase("Sciences de la vie et de la Terre"))
                    .count();

            long gelec = groupe.stream()
                    .filter(c -> c.getMatiere1() != null && c.getMatiere1().equalsIgnoreCase("Génie Electrique"))
                    .count();

            long gemec = groupe.stream()
                    .filter(c -> c.getMatiere1() != null && c.getMatiere1().equalsIgnoreCase("Génie mécanique"))
                    .count();

            long mo = groupe.stream()
                    .filter(c -> c.getMatiere1() != null && c.getMatiere1().equalsIgnoreCase("Management des organisations"))
                    .count();

            long ses = groupe.stream()
                    .filter(c -> c.getMatiere1() != null && c.getMatiere1().equalsIgnoreCase("Sciences Economiques et Sociales"))
                    .count();

            long gcf = groupe.stream()
                    .filter(c -> c.getMatiere1() != null && c.getMatiere1().equalsIgnoreCase("Gestion comptable et financière (Etude de cas)"))
                    .count();

            // DTO
            RepartitionTirageCSDTO dto = RepartitionTirageCSDTO.builder()
                    .jury(jury)
                    .session(session)
                    .centreEcrit(centreEcrit)
                    .academia(academia)
                    .effectif(effectif)
                    .frenchL(frenchL)
                    .frenchS(frenchS)
                    .frenchLA(frenchLA)
                    .frenchSA(frenchSA)
                    .englishS(englishS)
                    .mathL(mathL)
                    .mathSM(mathSM)
                    .mathSE(mathSE)
                    .pcSE(pcSE)
                    .pcSM(pcSM)
                    .svt(svtSE)
                    .philoL(philoL)
                    .philoS(philoS)
                    .hg(hg)
                    .lla(lla)
                    .allemendLV1(allemandLV1)
                    .allemendLV2(allemandLV2)
                    .anglaisLV1(anglaisLV1)
                    .anglaisLV2(anglaisLV2)
                    .arabeModerneLV1(arabeModerneLV1)
                    .arabeModerneLV2(arabeModerneLV2)
                    .economie(economie)
                    .espagnolLV1(espagnolLV1)
                    .espagnolLV2(espagnolLV2)
                    .italien(italien)
                    .latin(latin)
                    .portugaisLV1(portugaisLV1)
                    .portugaisLV2(portugaisLV2)
                    .russe(russe)
                    .pcL(pcL)
                    .svtL(svtL)
                    .mo(mo)
                    .ses(ses)
                    .gcf(gcf)
                    .gelec(gelec)
                    .gemec(gemec)
                    .build();

            dtos.add(dto);

            // Entity DB
            RepartitionTirageCES entity = RepartitionTirageCES.builder()
                    .jury(jury)
                    .session(session)
                    .centreEcrit(centreEcrit)
                    .academia(academia)
                    .effectif(effectif)
                    .frenchL(frenchL)
                    .frenchS(frenchS)
                    .frenchLA(frenchLA)
                    .frenchSA(frenchSA)
                    .englishS(englishS)
                    .mathL(mathL)
                    .mathSM(mathSM)
                    .mathSE(mathSE)
                    .pcSE(pcSE)
                    .pcSM(pcSM)
                    .svtSE(svtSE)
                    .svtSM(svtSM)
                    .philoL(philoL)
                    .philoS(philoS)
                    .hg(hg)
                    .lla(lla)
                    .allemendLV1(allemandLV1)
                    .allemendLV2(allemandLV2)
                    .anglaisLV1(anglaisLV1)
                    .anglaisLV2(anglaisLV2)
                    .arabeModerneLV1(arabeModerneLV1)
                    .arabeModerneLV2(arabeModerneLV2)
                    .economie(economie)
                    .espagnolLV1(espagnolLV1)
                    .espagnolLV2(espagnolLV2)
                    .italien(italien)
                    .latin(latin)
                    .portugaisLV1(portugaisLV1)
                    .portugaisLV2(portugaisLV2)
                    .russe(russe)
                    .pcL(pcL)
                    .svtL(svtL)
                    .mo(mo)
                    .ses(ses)
                    .gcf(gcf)
                    .gelec(gelec)
                    .gemec(gemec)
                    .build();

            entities.add(entity);
        }

        // Sauvegarde en base
        repartitionTirageCSRepository.saveAll(entities);

        // Retour trié
        return dtos.stream()
                .sorted(Comparator.comparing(RepartitionTirageCSDTO::getJury))
                .toList();
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

    public void unionCollections()
    {
        mongoTemplate.dropCollection("fusionRepartitionTirage");

        List<Document> collectionA = mongoTemplate.findAll(Document.class, "repartitionTirageCEP");
        List<Document> collectionB = mongoTemplate.findAll(Document.class, "repartitionTirageCES");
        List<Document> all = new ArrayList<>();

        collectionA.forEach(doc -> doc.remove("_id"));
        collectionB.forEach(doc -> doc.remove("_id"));

        all.addAll(collectionA);
        all.addAll(collectionB);

        mongoTemplate.insert(all, "fusionRepartitionTirage");
    }

    public Map<String, List<FusionRepartitionTirage>> getAllFusionRepTirage()
    {
        List<FusionRepartitionTirage> allRepTirage = fusionRepartitionTirageRepository.findAll();
        return allRepTirage
                .stream()
                .filter(p -> p.getAcademia() != null)
                .collect(Collectors.groupingBy(s -> s.getAcademia()));
    }

    public void prog_tirage_etiquette(HoraireRequest hr)
    {
        log.info("HoraireRequest: {}", hr);

        Map<String, HoraireItem> map = hr.getHoraires();

        // ✅ validation AVANT sauvegarde
        if (map == null || map.isEmpty()) {
            log.warn("Horaires vides — rien à traiter");
            return;
        }

        // 🔥 chercher s'il existe déjà
        HoraireRequest entity = horaireRequestRepository.findAll()
                .stream()
                .findFirst()
                .orElse(null);

        if (entity == null)
        {
            log.info("Création d'un nouveau HoraireRequest");
            horaireRequestRepository.save(hr);
        }
        else
        {
            log.info("Mise à jour du HoraireRequest existant");
            entity.setHoraires(map);
            horaireRequestRepository.save(entity);
        }

        List<FusionRepartitionTirage> all = fusionRepartitionTirageRepository.findAll();


        if (map == null || map.isEmpty()) {
            return;
        }

        for (FusionRepartitionTirage frt : all) {

            map.forEach((epreuve, h) -> {

                if (h == null) return;

                switch (epreuve) {
                    case "FRANCAIS L" -> {
                        frt.setDate1FL(h.getDate1());
                        frt.setHeure1FL(h.getHeure1());
                        frt.setDate2FL(h.getDate2());
                        frt.setHeure2FL(h.getHeure2());
                    }

                    case "FRANCAIS S" -> {
                        frt.setDate1FS(h.getDate1());
                        frt.setHeure1FS(h.getHeure1());
                        frt.setDate2FS(h.getDate2());
                        frt.setHeure2FS(h.getHeure2());
                    }

                    case "FRANCAIS LA" -> {
                        frt.setDate1FLa(h.getDate1());
                        frt.setHeure1FLa(h.getHeure1());
                        frt.setDate2FLa(h.getDate2());
                        frt.setHeure2FLa(h.getHeure2());
                    }

                    case "FRANCAIS SA" -> {
                        frt.setDate1FLa(h.getDate1());
                        frt.setHeure1FLa(h.getHeure1());
                        frt.setDate2FLa(h.getDate2());
                        frt.setHeure2FLa(h.getHeure2());
                    }

                    case "ANGLAIS S" -> {
                        frt.setDate1ES(h.getDate1());
                        frt.setHeure1ES(h.getHeure1());
                        frt.setDate2ES(h.getDate2());
                        frt.setHeure2ES(h.getHeure2());
                    }

                    case "MATH L" -> {
                        frt.setDate1ML(h.getDate1());
                        frt.setHeure1ML(h.getHeure1());
                        frt.setDate2ML(h.getDate2());
                        frt.setHeure2ML(h.getHeure2());
                    }

                    case "MATH SM" -> {
                        frt.setDate1MSM(h.getDate1());
                        frt.setHeure1MSM(h.getHeure1());
                        frt.setDate2MSM(h.getDate2());
                        frt.setHeure2MSM(h.getHeure2());
                    }

                    case "MATH SE" -> {
                        frt.setDate1MSE(h.getDate1());
                        frt.setHeure1MSE(h.getHeure1());
                        frt.setDate2MSE(h.getDate2());
                        frt.setHeure2MSE(h.getHeure2());
                    }

                    case "PC SM" -> {
                        frt.setDate1PCSM(h.getDate1());
                        frt.setHeure1PCSM(h.getHeure1());
                        frt.setDate2PCSM(h.getDate2());
                        frt.setHeure2PCSM(h.getHeure2());
                    }

                    case "PC SE" -> {
                        frt.setDate1PCSE(h.getDate1());
                        frt.setHeure1PCSE(h.getHeure1());
                        frt.setDate2PCSE(h.getDate2());
                        frt.setHeure2PCSE(h.getHeure2());
                    }

                    case "SVT SM" -> {
                        frt.setDate1SVTSM(h.getDate1());
                        frt.setHeure1SVTSM(h.getHeure1());
                        frt.setDate2SVTSM(h.getDate2());
                        frt.setHeure2SVTSM(h.getHeure2());
                    }

                    case "SVT SE" -> {
                        frt.setDate1SVTSE(h.getDate1());
                        frt.setHeure1SVTSE(h.getHeure1());
                        frt.setDate2SVTSE(h.getDate2());
                        frt.setHeure2SVTSE(h.getHeure2());
                    }

                    case "PHILO L" -> {
                        frt.setDate1PHILOL(h.getDate1());
                        frt.setHeure1PHILOL(h.getHeure1());
                        frt.setDate2PHILOL(h.getDate2());
                        frt.setHeure2PHILOL(h.getHeure2());
                    }

                    case "PHILO S" -> {
                        frt.setDate1PHILOS(h.getDate1());
                        frt.setHeure1PHILOS(h.getHeure1());
                        frt.setDate2PHILOS(h.getDate2());
                        frt.setHeure2PHILOS(h.getHeure2());
                    }

                    case "HG" -> {
                        frt.setDate1HG(h.getDate1());
                        frt.setHeure1HG(h.getHeure1());
                        frt.setDate2HG(h.getDate2());
                        frt.setHeure2HG(h.getHeure2());
                    }

                    case "LLA" -> {
                        frt.setDate1LLA(h.getDate1());
                        frt.setHeure1LLA(h.getHeure1());
                        frt.setDate2LLA(h.getDate2());
                        frt.setHeure2LLA(h.getHeure2());
                    }

                    case "ALLEMAND LV1" -> {
                        frt.setDate1ALL1(h.getDate1());
                        frt.setHeure1ALL1(h.getHeure1());
                        frt.setDate2ALL1(h.getDate2());
                        frt.setHeure2ALL1(h.getHeure2());
                    }

                    case "ALLEMAND LV2" -> {
                        frt.setDate1ALL2(h.getDate1());
                        frt.setHeure1ALL2(h.getHeure1());
                        frt.setDate2ALL2(h.getDate2());
                        frt.setHeure2ALL2(h.getHeure2());
                    }

                    case "ANGLAIS LV1" -> {
                        frt.setDate1ANG1(h.getDate1());
                        frt.setHeure1ANG1(h.getHeure1());
                        frt.setDate2ANG1(h.getDate2());
                        frt.setHeure2ANG1(h.getHeure2());
                    }

                    case "ANGLAIS LV2" -> {
                        frt.setDate1ANG2(h.getDate1());
                        frt.setHeure1ANG2(h.getHeure1());
                        frt.setDate2ANG2(h.getDate2());
                        frt.setHeure2ANG2(h.getHeure2());
                    }

                    case "ARABE MODERNE LV1" -> {
                        frt.setDate1AM1(h.getDate1());
                        frt.setHeure1AM1(h.getHeure1());
                        frt.setDate2AM1(h.getDate2());
                        frt.setHeure2AM1(h.getHeure2());
                    }

                    case "ARABE MODERNE LV2" -> {
                        frt.setDate1AM2(h.getDate1());
                        frt.setHeure1AM2(h.getHeure1());
                        frt.setDate2AM2(h.getDate2());
                        frt.setHeure2AM2(h.getHeure2());
                    }

                    case "ECONOMIE" -> {
                        frt.setDate1ECO(h.getDate1());
                        frt.setHeure1ECO(h.getHeure1());
                        frt.setDate2ECO(h.getDate2());
                        frt.setHeure2ECO(h.getHeure2());
                    }

                    case "ESPAGNOL LV1" -> {
                        frt.setDate1ESP1(h.getDate1());
                        frt.setHeure1ESP1(h.getHeure1());
                        frt.setDate2ESP1(h.getDate2());
                        frt.setHeure2ESP1(h.getHeure2());
                    }

                    case "ESPAGNOL LV2" -> {
                        frt.setDate1ESP2(h.getDate1());
                        frt.setHeure1ESP2(h.getHeure1());
                        frt.setDate2ESP2(h.getDate2());
                        frt.setHeure2ESP2(h.getHeure2());
                    }

                    case "ITALIEN" -> {
                        frt.setDate1ITA(h.getDate1());
                        frt.setHeure1ITA(h.getHeure1());
                        frt.setDate2ITA(h.getDate2());
                        frt.setHeure2ITA(h.getHeure2());
                    }

                    case "LATIN" -> {
                        frt.setDate1LAT(h.getDate1());
                        frt.setHeure1LAT(h.getHeure1());
                        frt.setDate2LAT(h.getDate2());
                        frt.setHeure2LAT(h.getHeure2());
                    }

                    case "PORTUGAIS LV1" -> {
                        frt.setDate1PORT1(h.getDate1());
                        frt.setHeure1PORT1(h.getHeure1());
                        frt.setDate2PORT1(h.getDate2());
                        frt.setHeure2PORT1(h.getHeure2());
                    }

                    case "PORTUGAIS LV2" -> {
                        frt.setDate1PORT2(h.getDate1());
                        frt.setHeure1PORT2(h.getHeure1());
                        frt.setDate2PORT2(h.getDate2());
                        frt.setHeure2PORT2(h.getHeure2());
                    }

                    case "RUSSE" -> {
                        frt.setDate1RUS(h.getDate1());
                        frt.setHeure1RUS(h.getHeure1());
                        frt.setDate2RUS(h.getDate2());
                        frt.setHeure2RUS(h.getHeure2());
                    }

                    case "PC L" -> {
                        frt.setDate1PCL(h.getDate1());
                        frt.setHeure1PCL(h.getHeure1());
                        frt.setDate2PCL(h.getDate2());
                        frt.setHeure2PCL(h.getHeure2());
                    }

                    case "SVT L" -> {
                        frt.setDate1SVTL(h.getDate1());
                        frt.setHeure1SVTL(h.getHeure1());
                        frt.setDate2SVTL(h.getDate2());
                        frt.setHeure2SVTL(h.getHeure2());
                    }

                    case "GENIE ELECTRIQUE" -> {
                        frt.setDate1GE(h.getDate1());
                        frt.setHeure1GE(h.getHeure1());
                        frt.setDate2GE(h.getDate2());
                        frt.setHeure2GE(h.getHeure2());
                    }

                    case "GENIE MECANIQUE" -> {
                        frt.setDate1GM(h.getDate1());
                        frt.setHeure1GM(h.getHeure1());
                        frt.setDate2GM(h.getDate2());
                        frt.setHeure2GM(h.getHeure2());
                    }

                    case "MANAGEMENT DES ORGANISATIONS" -> {
                        frt.setDate1MO(h.getDate1());
                        frt.setHeure1MO(h.getHeure1());
                        frt.setDate2MO(h.getDate2());
                        frt.setHeure2MO(h.getHeure2());
                    }

                    case "SCIENCES ECONOMIQUES ET SOCIALES" -> {
                        frt.setDate1SES(h.getDate1());
                        frt.setHeure1SES(h.getHeure1());
                        frt.setDate2SES(h.getDate2());
                        frt.setHeure2SES(h.getHeure2());
                    }

                    case "GESTION COMPTABLE ET FINANCIERE" -> {
                        frt.setDate1GCF(h.getDate1());
                        frt.setHeure1GCF(h.getHeure1());
                        frt.setDate2GCF(h.getDate2());
                        frt.setHeure2GCF(h.getHeure2());
                    }

                    default -> {
                        // log.warn("Matière non mappée: {}", epreuve);
                    }
                }
            });
        }

        fusionRepartitionTirageRepository.saveAll(all);

        log.info("Log bombe");
    }

    public HoraireRequest getHoraires() {

        return horaireRequestRepository.findAll()
                .stream()
                .findFirst()
                .orElseGet(HoraireRequest::new);
    }

}
