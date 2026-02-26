package com.officedubac.project.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.officedubac.project.dto.RepartitionFeuilleCEPDTO;
import com.officedubac.project.dto.RepartitionFeuilleCESDTO;
import com.officedubac.project.dto.RepartitionTirageCEPDTO;
import com.officedubac.project.dto.RepartitionTirageCSDTO;
import com.officedubac.project.models.*;
import com.officedubac.project.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class DecompteFeuilleJuryService
{
    private final SourceCandidatRepository sourceCandidatRepository;

    private final RepartitionFeuilleCEPRepository repartitionFeuilleCEPRepository;

    private final FusionRepartitionFeuilleRepository fusionRepartitionFeuilleRepository;

    private final RepartitionFeuilleCESRepository repartitionFeuilleCESRepository;

    private final MongoTemplate mongoTemplate;

    public List<RepartitionFeuilleCEPDTO> repartitionParCEP() {

        List<SourceCandidat> candidats = sourceCandidatRepository.findAll();

        // Regroupement par CEP
        Map<String, List<SourceCandidat>> candidatsParCEP =
                candidats.stream().collect(Collectors.groupingBy(SourceCandidat::getCentreEcritPrincipal));

        // Suppression ancienne répartition
        repartitionFeuilleCEPRepository.deleteAll();

        List<RepartitionFeuilleCEP> entities = new ArrayList<>();
        List<RepartitionFeuilleCEPDTO> dtos = new ArrayList<>();

        try
        {
            for (Map.Entry<String, List<SourceCandidat>> entry : candidatsParCEP.entrySet()) {

                String cep = entry.getKey();
                List<SourceCandidat> groupe = entry.getValue();

                String centreEcrit = groupe.get(0).getCentreEcritPrincipal();
                String academia = groupe.get(0).getAcaCentEcrit();
                String centreExam = groupe.get(0).getCentreExamen();
                Integer session = groupe.get(0).getSession();
                long effectif = groupe.size();

                long F6 = groupe.stream()
                        .filter(c -> c.getSerie() != null && c.getSerie().equalsIgnoreCase("F6"))
                        .count();

                long Lprime = groupe.stream()
                        .filter(c -> c.getSerie() != null && c.getSerie().equalsIgnoreCase("L'1"))
                        .count();

                long L1A = groupe.stream()
                        .filter(c -> c.getSerie() != null && c.getSerie().equalsIgnoreCase("L1A"))
                        .count();

                long L1B = groupe.stream()
                        .filter(c -> c.getSerie() != null && c.getSerie().equalsIgnoreCase("L1B"))
                        .count();

                long L2 = groupe.stream()
                        .filter(c -> c.getSerie() != null && c.getSerie().equalsIgnoreCase("L2"))
                        .count();

                long LA = groupe.stream()
                        .filter(c -> c.getSerie() != null && c.getSerie().equalsIgnoreCase("LA"))
                        .count();

                long LAR = groupe.stream()
                        .filter(c -> c.getSerie() != null && c.getSerie().equalsIgnoreCase("LAR"))
                        .count();

                long S1 = groupe.stream()
                        .filter(c -> c.getSerie() != null && c.getSerie().equalsIgnoreCase("S1"))
                        .count();

                long S1A = groupe.stream()
                        .filter(c -> c.getSerie() != null && c.getSerie().equalsIgnoreCase("S1A"))
                        .count();

                long S2 = groupe.stream()
                        .filter(c -> c.getSerie() != null && c.getSerie().equalsIgnoreCase("S2"))
                        .count();

                long S2A = groupe.stream()
                        .filter(c -> c.getSerie() != null && c.getSerie().equalsIgnoreCase("S2A"))
                        .count();

                long S3 = groupe.stream()
                        .filter(c -> c.getSerie() != null && c.getSerie().equalsIgnoreCase("S3"))
                        .count();

                long S4 = groupe.stream()
                        .filter(c -> c.getSerie() != null && c.getSerie().equalsIgnoreCase("S4"))
                        .count();

                long S5 = groupe.stream()
                        .filter(c -> c.getSerie() != null && c.getSerie().equalsIgnoreCase("S5"))
                        .count();

                long STEG = groupe.stream()
                        .filter(c -> c.getSerie() != null && c.getSerie().equalsIgnoreCase("STEG"))
                        .count();

                long STIDD = groupe.stream()
                        .filter(c -> c.getSerie() != null && c.getSerie().equalsIgnoreCase("STIDD"))
                        .count();

                long T1 = groupe.stream()
                        .filter(c -> c.getSerie() != null && c.getSerie().equalsIgnoreCase("T1"))
                        .count();

                long T2 = groupe.stream()
                        .filter(c -> c.getSerie() != null && c.getSerie().equalsIgnoreCase("T2"))
                        .count();


                // DTO
                RepartitionFeuilleCEPDTO dto = RepartitionFeuilleCEPDTO.builder()
                        .centreEcrit(centreEcrit)
                        .academia(academia)
                        .effectif(effectif)
                        .centreExamen(centreExam)
                        .session(session)
                        .F6(F6)
                        .Lprime(Lprime)
                        .L1A(L1A)
                        .L1B(L1B)
                        .L2(L2)
                        .LA(LA)
                        .LAR(LAR)
                        .S1(S1)
                        .S1A(S1A)
                        .S2(S2)
                        .S2A(S2A)
                        .S3(S3)
                        .S4(S4)
                        .S5(S5)
                        .STEG(STEG)
                        .STIDD(STIDD)
                        .T1(T1)
                        .T2(T2)
                        .feuille_double((long) (Math.ceil((Lprime * 9) + (L1A * 10) + (L1B * 10) + (L2 * 10) + (LA * 10) + (LAR * 9) + (S1 * 10) + (S1A * 11) +
                                                        (S2 * 10) + (S2A * 11) + (S3 * 10) + (S4 * 13) + (S5 * 13) + (STEG * 13) + (STIDD * 11) +
                                                        (T1 * 13) + (T2 * 11)) * 1.1))
                        .feuille_brouillon((long) (Math.ceil((Lprime * 9) + (L1A * 10) + (L1B * 10) + (L2 * 10) + (LA * 10) + (LAR * 9) + (S1 * 10) + (S1A * 11) +
                                                        (S2 * 10) + (S2A * 11) + (S3 * 10) + (S4 * 13) + (S5 * 13) + (STEG * 13) + (STIDD * 11) +
                                                        (T1 * 13) + (T2 * 11)) * 1.1) * 2)
                        .feuille_intercalaire((long) (Math.ceil((Lprime * 9) + (L1A * 10) + (L1B * 10) + (L2 * 10) + (LA * 10) + (LAR * 9) + (S1 * 10) + (S1A * 11) +
                                                        (S2 * 10) + (S2A * 11) + (S3 * 10) + (S4 * 13) + (S5 * 13) + (STEG * 13) + (STIDD * 11) +
                                                        (T1 * 13) + (T2 * 11)) * 1.1) * 2)
                        .build();

                dtos.add(dto);

                // Entity DB
                RepartitionFeuilleCEP entity = RepartitionFeuilleCEP.builder()
                        .centreEcrit(centreEcrit)
                        .academia(academia)
                        .effectif(effectif)
                        .centreExamen(centreExam)
                        .session(session)
                        .F6(F6)
                        .Lprime(Lprime)
                        .L1A(L1A)
                        .L1B(L1B)
                        .L2(L2)
                        .LA(LA)
                        .LAR(LAR)
                        .S1(S1)
                        .S1A(S1A)
                        .S2(S2)
                        .S2A(S2A)
                        .S3(S3)
                        .S4(S4)
                        .S5(S5)
                        .STEG(STEG)
                        .STIDD(STIDD)
                        .T1(T1)
                        .T2(T2)
                        .feuille_double((long) (Math.ceil((Lprime * 9) + (L1A * 10) + (L1B * 10) + (L2 * 10) + (LA * 10) + (LAR * 9) + (S1 * 10) + (S1A * 11) +
                                                        (S2 * 10) + (S2A * 11) + (S3 * 10) + (S4 * 13) + (S5 * 13) + (STEG * 13) + (STIDD * 11) +
                                                        (T1 * 13) + (T2 * 11)) * 1.1))
                        .feuille_brouillon((long) (Math.ceil((Lprime * 9) + (L1A * 10) + (L1B * 10) + (L2 * 10) + (LA * 10) + (LAR * 9) + (S1 * 10) + (S1A * 11) +
                                                        (S2 * 10) + (S2A * 11) + (S3 * 10) + (S4 * 13) + (S5 * 13) + (STEG * 13) + (STIDD * 11) +
                                                        (T1 * 13) + (T2 * 11)) * 1.1) * 2)
                        .feuille_intercalaire((long) (Math.ceil((Lprime * 9) + (L1A * 10) + (L1B * 10) + (L2 * 10) + (LA * 10) + (LAR * 9) + (S1 * 10) + (S1A * 11) +
                                                        (S2 * 10) + (S2A * 11) + (S3 * 10) + (S4 * 13) + (S5 * 13) + (STEG * 13) + (STIDD * 11) +
                                                        (T1 * 13) + (T2 * 11)) * 1.1) * 2)
                        .build();

                entities.add(entity);
            }

        }
        catch (Exception e)
        {
            System.out.println(e);
        }
        // Sauvegarde en base
        repartitionFeuilleCEPRepository.saveAll(entities);

        // Retour trié
        return dtos.stream()
                .sorted(Comparator.comparing(RepartitionFeuilleCEPDTO::getCentreExamen))
                .toList();
    }

    public List<RepartitionFeuilleCESDTO> repartitionParCS() {

        List<SourceCandidat> candidats = sourceCandidatRepository.findByCentreEcritSecondaireIsNotNull();

        Map<String, List<SourceCandidat>> candidatsParCS =
                candidats.stream().collect(Collectors.groupingBy(SourceCandidat::getCentreEcritSecondaire));

        repartitionFeuilleCESRepository.deleteAll();

        List<RepartitionFeuilleCES> entities = new ArrayList<>();
        List<RepartitionFeuilleCESDTO> dtos = new ArrayList<>();

        try {
            for (Map.Entry<String, List<SourceCandidat>> entry : candidatsParCS.entrySet()) {

                String ces = entry.getKey();
                List<SourceCandidat> groupe = entry.getValue();

                String centreEcrit = groupe.get(0).getCentreEcritSecondaire();
                String academia = groupe.get(0).getAcaCentEcrit();
                String centreExam = groupe.get(0).getCentreExamen();
                Integer session = groupe.get(0).getSession();
                long effectif = groupe.size();

                long F6 = countSerie(groupe, "F6");
                long Lprime = countSerie(groupe, "L'1");
                long L1A = countSerie(groupe, "L1A");
                long L1B = countSerie(groupe, "L1B");
                long L2 = countSerie(groupe, "L2");
                long LA = countSerie(groupe, "LA");
                long LAR = countSerie(groupe, "LAR");
                long S1 = countSerie(groupe, "S1");
                long S1A = countSerie(groupe, "S1A");
                long S2 = countSerie(groupe, "S2");
                long S2A = countSerie(groupe, "S2A");
                long S3 = countSerie(groupe, "S3");
                long S4 = countSerie(groupe, "S4");
                long S5 = countSerie(groupe, "S5");
                long STEG = countSerie(groupe, "STEG");
                long STIDD = countSerie(groupe, "STIDD");
                long T1 = countSerie(groupe, "T1");
                long T2 = countSerie(groupe, "T2");

                // ---------------------------
                //  CALCUL TOTAL EN UNE SEULE FOIS
                // ---------------------------
                long totalBrut =
                        (Lprime * 9L) + (L1A * 10L) + (L1B * 10L) + (L2 * 10L) + (LA * 10L)
                                + (LAR * 9L) + (S1 * 10L) + (S1A * 11L)
                                + (S2 * 10L) + (S2A * 11L) + (S3 * 10L)
                                + (S4 * 13L) + (S5 * 13L)
                                + (STEG * 13L) + (STIDD * 11L)
                                + (T1 * 13L) + (T2 * 11L);

                // ---------------------------
                //  CALCUL AVEC BIGDECIMAL (FIABLE)
                // ---------------------------
                BigDecimal bdTotal = BigDecimal.valueOf(totalBrut);
                BigDecimal coeff = new BigDecimal("1.1"); // IMPORTANT
                BigDecimal bdAdjusted = bdTotal.multiply(coeff);

                long feuilleDouble = bdAdjusted.setScale(0, RoundingMode.CEILING).longValueExact();
                long feuilleBrouillon = feuilleDouble * 2;
                long feuilleIntercalaire = feuilleDouble * 2;

                // DTO
                RepartitionFeuilleCESDTO dto = RepartitionFeuilleCESDTO.builder()
                        .centreEcrit(centreEcrit)
                        .academia(academia)
                        .effectif(effectif)
                        .centreExamen(centreExam)
                        .session(session)
                        .F6(F6)
                        .Lprime(Lprime)
                        .L1A(L1A)
                        .L1B(L1B)
                        .L2(L2)
                        .LA(LA)
                        .LAR(LAR)
                        .S1(S1)
                        .S1A(S1A)
                        .S2(S2)
                        .S2A(S2A)
                        .S3(S3)
                        .S4(S4)
                        .S5(S5)
                        .STEG(STEG)
                        .STIDD(STIDD)
                        .T1(T1)
                        .T2(T2)
                        .feuille_double(feuilleDouble)
                        .feuille_brouillon(feuilleBrouillon)
                        .feuille_intercalaire(feuilleIntercalaire)
                        .build();

                dtos.add(dto);

                // Entity DB
                RepartitionFeuilleCES entity = RepartitionFeuilleCES.builder()
                        .centreEcrit(centreEcrit)
                        .academia(academia)
                        .effectif(effectif)
                        .centreExamen(centreExam)
                        .session(session)
                        .F6(F6)
                        .Lprime(Lprime)
                        .L1A(L1A)
                        .L1B(L1B)
                        .L2(L2)
                        .LA(LA)
                        .LAR(LAR)
                        .S1(S1)
                        .S1A(S1A)
                        .S2(S2)
                        .S2A(S2A)
                        .S3(S3)
                        .S4(S4)
                        .S5(S5)
                        .STEG(STEG)
                        .STIDD(STIDD)
                        .T1(T1)
                        .T2(T2)
                        .feuille_double(feuilleDouble)
                        .feuille_brouillon(feuilleBrouillon)
                        .feuille_intercalaire(feuilleIntercalaire)
                        .build();

                entities.add(entity);
            }

        } catch (Exception e) {
            System.out.println(e);
        }

        repartitionFeuilleCESRepository.saveAll(entities);

        return dtos.stream()
                .sorted(Comparator.comparing(RepartitionFeuilleCESDTO::getCentreExamen))
                .toList();
    }

    public void unionCollections()
    {
        List<Document> collectionA = mongoTemplate.findAll(Document.class, "repartition_feuille_CEP");
        log.info("collectionA size = {}", collectionA.size());
        log.info("Retrouver tirage CS...");
        List<Document> collectionB = mongoTemplate.findAll(Document.class, "repartition_feuille_CES");
        log.info("collectionB size = {}", collectionB.size());
        List<Document> all = new ArrayList<>();
        collectionA.forEach(doc -> doc.remove("_id"));
        collectionB.forEach(doc -> doc.remove("_id"));
        all.addAll(collectionA);
        all.addAll(collectionB);
        log.info("Total à insérer = {}", all.size());
        mongoTemplate.dropCollection("fusion_repartition_feuille");
        mongoTemplate.insert(all, "fusion_repartition_feuille");
    }

    // Méthode utilitaire
    private long countSerie(List<SourceCandidat> groupe, String serie) {
        return groupe.stream()
                .filter(c -> c.getSerie() != null && c.getSerie().equalsIgnoreCase(serie))
                .count();
    }


    public Map<String, List<FusionRepartitionFeuille>> getRepCPByAcademie()
    {
        List<FusionRepartitionFeuille> allUsers = fusionRepartitionFeuilleRepository.findAll();
        return allUsers
                .stream()
                .filter(p -> p.getAcademia() != null)
                .collect(Collectors.groupingBy(s -> s.getAcademia()));
    }

    public Map<String, List<RepartitionFeuilleCES>> getRepCSByAcademie()
    {
        List<RepartitionFeuilleCES> allUsers = repartitionFeuilleCESRepository.findAll();
        return allUsers
                .stream()
                .filter(p -> p.getAcademia() != null)
                .collect(Collectors.groupingBy(s -> s.getAcademia()));
    }
}
