package com.officedubac.project.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.officedubac.project.dto.*;
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
import java.util.function.Function;
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

    private final EtablissementRepository etablissementRepository;


    // Méthode utilitaire
    private long countSerie(List<SourceCandidat> groupe, String serie)
    {
        return groupe.stream()
                .filter(c -> c.getSerie() != null && c.getSerie().equalsIgnoreCase(serie))
                .count();
    }


    private long countJurys(List<SourceCandidat> groupe)
    {
        // System.out.println("Taille du groupe : " + groupe.size());

        List<Integer> jurysNonNuls = groupe.stream()
                .map(SourceCandidat::getJury)
                .filter(Objects::nonNull)
                .toList();

        // System.out.println("Jurys non nuls : " + jurysNonNuls.size());
        // System.out.println("Jurys distincts : " + jurysNonNuls.stream().distinct().count());
        // System.out.println("Valeurs des jurys : " + jurysNonNuls);

        return jurysNonNuls.stream().distinct().count();
    }

    public List<RepartitionFeuilleCEPDTO> repartitionParCEP()
    {
        List<SourceCandidat> candidats = sourceCandidatRepository.findLightCandidats();
        // Regroupement par CEP
        Map<String, List<SourceCandidat>> candidatsParCEP = candidats.stream().collect(Collectors.groupingBy(SourceCandidat::getCentreEcritPrincipal));
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
                long nbJury = countJurys(groupe);
                long effectif = groupe.size();

                long F6 = countSerie(groupe, "F6");
                long Lprime = countSerie(groupe, "L'1");
                long L1A = countSerie(groupe, "L1A");
                long L1B = countSerie(groupe, "L1B");
                long L2 = countSerie(groupe, "L2");
                long LA = countSerie(groupe, "LA");
                long LAR = countSerie(groupe, "L-AR");
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

                RepartitionFeuilleCEPDTO dto = RepartitionFeuilleCEPDTO.builder()
                        .centreEcrit(centreEcrit)
                        .academia(academia)
                        .effectif(effectif)
                        .centreExamen(centreExam)
                        .session(session)
                        .nbJury(nbJury)
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
                RepartitionFeuilleCEP entity = RepartitionFeuilleCEP.builder()
                        .centreEcrit(centreEcrit)
                        .academia(academia)
                        .effectif(effectif)
                        .centreExamen(centreExam)
                        .session(session)
                        .nbJury(nbJury)
                        .cp(true)
                        .cs(false)
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

                System.out.println("Entity avant sauvegarde - nbJury: " + entity.getNbJury());

                entities.add(entity);
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        // Sauvegarde en base
        repartitionFeuilleCEPRepository.saveAll(entities);

        // Retour trié
        return dtos.stream()
                .sorted(Comparator.comparing(RepartitionFeuilleCEPDTO::getCentreExamen))
                .toList();
    }

    public List<RepartitionFeuilleCESDTO> repartitionParCS() {

        List<SourceCandidat> candidats = sourceCandidatRepository.findCandidatsWithCentreSecondaire();

        Map<String, List<SourceCandidat>> candidatsParCS = candidats.stream().collect(Collectors.groupingBy(SourceCandidat::getCentreEcritSecondaire));

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
                long nbJury = countJurys(groupe);
                long effectif = groupe.size();

                long F6 = countSerie(groupe, "F6");
                long Lprime = countSerie(groupe, "L'1");
                long L1A = countSerie(groupe, "L1A");
                long L1B = countSerie(groupe, "L1B");
                long L2 = countSerie(groupe, "L2");
                long LA = countSerie(groupe, "LA");
                long LAR = countSerie(groupe, "L-AR");
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
                        .nbJury(nbJury)
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
                        .nbJury(nbJury)
                        .cp(false)
                        .cs(true)
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

        }
        catch (Exception e)
        {
            e.printStackTrace();
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


    public RepartitionCompleteFDTO construire(FusionRepartitionFeuille rep)
    {
        return RepartitionCompleteFDTO.builder()
                .centre(rep.getCentreEcrit())
                .academie(rep.getAcademia())
                .session(rep.getSession())
                .effectif(rep.getEffectif())
                .nbJury(rep.getNbJury())
                .cp(rep.getCp())
                .cs(rep.getCs())
                .localite(rep.getCentreExamen())
                .fb(rep.getFeuille_brouillon())
                .ic(rep.getFeuille_intercalaire())
                .fd(rep.getFeuille_double())
                .build();
    }
}
