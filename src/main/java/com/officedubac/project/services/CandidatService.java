package com.officedubac.project.services;

import com.officedubac.project.dto.*;
import com.officedubac.project.exception.ResourceAlreadyExists;
import com.officedubac.project.exception.TechnicalException;
import com.officedubac.project.models.*;
import com.officedubac.project.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.security.auth.Subject;
import java.text.Normalizer;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CandidatService
{
    @Autowired
    private final CandidatRepository candidatRepository;
    @Autowired
    private final SujetRepository sujetRepository;
    @Autowired
    private final CentreEtatCivilRepository centreEtatCivilRepository;
    @Autowired
    private final EtablissementRepository etablissementRepository;
    @Autowired
    private final TypeCandidatRepository typeCandidatRepository;
    @Autowired
    private final SerieRepository serieRepository;
    @Autowired
    private final NationalityRepository nationalityRepository;
    @Autowired
    private final OptionRepository optionRepository;
    @Autowired
    private final SpecialiteRepository specialiteRepository;
    @Autowired
    private final MatiereRepository matiereRepository;
    @Autowired
    private final ProgrammationRepository programmationRepository;
    @Autowired
    private final RejetRepository rejetRepository;
    @Autowired
    private final EtatDeVersementRepository etatDeVersementRepository;
    @Autowired
    private final CompteDroitInscriptionRepository compteDroitInscriptionRepository;
    @Autowired
    private final ConcoursGeneralRepository concoursGeneralRepository;
    @Autowired
    private final SpecialiteCGSRepository specialiteCGSRepository;
    @Autowired
    private final AuditService auditService;
    @Autowired
    private final ImportDataService importDataService;

    @Autowired
    private MongoTemplate mongoTemplate;

    public List<Serie> getSerie()
    {
        List<Serie> series = serieRepository.findAll();
        return series;
    }

    public List<CentreEtatCivil> getCEC()
    {
        List<CentreEtatCivil> cecs = centreEtatCivilRepository.findAll();
        return cecs;
    }

    public List<Nationality> getNationality()
    {
        List<Nationality> nats = nationalityRepository.findAll();
        return nats;
    }

    public List<Option> getOptions()
    {
        List<Option> options = optionRepository.findAll();
        return options;
    }

    public List<Specialite> getSpecialites()
    {
        List<Specialite> specialites = specialiteRepository.findAll();
        return specialites;
    }

    public List<Etablissement> getEtablissement()
    {
        List<Etablissement> etabs = etablissementRepository.findAll();
        return etabs;
    }

    public List<Matiere> getMatiereFromSerie(String serieId)
    {
        System.out.println("Serie Id"+serieId);
        List<Matiere> mat = matiereRepository.findBySerie_Id(serieId);
        return mat;
    }

    public List<Programmation> getProgs()
    {
        return programmationRepository.findAll();
    }

    public Programmation getDerniereProg() {
        return programmationRepository.findTopByOrderByIdDesc();
    }

    private boolean equalsIgnoreCaseAndAccent(String a, String b) {
        if (a == null && b == null) return true;
        if (a == null || b == null) return false;

        String normA = Normalizer.normalize(a, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase();

        String normB = Normalizer.normalize(b, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase();

        return normA.equals(normB);
    }

    @Transactional
    public BaseMorte createCandidat(CandidatDTO candidatDTO) {

        // Formatage de la date de naissance
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate dateNaissance = LocalDate.parse(candidatDTO.getDate_birth(), formatter);

        // Génération du numéro d’enrôlement et du code dossier
        String code = "NO" +
                candidatDTO.getCentreEtatCivil().getCode() +
                candidatDTO.getYear_registry_num() +
                candidatDTO.getRegistry_num() +
                candidatDTO.getSession();

        String dosNumberBySessionAndEtab = candidatDTO.getDosNumber() + "_" +
                candidatDTO.getSession() + "_" +
                (candidatDTO.getEtablissement() != null
                        ? candidatDTO.getEtablissement().getCode()
                        : "NULL");

        // Construction de l'entité candidat
        Candidat cdt = Candidat.builder()
                .dosNumber(candidatDTO.getDosNumber())
                .session(candidatDTO.getSession())
                .firstname(candidatDTO.getFirstname())
                .lastname(candidatDTO.getLastname())
                .date_birth(dateNaissance)
                .place_birth(candidatDTO.getPlace_birth())
                .gender(candidatDTO.getGender())
                .phone1(candidatDTO.getPhone1())
                .phone2(candidatDTO.getPhone2())
                .email(candidatDTO.getEmail())
                .centreEtatCivil(candidatDTO.getCentreEtatCivil())
                .centreExamen(candidatDTO.getCentreExamen())
                .year_registry_num(candidatDTO.getYear_registry_num())
                .registry_num(candidatDTO.getRegistry_num())
                .bac_do_count(candidatDTO.getBac_do_count())
                .year_bfem(candidatDTO.getYear_bfem())
                .subject(candidatDTO.getSubject())
                .handicap(candidatDTO.isHandicap())
                .type_handicap(candidatDTO.getType_handicap())
                .eps(candidatDTO.getEps())
                .cdt_is_cgs(candidatDTO.isCdt_is_cgs())
                .typeCandidat(candidatDTO.getTypeCandidat())
                .etablissement(candidatDTO.getEtablissement())
                .serie(candidatDTO.getSerie())
                .nationality(candidatDTO.getNationality())
                .matiere1(candidatDTO.getMatiere1())
                .matiere2(candidatDTO.getMatiere2())
                .matiere3(candidatDTO.getMatiere3())
                .countryBirth(candidatDTO.getCountryBirth())
                .eprFacListA(candidatDTO.getEprFacListA())
                .eprFacListB(candidatDTO.getEprFacListB())
                .origine_bfem(candidatDTO.getOrigine_bfem())
                .numEnrolement(code)
                .dosNumber_by_session_and_etablissement(dosNumberBySessionAndEtab)
                .alreadyBac(candidatDTO.isAlreadyBac())
                .codeEnrolementEC(candidatDTO.getCodeEnrolementEC())
                .decision(0)
                .build();

        // Vérification d’un éventuel redoublant
        BaseMorte bm = importDataService.checkRedoublantByEtatCivil(
                cdt.getCentreEtatCivil().getCode(),
                cdt.getYear_registry_num(),
                cdt.getRegistry_num()
        );

        log.info("Yeaahh" + cdt.isAlreadyBac());

        log.info("Yeaahh 2" + candidatDTO.isAlreadyBac());

        if (bm == null || (cdt.isAlreadyBac() && bm.getExclusionDuree() == 0))
        {
            // Aucun redoublant détecté
            log.info("{} {}", cdt.getYear_registry_num(), cdt.getDate_birth().getYear());

            Programmation prg = programmationRepository.findByEdition(cdt.getSession());

            // Vérifications d’unicité (numéro, dossier, téléphone, email)
            Query query = new Query();
            query.addCriteria(new Criteria().orOperator(
                    Criteria.where("numEnrolement").is(cdt.getNumEnrolement()),
                    Criteria.where("dosNumber_by_session_and_etablissement").is(cdt.getDosNumber_by_session_and_etablissement()),
                    Criteria.where("phone1").is(cdt.getPhone1()),
                    Criteria.where("email").is(cdt.getEmail())
            ));

            List<Candidat> existing = mongoTemplate.find(query, Candidat.class);
            log.info("Candidats existants = {}", existing);

            if (!existing.isEmpty()) {
                for (Candidat c : existing) {
                    if (c.getNumEnrolement().equals(cdt.getNumEnrolement())) {
                        throw new BusinessResourceException(
                                "numEnrolement-error",
                                "Vos références d'État civil ont déjà été utilisées",
                                HttpStatus.CONFLICT
                        );
                    }
                    if (c.getDosNumber_by_session_and_etablissement().equals(cdt.getDosNumber_by_session_and_etablissement())) {
                        throw new BusinessResourceException(
                                "dosNumber-error",
                                "Le numéro de dossier est déjà utilisé pour cette session et cet établissement",
                                HttpStatus.CONFLICT
                        );
                    }
                    if (c.getPhone1().equals(cdt.getPhone1())) {
                        throw new BusinessResourceException(
                                "phone1-error",
                                "Le numéro de téléphone est déjà utilisé",
                                HttpStatus.CONFLICT
                        );
                    }
                    if (c.getEmail().equals(cdt.getEmail())) {
                        throw new BusinessResourceException(
                                "email-error",
                                "L'adresse email est déjà utilisée",
                                HttpStatus.CONFLICT
                        );
                    }
                }
            }

            // Vérification cohérence date de naissance / année de déclaration
            int birthYear = cdt.getDate_birth().getYear();
            log.info("Résultat comparaison = {}", cdt.getYear_registry_num() < birthYear);

            if (cdt.getYear_registry_num() < birthYear) {
                throw new BusinessResourceException(
                        "year-error",
                        "L'année de déclaration ne doit pas être antérieure à celle de naissance",
                        HttpStatus.NOT_ACCEPTABLE
                );
            }

            // Vérifications sur l’année du BFEM selon le type d’établissement
            String typeEtab = cdt.getEtablissement().getTypeEtablissement().getCode();

            if ("EFI".equals(typeEtab) && cdt.getYear_bfem() > prg.getBfem_IfEPI()) {
                throw new BusinessResourceException(
                        "diff-year-bfem-error1",
                        "Pour ce candidat, l'année d'obtention du BFEM ne doit pas être supérieure à " + prg.getBfem_IfEPI(),
                        HttpStatus.NOT_ACCEPTABLE
                );
            }

            if ("I".equals(typeEtab) && cdt.getYear_bfem() > prg.getBfem_IfI()) {
                throw new BusinessResourceException(
                        "diff-year-bfem-error2",
                        "Pour ce candidat, l'année d'obtention du BFEM ne doit pas être supérieure à " + prg.getBfem_IfI(),
                        HttpStatus.NOT_ACCEPTABLE
                );
            }

            // Insertion finale du candidat
            candidatRepository.insert(cdt);

            return null;
        }

        return bm != null ? bm : null;
    }

    public ConcoursGeneral createConcoursGeneral(ConcoursGeneralDTO concoursGeneralDTO) {
        try {
            // Récupération de l'établissement
            Etablissement etab = etablissementRepository.findByCode(concoursGeneralDTO.getEtablissement());
            if (etab == null) {
                throw new IllegalArgumentException("Établissement non trouvé pour le code : " + concoursGeneralDTO.getEtablissement());
            }

            // Conversion de la date de naissance
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate dateNaissance = LocalDate.parse(concoursGeneralDTO.getDate_birth(), formatter);

            // Construction de l'entité ConcoursGeneral
            ConcoursGeneral cgs = ConcoursGeneral.builder()
                    .firstname(concoursGeneralDTO.getFirstname())
                    .lastname(concoursGeneralDTO.getLastname())
                    .date_birth(dateNaissance)
                    .place_birth(concoursGeneralDTO.getPlace_birth())
                    .phone(concoursGeneralDTO.getPhone())
                    .gender(concoursGeneralDTO.getGender())
                    .classe_0(concoursGeneralDTO.getClasse_0())
                    .note_student_disc(concoursGeneralDTO.getNote_student_disc())
                    .classe_1(concoursGeneralDTO.getClasse_1())
                    .note_classe_disc(concoursGeneralDTO.getNote_classe_disc())
                    .firstname_prof(concoursGeneralDTO.getFirstname_prof())
                    .lastname_prof(concoursGeneralDTO.getLastname_prof())
                    .serie(concoursGeneralDTO.getSerie())
                    .session(concoursGeneralDTO.getSession())
                    .etablissement(etab)
                    .level(concoursGeneralDTO.getLevel())
                    .specialite(concoursGeneralDTO.getSpecialite())
                    .build();

            // Sauvegarde dans la base
            return concoursGeneralRepository.save(cgs);

        } catch (Exception ex) {
            log.error("Erreur lors de la création du candidat : {}", ex.getMessage(), ex);
            throw new RuntimeException("Impossible de créer le candidat.", ex);
        }
    }

    public ConcoursGeneral updateConcoursGeneral(String idCgs, ConcoursGeneralDTO concoursGeneralDTO)
    {
        ConcoursGeneral cgs = concoursGeneralRepository.findById(idCgs).orElse(null);
        if (cgs != null)
        {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate dateNaissance = LocalDate.parse(concoursGeneralDTO.getDate_birth(), formatter);

            cgs.setFirstname(concoursGeneralDTO.getFirstname());
            cgs.setLastname(concoursGeneralDTO.getLastname());
            cgs.setDate_birth(dateNaissance);
            cgs.setPlace_birth(concoursGeneralDTO.getPlace_birth());
            cgs.setPhone(concoursGeneralDTO.getPhone());
            cgs.setGender(concoursGeneralDTO.getGender());
            cgs.setClasse_0(concoursGeneralDTO.getClasse_0());
            cgs.setNote_student_disc(concoursGeneralDTO.getNote_student_disc());
            cgs.setClasse_1(concoursGeneralDTO.getClasse_1());
            cgs.setNote_classe_disc(concoursGeneralDTO.getNote_classe_disc());
            cgs.setFirstname_prof(concoursGeneralDTO.getFirstname_prof());
            cgs.setLastname_prof(concoursGeneralDTO.getLastname_prof());
            cgs.setSpecialite(concoursGeneralDTO.getSpecialite());
            return concoursGeneralRepository.save(cgs);
        }
        else
        {
            // Handle the case where the user is not found
            throw new NotFoundException("Objet with ID " + idCgs + " is not found");
        }
    }

    public void deleteCGS(String idCdt)
    {
        ConcoursGeneral delete_cgs = concoursGeneralRepository.findById(idCdt).orElse(null);

        if (delete_cgs != null)
        {
            concoursGeneralRepository.delete(delete_cgs);
        }
        else
        {
            // Handle the case where the user is not found
            throw new NotFoundException("User with ID " + idCdt + " is not found");
        }
    }

    public Map<String, List<ConcoursGeneral>> getCdtCgsGroupedByClasse(String etablissementId, Long session)
    {
        List<ConcoursGeneral> allCandidates = concoursGeneralRepository.findByEtablissementIdAndSession(etablissementId, session);
        return allCandidates.stream()
                .filter(s -> s.getLevel() != null)
                .collect(Collectors.groupingBy(s -> s.getLevel()));
    }

    public long countCandidats(String specialite, String classe, Integer session, String etablissementCode) {
        Query query = new Query();

        if (specialite != null && !specialite.isEmpty()) {
            query.addCriteria(Criteria.where("specialite").is(specialite));
        }
        if (classe != null && !classe.isEmpty()) {
            query.addCriteria(Criteria.where("level").is(classe));
        }
        if (session != null) {
            query.addCriteria(Criteria.where("session").is(session));
        }
        if (etablissementCode != null && !etablissementCode.isEmpty()) {
            query.addCriteria(Criteria.where("etablissement.code").is(etablissementCode));
        }

        return mongoTemplate.count(query, "concours_general");
        // "concoursGeneral" = nom de ta collection
    }

/**
    @Transactional
    public void assignSpecialiteToCgs(SpecialiteAndCgsDTO specialiteAndCgsDTO)
    {
        try
        {
            String specialite = specialiteAndCgsDTO.getSpecialite().getSpecialite();
            String level = specialiteAndCgsDTO.getSpecialite().getClasse();

            List<ConcoursGeneral> candidatsAvecCeSujet =
                    Optional.ofNullable(
                            concoursGeneralRepository.findByLevelAndSpecialite(level, specialite)
                    ).orElse(Collections.emptyList());

            Set<String> idsCandidatsCibles = new HashSet<>(specialiteAndCgsDTO.getCandidats());

            System.out.println(idsCandidatsCibles);

            List<ConcoursGeneral> candidatsAMettreAJour = new ArrayList<>();

            if (!candidatsAvecCeSujet.isEmpty())
            {
                for (ConcoursGeneral c : candidatsAvecCeSujet) {
                    if (!idsCandidatsCibles.contains(c.getId())) {
                        c.setSpecialite("");
                        candidatsAMettreAJour.add(c);
                    }
                }
            }


            for (String idCdt : idsCandidatsCibles) {
                ConcoursGeneral cgs = concoursGeneralRepository.findById(idCdt).orElse(null);
                if (cgs != null && !specialite.equals(cgs.getSpecialite())) {
                    cgs.setSpecialite(specialite);
                    candidatsAMettreAJour.add(cgs);
                }
            }

            if (!candidatsAMettreAJour.isEmpty()) {
                concoursGeneralRepository.saveAll(candidatsAMettreAJour);
            }
        }
        catch (Exception ex)
        {
            log.error(ex.getMessage());
        }
    }
    */


    public List<ConcoursGeneral> getCdtCgsBySpecialite(String level, String specialite)
    {
        return concoursGeneralRepository.findByLevelAndSpecialite(level, specialite);
    }


    public EtatDeVersement createEV(EtatDeVersementDTO etatDeVersementDTO)
    {
        Etablissement etab = etablissementRepository.findById(etatDeVersementDTO.getEtablissement()).orElse(null);
        EtatDeVersement ev = EtatDeVersement.builder()
                .session(etatDeVersementDTO.getSession())
                .file_id(etatDeVersementDTO.getFile_id())
                .count_5000(etatDeVersementDTO.getCount_5000())
                .count_1000_EF(etatDeVersementDTO.getCount_1000_EF())
                .etablissement(etab)
                .date_deposit(LocalDateTime.now())
                .build();

        return etatDeVersementRepository.save(ev);
    }

    public EtatDeVersement updateEV(String idEV, VignetteAddDTO vignetteAddDTO, String firstname, String lastname) {
        EtatDeVersement update_ev = etatDeVersementRepository.findById(idEV).orElse(null);

        if (update_ev == null) {
            throw new NotFoundException("Objet with ID " + idEV + " is not found");
        }

        // Vérifier si les valeurs sont identiques
        boolean same1000 = Objects.equals(update_ev.getCount_1000_EF(), vignetteAddDTO.getV1000());
        boolean same5000 = Objects.equals(update_ev.getCount_5000(), vignetteAddDTO.getV5000());

        if (same1000 && same5000)
        {
            return update_ev;
        }

        // Mise à jour uniquement si changement
        update_ev.setCount_1000_EF(vignetteAddDTO.getV1000());
        update_ev.setCount_5000(vignetteAddDTO.getV5000());
        update_ev.setOperator(firstname + " " + lastname);
        update_ev.setDate_ops(LocalDateTime.now());
        update_ev.setState(true);

        EtatDeVersement ev = etatDeVersementRepository.save(update_ev);

        // --- Mise à jour du compte associé ---
        CompteDroitsInscription cmpt_droit_insc = compteDroitInscriptionRepository
                .findByEtablissementIdAndSession(ev.getEtablissement().getId(), ev.getSession());

        if (cmpt_droit_insc == null)
        {
            CompteDroitsInscription compteEtab = new CompteDroitsInscription();
            compteEtab.setSession(ev.getSession());
            compteEtab.setEtablissement(ev.getEtablissement());
            compteEtab.setCount_1000_EF(ev.getCount_1000_EF());
            compteEtab.setCount_5000(ev.getCount_5000());
            compteDroitInscriptionRepository.save(compteEtab);
        }
        else
        {
            cmpt_droit_insc.setCount_1000_EF(ev.getCount_1000_EF());
            cmpt_droit_insc.setCount_5000(ev.getCount_5000());
            compteDroitInscriptionRepository.save(cmpt_droit_insc);
        }

        return ev;
    }


    public EtatDeVersement updateEV_(String idEV, String motif, String f, String l) {
        EtatDeVersement update_ev = etatDeVersementRepository.findById(idEV).orElse(null);

        Integer v5000 = update_ev.getCount_5000();
        Integer v1000 = update_ev.getCount_1000_EF();

        if (update_ev == null) {
            throw new NotFoundException("Objet with ID " + idEV + " is not found");
        }

        // Mise à jour uniquement si changement
        update_ev.setCount_1000_EF(0);
        update_ev.setCount_5000(0);
        update_ev.setCorrecteur(f + " " + l);
        update_ev.setMotif_correction_vignettes(motif);
        update_ev.setDate_correction(LocalDateTime.now());

        EtatDeVersement ev = etatDeVersementRepository.save(update_ev);

        // --- Mise à jour du compte associé ---
        CompteDroitsInscription cmpt_droit_insc = compteDroitInscriptionRepository
                .findByEtablissementIdAndSession(ev.getEtablissement().getId(), ev.getSession());

        cmpt_droit_insc.setCount_1000_EF(cmpt_droit_insc.getCount_1000_EF() - v1000);
        cmpt_droit_insc.setCount_5000(cmpt_droit_insc.getCount_5000() - v5000);
        cmpt_droit_insc.setEnabled(false);
        cmpt_droit_insc.setRepresentative("");
        compteDroitInscriptionRepository.save(cmpt_droit_insc);
        return ev;
    }


    public CompteDroitsInscription enabledReception(String idCmptDroitInsc, AutorisationReception autorisationReception)
    {
        CompteDroitsInscription update_cdi = compteDroitInscriptionRepository.findById(idCmptDroitInsc).orElse(null);
        if (update_cdi != null)
        {
            update_cdi.setRepresentative(autorisationReception.getRepresentative());
            update_cdi.setEnabled(autorisationReception.isEnabled());
            return compteDroitInscriptionRepository.save(update_cdi);
        }
        else
        {
            // Handle the case where the user is not found
            throw new NotFoundException("Objet with ID " + idCmptDroitInsc + " is not found");
        }
    }

    @Transactional
    public Candidat updateCandidat(String idCdt, CandidatDTO candidatDTO, String login, String ip) {
        Candidat update_cdt = candidatRepository.findById(idCdt).orElse(null);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate dateNaissance = LocalDate.parse(candidatDTO.getDate_birth(), formatter);

        Programmation prg = programmationRepository.findByEdition(candidatDTO.getSession());

        if (update_cdt != null) {
            // Génération des champs calculés
            String code = "NO" + candidatDTO.getCentreEtatCivil().getCode()
                    + candidatDTO.getYear_registry_num()
                    + candidatDTO.getRegistry_num()
                    + candidatDTO.getSession();

            String dosNumberBySessionAndEtab = candidatDTO.getDosNumber() + "_"
                    + candidatDTO.getSession() + "_"
                    + (candidatDTO.getEtablissement() != null ? candidatDTO.getEtablissement().getCode() : "NULL");

            // Vérification des doublons en excluant le candidat en cours
            Query query = new Query();
            query.addCriteria(new Criteria().andOperator(
                    Criteria.where("_id").ne(idCdt), // exclure le candidat en cours
                    new Criteria().orOperator(
                            Criteria.where("numEnrolement").is(code),
                            Criteria.where("dosNumber_by_session_and_etablissement").is(dosNumberBySessionAndEtab),
                            Criteria.where("phone1").is(candidatDTO.getPhone1()),
                            Criteria.where("email").is(candidatDTO.getEmail())
                    )
            ));

            List<Candidat> existing = mongoTemplate.find(query, Candidat.class);

            if (!existing.isEmpty()) {

                for (Candidat c : existing) {
                    if (c.getNumEnrolement().equals(code)) {
                        throw new BusinessResourceException(
                                "numEnrolement-error",
                                "Vos références d'Etat Civil ont été déjà utilisées",
                                HttpStatus.CONFLICT
                        );
                    }
                    if (c.getDosNumber_by_session_and_etablissement().equals(dosNumberBySessionAndEtab)) {
                        throw new BusinessResourceException(
                                "dosNumber-error",
                                "Le numéro de dossier est déjà utilisé pour cette session et cet établissement",
                                HttpStatus.CONFLICT
                        );
                    }
                    if (c.getPhone1().equals(candidatDTO.getPhone1())) {
                        throw new BusinessResourceException(
                                "phone1-error",
                                "Le numéro de téléphone est déjà utilisé",
                                HttpStatus.CONFLICT
                        );
                    }
                    if (c.getEmail().equals(candidatDTO.getEmail())) {
                        throw new BusinessResourceException(
                                "email-error",
                                "L'email est déjà utilisé",
                                HttpStatus.CONFLICT
                        );
                    }


                }
            }

            if (candidatDTO.getYear_bfem() > prg.getBfem_IfEPI() && Objects.equals(candidatDTO.getEtablissement().getTypeEtablissement().getCode(), "EFI"))
            {
                throw new BusinessResourceException(
                        "diff-year-bfem-error1",
                        "Pour ce candidat, l'année d'obtention du BFEM ne doit pas être supérieure à " + prg.getBfem_IfEPI(),
                        HttpStatus.NOT_ACCEPTABLE
                );
            }

            if (candidatDTO.getYear_bfem() > prg.getBfem_IfI() && Objects.equals(candidatDTO.getEtablissement().getTypeEtablissement().getCode(), "I"))
            {
                throw new BusinessResourceException(
                        "diff-year-bfem-error2",
                        "Pour ce candidat, l'année d'obtention du BFEM ne doit pas être supérieure à " + prg.getBfem_IfI(),
                        HttpStatus.NOT_ACCEPTABLE
                );
            }

            // Mise à jour des champs
            update_cdt.setDosNumber(candidatDTO.getDosNumber());
            update_cdt.setFirstname(candidatDTO.getFirstname());
            update_cdt.setLastname(candidatDTO.getLastname());
            // update_cdt.setSession(candidatDTO.getSession()); // tu avais commenté
            update_cdt.setDate_birth(dateNaissance);
            update_cdt.setPlace_birth(candidatDTO.getPlace_birth());
            update_cdt.setGender(candidatDTO.getGender());
            update_cdt.setPhone1(candidatDTO.getPhone1());
            update_cdt.setPhone2(candidatDTO.getPhone2());
            update_cdt.setEmail(candidatDTO.getEmail());
            update_cdt.setCentreEtatCivil(candidatDTO.getCentreEtatCivil());
            update_cdt.setYear_registry_num(candidatDTO.getYear_registry_num());
            update_cdt.setRegistry_num(candidatDTO.getRegistry_num());
            update_cdt.setBac_do_count(candidatDTO.getBac_do_count());
            update_cdt.setYear_bfem(candidatDTO.getYear_bfem());
            update_cdt.setSubject(candidatDTO.getSubject());
            update_cdt.setHandicap(candidatDTO.isHandicap());
            update_cdt.setType_handicap(candidatDTO.getType_handicap());
            update_cdt.setEps(candidatDTO.getEps());
            update_cdt.setCdt_is_cgs(candidatDTO.isCdt_is_cgs());
            update_cdt.setTypeCandidat(candidatDTO.getTypeCandidat());
            update_cdt.setEtablissement(candidatDTO.getEtablissement());
            update_cdt.setSerie(candidatDTO.getSerie());
            update_cdt.setNationality(candidatDTO.getNationality());
            update_cdt.setCountryBirth(candidatDTO.getCountryBirth());
            update_cdt.setMatiere1(candidatDTO.getMatiere1());
            update_cdt.setMatiere2(candidatDTO.getMatiere2());
            update_cdt.setMatiere3(candidatDTO.getMatiere3());
            update_cdt.setEprFacListA(candidatDTO.getEprFacListA());
            update_cdt.setEprFacListB(candidatDTO.getEprFacListB());
            update_cdt.setOrigine_bfem(candidatDTO.getOrigine_bfem());
            update_cdt.setNumEnrolement(code);
            update_cdt.setDosNumber_by_session_and_etablissement(dosNumberBySessionAndEtab);
            update_cdt.setCentreExamen(candidatDTO.getCentreExamen());

            return candidatRepository.save(update_cdt);
        } else {
            throw new NotFoundException("User with ID " + idCdt + " is not found");
        }
    }

    public void deleteCandidat(String idCdt)
    {
        Candidat delete_cdt = candidatRepository.findById(idCdt).orElse(null);

        if (delete_cdt != null)
        {
            candidatRepository.delete(delete_cdt);
        }
        else
        {
            // Handle the case where the user is not found
            throw new NotFoundException("User with ID " + idCdt + " is not found");
        }
    }

    public List<Candidat> getCandidats()
    {
        return candidatRepository.findAll();
    }

    public List<Candidat> getCandidatsParEtablissement(String etablissementId, Long session) {
        return candidatRepository.findByEtablissementIdAndSession(etablissementId, session);
    }

    public Map<String, List<Candidat>> getCandidatsGroupedBySerie(String etablissementId, Long session)
    {
        List<Candidat> allCandidates = candidatRepository.findByEtablissementIdAndSession(etablissementId, session);
        return allCandidates.stream()
                .filter(s -> s.getSerie() != null && s.getSerie().getCode() != null)
                .collect(Collectors.groupingBy(s -> s.getSerie().getCode()));
    }

    public Map<String, List<Candidat>> getCandidatsGroupedBySujet(String etablissementId, Long session)
    {
        List<Candidat> allCandidates = candidatRepository.findByEtablissementIdAndSession(etablissementId, session);
        return allCandidates.stream()
                .filter(s -> s.getSubject() != null)
                .collect(Collectors.groupingBy(s -> s.getSubject()));
    }

    public List<Sujet> getSujets()
    {
        return sujetRepository.findAll();
    }

    public Sujet createSujet(SujetDTO sujetDTO) {
        Etablissement etab = etablissementRepository.findById(sujetDTO.getEtab_id())
                .orElseThrow(() -> new RuntimeException("Etablissement introuvable"));
        Specialite spec = specialiteRepository.findById(sujetDTO.getSpec_id())
                .orElseThrow(() -> new RuntimeException("Spécialité introuvable"));

        // Chercher le dernier sujet de la même session et du même établissement
        Optional<Sujet> lastSujetOpt = sujetRepository
                .findTopBySessionAndEtablissementOrderByNumSujetDesc(
                        sujetDTO.getSession(), etab);

        int nextNumSujet = 1; // valeur par défaut si nouvelle session ou nouvel etab
        if (lastSujetOpt.isPresent()) {
            nextNumSujet = lastSujetOpt.get().getNumSujet() + 1;
        }

        Sujet sj = Sujet.builder()
                .wording(sujetDTO.getWording())
                .numSujet(nextNumSujet)
                .etablissement(etab)
                .specialite(spec)
                .session(sujetDTO.getSession())
                .build();

        return sujetRepository.save(sj);
    }


    public String updateSujet(String idS, SujetDTO sujetDTO)
    {
        Sujet update_sujet = sujetRepository.findById(idS).orElse(null);
        assert update_sujet != null;
        boolean ok = candidatRepository.existsBySubject(update_sujet.getWording());

        if (ok)
        {
            return "Impossible";
        }
        else
        {
            Specialite spec = specialiteRepository.findById(sujetDTO.getSpec_id()).orElse(null);
            update_sujet.setWording(sujetDTO.getWording());
            update_sujet.setSpecialite(spec);
            sujetRepository.save(update_sujet);
            return "Ok";
        }

    }

    public String deleteSujet(String idS) {
        Sujet sujet = sujetRepository.findById(idS).orElse(null);

        if (sujet == null)
        {
            return "NotFound";
        }

        String sujetWording = sujet.getWording();
        List<Candidat> candidats = candidatRepository.findBySubject(sujetWording);

        if (!candidats.isEmpty())
        {
            for (Candidat c : candidats)
            {
                c.setSubject(null);
            }
            candidatRepository.saveAll(candidats);
        }
        sujetRepository.delete(sujet);
        return "OK";
    }

    public List<Sujet> getSujetsParEtablissement(String etablissementId, Long session) {
        return sujetRepository.findByEtablissementIdAndSession(etablissementId, session);
    }

    public List<SpecialiteCGS> getAllSpecialite() {
        return specialiteCGSRepository.findAll();
    }

    public List<ConcoursGeneral> getCdtsCgsParEtablissement(String etablissementId, Long session) {
        return concoursGeneralRepository.findByEtablissementIdAndSession(etablissementId, session);
    }

    public Candidat getCandidat(int extrait)
    {
        System.out.println("NUM REGISTRE"+extrait);
        Candidat cdt = candidatRepository.findByRegistryNum(extrait);
        return cdt;
    }

    public List<Candidat> getCandidatsBySubject(String sujet)
    {
        return candidatRepository.findBySubject(sujet);
    }


    public CentreEtatCivil getCECByName(String nameCEC)
    {
        System.out.println("Centre Etat Civil"+nameCEC);
        CentreEtatCivil cec_ = centreEtatCivilRepository.findByName(nameCEC);
        return cec_;
    }

    @Transactional
    public void assignSubjectToCandidate(SujetAndCandidatDTO sujetAndCandidatDTO) {
        String subject = sujetAndCandidatDTO.getSubject().getWording();

        List<Candidat> candidatsAvecCeSujet = candidatRepository.findBySubject(subject);

        Set<String> idsCandidatsCibles = new HashSet<>(sujetAndCandidatDTO.getCandidats());

        List<Candidat> candidatsAMettreAJour = new ArrayList<>();

        for (Candidat c : candidatsAvecCeSujet) {
            if (!idsCandidatsCibles.contains(c.getId())) {
                c.setSubject("");
                candidatsAMettreAJour.add(c);
            }
        }

        for (String idCdt : idsCandidatsCibles) {
            Candidat cdt = candidatRepository.findById(idCdt).orElse(null);
            if (cdt != null && !subject.equals(cdt.getSubject())) {
                cdt.setSubject(subject);
                candidatsAMettreAJour.add(cdt);
            }
        }

        if (!candidatsAMettreAJour.isEmpty()) {
            candidatRepository.saveAll(candidatsAMettreAJour);
        }
    }


    /**
    public List<Candidat> getFilteredCandidats(String etablissementId, String serieCode, Long session) {
        return candidatRepository.findByEtablissementIdAndSerieCodeAndSession(etablissementId, serieCode, session);
    }**/

    public List<Candidat> getFilteredCandidats(String etablissementId, Long session) {
        return candidatRepository.findByEtablissementIdAndSession(etablissementId, session);
    }

    public List<Candidat> getFilteredCandidatsForPdf(String etablissementId, Long session, String sortBy, String serieCode)
    {
        List<Candidat> candidats =  candidatRepository.findByEtablissementIdAndSessionAndSerieCode(etablissementId, session, serieCode);

        // Trier en Java après récupération (option simple)
        if ("lastname".equalsIgnoreCase(sortBy))
        {
            candidats.sort(Comparator.comparing(Candidat::getLastname, String.CASE_INSENSITIVE_ORDER));
        }
        else if ("dos_number".equalsIgnoreCase(sortBy))
        {
            candidats.sort(Comparator.comparingInt(c -> Integer.parseInt(c.getDosNumber())));
        }

        return candidats;

    }

    public List<Candidat> getAllCandidatsForPdf(String etablissementId, Long session, String sortBy)
    {
        List<Candidat> candidats =  candidatRepository.findByEtablissementIdAndSession(etablissementId, session);

        if ("lastname".equalsIgnoreCase(sortBy))
        {
            candidats.sort(Comparator.comparing(Candidat::getLastname, String.CASE_INSENSITIVE_ORDER));
        }
        else if ("dosNumber".equalsIgnoreCase(sortBy))
        {
            candidats.sort(Comparator.comparingInt(c -> Integer.parseInt(c.getDosNumber())));
        }

        return candidats;

    }


    public List<Candidat> getFilteredCandidatsForPdfOL(String etablissementId, Long session, String serieCode)
    {
        List<Candidat> candidats =  candidatRepository.findByEtablissementIdAndSessionAndSerieCodeAndDecision(etablissementId, session, serieCode, 1);
        candidats.sort(Comparator.comparing(Candidat::getLastname, String.CASE_INSENSITIVE_ORDER));
        return candidats;

    }

    public List<Candidat> getAllCandidatsForPdfOL(String etablissementId, Long session)
    {
        List<Candidat> candidats =  candidatRepository.findByEtablissementIdAndSessionAndDecision(etablissementId, session, 1);
        candidats.sort(Comparator.comparing(Candidat::getLastname, String.CASE_INSENSITIVE_ORDER));
        return candidats;

    }

    public List<Candidat> getFilteredCandidatsForPdfSujet(String etablissementId, Long session, String subject)
    {
        List<Candidat> candidats =  candidatRepository.findByEtablissementIdAndSessionAndSubject(etablissementId, session, subject);
        candidats.sort(Comparator.comparing(Candidat::getLastname, String.CASE_INSENSITIVE_ORDER));
        return candidats;

    }

    public List<ConcoursGeneral> getFilteredCandidatsForPdfCGS(String etablissementId, Long session, String specialite, String level)
    {
        List<ConcoursGeneral> candidats =  concoursGeneralRepository.findByEtablissementIdAndSessionAndSpecialiteAndLevel(etablissementId, session, specialite, level);
        return candidats;

    }

    public List<Candidat> getFilteredCandidatsForPdfReject(String etablissementId, Long session)
    {
        return candidatRepository.findByEtablissementIdAndSession(etablissementId, session);
    }

    public List<StatsDTO> getNombreCandidatsParSerie(String codeEtab, Long session)
    {
        List<Candidat> candidats = candidatRepository.findAll();

        Map<String, Long> groupedBySerie = candidats.stream()
                .filter(c -> c.getSession() != null && c.getSession().equals(session))
                .filter(c -> c.getSerie() != null && c.getSerie().getCode() != null && codeEtab.equals(c.getEtablissement().getId()))
                .collect(Collectors.groupingBy(
                        c -> c.getSerie().getCode(),
                        Collectors.counting()
                ));

        return groupedBySerie.entrySet().stream()
                .map(entry -> new StatsDTO(entry.getKey(), entry.getValue()))
                .sorted((a, b) -> Long.compare(b.getData(), a.getData())) // Tri décroissant
                .collect(Collectors.toList());
    }

    public List<StatsDTO> getNombreCandidatsParSexe(String codeEtab, Long session)
    {
        List<Candidat> candidats = candidatRepository.findAll();

        Map<Object, Long> groupedBySexe = candidats.stream()
                .filter(c -> c.getSession() != null && c.getSession().equals(session))
                .filter(c -> c.getGender() != null && codeEtab.equals(c.getEtablissement().getId()))
                .collect(Collectors.groupingBy(
                        Candidat::getGender,
                        Collectors.counting()
                ));


        return groupedBySexe.entrySet().stream()
                .map(entry -> new StatsDTO(entry.getKey().toString(), entry.getValue()))
                .sorted((a, b) -> Long.compare(b.getData(), a.getData())) // Tri décroissant
                .collect(Collectors.toList());
    }

    public List<StatsDTO> getNombreCandidatsParEPS(String codeEtab, Long session)
    {
        List<Candidat> candidats = candidatRepository.findAll();

        Map<String, Long> groupedBySerie = candidats.stream()
                .filter(c -> c.getSession() != null && c.getSession().equals(session))
                .filter(c -> c.getEps() != null && codeEtab.equals(c.getEtablissement().getId()))
                .collect(Collectors.groupingBy(
                        Candidat::getEps,
                        Collectors.counting()
                ));

        return groupedBySerie.entrySet().stream()
                .map(entry -> new StatsDTO(entry.getKey(), entry.getValue()))
                .sorted((a, b) -> Long.compare(b.getData(), a.getData())) // Tri décroissant
                .collect(Collectors.toList());
    }

    public List<StatsDTO> getNombreCandidatsParHandicap(String codeEtab, Long session)
    {
        List<Candidat> candidats = candidatRepository.findAll();

        Map<String, Long> groupedBySerie = candidats.stream()
                .filter(c -> c.getSession() != null && c.getSession().equals(session))
                .filter(c -> c.getType_handicap() != null && codeEtab.equals(c.getEtablissement().getId()))
                .collect(Collectors.groupingBy(
                        Candidat::getType_handicap,
                        Collectors.counting()
                ));

        return groupedBySerie.entrySet().stream()
                .map(entry -> new StatsDTO(entry.getKey(), entry.getValue()))
                .sorted((a, b) -> Long.compare(b.getData(), a.getData())) // Tri décroissant
                .collect(Collectors.toList());
    }

    public List<StatsDTO> getNombreCandidatsParEF(String codeEtab, Long session)
    {
        List<Candidat> candidats = candidatRepository.findAll();

        Map<String, Long> groupedByEF = new HashMap<>();

        for (Candidat c : candidats) {
            if (c.getSession() != null && c.getSession().equals(session) && codeEtab.equals(c.getEtablissement().getId())) {
                if (!c.getEprFacListA().name().equals("Aucun")) {
                    groupedByEF.merge(c.getEprFacListA().toString(), 1L, Long::sum);
                }
                if (c.getEprFacListB() != null) {
                    groupedByEF.merge(c.getEprFacListB().getName(), 1L, Long::sum);
                }
            }
        }

        return groupedByEF.entrySet().stream()
                .map(entry -> new StatsDTO(entry.getKey(), entry.getValue()))
                .sorted((a, b) -> Long.compare(b.getData(), a.getData()))
                .collect(Collectors.toList());
    }

    public List<StatsDTO> getNombreCandidatsParOptions(String codeEtab, Long session)
    {
        List<Candidat> candidats = candidatRepository.findAll();

        Map<String, Long> groupedByOPT = new HashMap<>();

        for (Candidat c : candidats) {
            if (c.getSession() != null && c.getSession().equals(session) && codeEtab.equals(c.getEtablissement().getId())) {
                if (c.getMatiere1() != null) {
                    groupedByOPT.merge(c.getMatiere1().getName(), 1L, Long::sum);
                }
                if (c.getMatiere2() != null) {
                    groupedByOPT.merge(c.getMatiere2().getName(), 1L, Long::sum);
                }
                if (c.getMatiere3() != null) {
                    groupedByOPT.merge(c.getMatiere3().getName(), 1L, Long::sum);
                }
            }
        }

        return groupedByOPT.entrySet().stream()
                .map(entry -> new StatsDTO(entry.getKey(), entry.getValue()))
                .sorted((a, b) -> Long.compare(b.getData(), a.getData()))
                .collect(Collectors.toList());
    }


    public Candidat updateDecision(String idCdt, CandidatDecisionDTO candidatDecisionDTO, String login, String ip)
    {
        try
        {
            Candidat update_cdt = candidatRepository.findById(idCdt).orElse(null);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate dateNaissance = LocalDate.parse(candidatDecisionDTO.getDate_birth(), formatter);

            /***
             TypeCandidat typeCandidat = typeCandidatRepository.findByName(candidatDTO.getType_candidat_name());
             Etablissement etab = etablissementRepository.findByName(candidatDTO.getEtab_name());
             CentreEtatCivil cec = centreEtatCivilRepository.findByName(candidatDTO.getCentre_etat_civil_name());
             Serie serie = serieRepository.findByCode(candidatDTO.getSerie_name());
             Nationality nat = nationalityRepository.findByName(candidatDTO.getNationality_name());
             ***/

            //List<String> opta = candidatDTO.getOptions();

            Map<String, Object> diffs = auditService.getDifferences(update_cdt, candidatDecisionDTO);

            if (update_cdt != null) {
                List<Rejet> rj = new ArrayList<>(); // ← Initialisation ici

                for (String r : candidatDecisionDTO.getMotif()) {
                    Rejet rejet = rejetRepository.findByName(r);
                    if (rejet != null)
                    {
                        rj.add(rejet);
                    }
                }
                if (candidatDecisionDTO.getDecision() == 1)
                {
                    rj.clear();
                }
                update_cdt.setDosNumber(candidatDecisionDTO.getDosNumber());
                update_cdt.setFirstname(candidatDecisionDTO.getFirstname());
                update_cdt.setLastname(candidatDecisionDTO.getLastname());
                //update_cdt.setSession(candidatDTO.getSession());
                update_cdt.setDate_birth(dateNaissance);
                update_cdt.setPlace_birth(candidatDecisionDTO.getPlace_birth());
                update_cdt.setGender(candidatDecisionDTO.getGender());
                update_cdt.setPhone1(candidatDecisionDTO.getPhone1());
                update_cdt.setPhone2(candidatDecisionDTO.getPhone2());
                update_cdt.setEmail(candidatDecisionDTO.getEmail());
                update_cdt.setCentreEtatCivil(candidatDecisionDTO.getCentreEtatCivil());
                update_cdt.setYear_registry_num(candidatDecisionDTO.getYear_registry_num());
                update_cdt.setRegistry_num(candidatDecisionDTO.getRegistry_num());
                update_cdt.setBac_do_count(candidatDecisionDTO.getBac_do_count());
                update_cdt.setYear_bfem(candidatDecisionDTO.getYear_bfem());
                update_cdt.setSubject(candidatDecisionDTO.getSubject());
                update_cdt.setHandicap(candidatDecisionDTO.isHandicap());
                update_cdt.setType_handicap(candidatDecisionDTO.getType_handicap());
                update_cdt.setEps(candidatDecisionDTO.getEps());
                update_cdt.setCdt_is_cgs(candidatDecisionDTO.isCdt_is_cgs());
                //update_cdt.setTypeCandidat(candidatDecisionDTO.getTypeCandidat());
                //update_cdt.setEtablissement(candidatDecisionDTO.getEtablissement());
                update_cdt.setSerie(candidatDecisionDTO.getSerie());
                update_cdt.setNationality(candidatDecisionDTO.getNationality());
                update_cdt.setCountryBirth(candidatDecisionDTO.getCountryBirth());
                update_cdt.setMatiere1(candidatDecisionDTO.getMatiere1());
                update_cdt.setMatiere2(candidatDecisionDTO.getMatiere2());
                update_cdt.setMatiere3(candidatDecisionDTO.getMatiere3());
                update_cdt.setEprFacListA(candidatDecisionDTO.getEprFacListA());
                update_cdt.setEprFacListB(candidatDecisionDTO.getEprFacListB());
                update_cdt.setOrigine_bfem(candidatDecisionDTO.getOrigine_bfem());

                update_cdt.setDecision(candidatDecisionDTO.getDecision());
                update_cdt.setRejets(rj);
                update_cdt.setOperator(candidatDecisionDTO.getOperator());
                update_cdt.setDateOperation(LocalDateTime.now());

                Candidat saved = candidatRepository.save(update_cdt);

                auditService.logOperation(
                        "Candidat Modifié",
                        idCdt,
                        "PUT",
                        Map.of("modified", diffs), // old values/new values regroupés
                        login,
                        ip
                );

                return saved;

            }

            else
            {
                // Handle the case where the user is not found
                throw new NotFoundException("User with ID " + idCdt + " is not found");
            }
        } catch (Exception e) {
            e.printStackTrace(); // ← à lire dans la console
            throw new TechnicalException("Erreur technique : " + e.getMessage());
        }


    }

    public List<Candidat> getCandidatsValidesParSession(Long session) {
        return candidatRepository.findBySessionAndDecision(session, 1);
    }

    public List<Rejet> getRejets() {
        return rejetRepository.findAll();
    }

    public List<EtatDeVersement> getFilteredEVs(String etablissementId, Long session)
    {
        return etatDeVersementRepository.findByEtablissementIdAndSession(etablissementId, session, Sort.by(Sort.Direction.DESC, "date_deposit"));
    }

    public List<EtatDeVersement> getFilteredEVs_(Long session)
    {
        return etatDeVersementRepository.findBySession(session, Sort.by(Sort.Direction.DESC, "date_deposit"));
    }

    public CompteDroitsInscription getCompteDroitsInscription(String establishmentId, Long session) {
        return compteDroitInscriptionRepository.findByEtablissementIdAndSession(establishmentId, session);
    }

    public long countBySessionAndEtablissementIdWhereEprFacListANotNullAndNotAucun(Long session, String etablissementId) {
        Query query = Query.query(
                new Criteria().andOperator(
                        Criteria.where("session").is(session),
                        Criteria.where("etablissement.id").is(etablissementId),
                        Criteria.where("eprFacListA").nin("Aucun")
                )
        );

        // soit Candidat.class si tu as la classe mappée, soit le nom de collection "candidat"
        return mongoTemplate.count(query, Candidat.class);
    }

    public Map<String, Long> compterFacultatives(String etablissementId, Long session)
    {
        long countA = countBySessionAndEtablissementIdWhereEprFacListANotNullAndNotAucun(session, etablissementId);
        long countB = candidatRepository.countBySessionAndEtablissement_IdAndEprFacListBNotNull(session, etablissementId);
        long cdt = candidatRepository.countBySessionAndEtablissement_Id(session, etablissementId);

        return Map.of(
                "facListA", countA,
                "facListB", countB,
                "candidats", cdt
        );
    }

    public Candidat checkDoublon(int yearRegistryNum, String registryNum, String cec, Long session)
    {
        return candidatRepository.findCandidate(yearRegistryNum, registryNum, cec, session);
    }

    public Candidat checkDoublonNumTel(String phone1, Long session)
    {
        return candidatRepository.findByPhone1AndSession(phone1, session);
    }

    public Candidat checkDoublonEmail(String email, Long session)
    {
        return candidatRepository.findByEmailAndSession(email, session);
    }

    public Candidat checkByDosNumber(String dosNumber, Long session, String etablissementId)
    {
        return candidatRepository.findByDosNumberAndSessionAndEtablissement_Id(dosNumber, session, etablissementId);
    }

    public List<EtablissementSummaryReception> summarize(Long session)
    {
        List<Candidat> candidats = candidatRepository.findBySession(session);

        Map<Etablissement, EtablissementSummaryReception> map = new HashMap<>();

        for (Candidat cdt : candidats) {
            Etablissement etabName = (cdt.getEtablissement() != null) ? cdt.getEtablissement() : null;
            map.putIfAbsent(etabName, new EtablissementSummaryReception());
            EtablissementSummaryReception summary = map.get(etabName);
            summary.setEtablissement(etabName);

            if (cdt.getDecision() == 1)
            {
                summary.setDecision1(summary.getDecision1() + 1);
            }
            else
                if (cdt.getDecision() == 2)
                {
                summary.setDecision2(summary.getDecision2() + 1);
                }
                else
                {
                    summary.setDecision0(summary.getDecision0() + 1);
                }

            if (cdt.getOperator() != null) {
                summary.getOperators().add(cdt.getOperator());
            }
        }
        return new ArrayList<>(map.values());
    }

    public EtablissementSummaryReception_ summarize_(Long session)
    {
        List<Candidat> candidats = candidatRepository.findBySession(session);

        EtablissementSummaryReception_ ok = new EtablissementSummaryReception_();

        for (Candidat cdt : candidats) {

            if (cdt.getDecision() == 1)
            {
                ok.setDecision1(ok.getDecision1() + 1);
            }
            if (cdt.getDecision() == 2)
            {
                ok.setDecision2(ok.getDecision2() + 1);
            }
            if (cdt.getDecision() == 0)
            {
                ok.setDecision0(ok.getDecision0() + 1);
            }
        }
        return ok;
    }






    /***
    private CandidatDTO convertToDTO(Candidat candidat) {
        CandidatDTO dto = new CandidatDTO();

        dto.setDos_number(candidat.getDos_number());
        dto.setFirstname(candidat.getFirstname());
        dto.setLastname(candidat.getLastname());
        dto.setSession(candidat.getSession());
        dto.setDate_birth(candidat.getDate_birth());
        dto.setPlace_birth(candidat.getPlace_birth());
        dto.setGender(candidat.getGender());
        dto.setPhone1(candidat.getPhone1());
        dto.setPhone2(candidat.getPhone2());
        dto.setEmail(candidat.getEmail());
        dto.setYear_registry_num(candidat.getYear_registry_num());
        dto.setRegistry_num(candidat.getRegistry_num());
        dto.setBac_do_count(candidat.getBac_do_count());
        dto.setYear_bfem(candidat.getYear_bfem());
        dto.setSubject(candidat.getSubject());
        dto.setHandicap(candidat.isHandicap());
        dto.setType_handicap(candidat.getType_handicap());
        dto.setEps(candidat.isEps());
        dto.setCdt_is_cgs(candidat.isCdt_is_cgs());
        dto.setDecision(candidat.getDecision());
        dto.setOptions(candidat.getOptions());

        // Attributs dérivés d'autres entités liées (null-check recommandé)
        if (candidat.getEtablissement() != null) {
            dto.setEtab_name(candidat.getEtablissement().getName());
        }

        if (candidat.getCentreEtatCivil() != null) {
            dto.setCentre_etat_civil_name(candidat.getCentreEtatCivil().getName());
        }

        if (candidat.getTypeCandidat() != null) {
            dto.setType_candidat_name(candidat.getTypeCandidat().getName());
        }

        if (candidat.getSerie() != null) {
            dto.setSerie_name(candidat.getSerie().getName());
        }

        if (candidat.getNationality() != null) {
            dto.setNationality_name(candidat.getNationality().getName());
        }

        if (candidat.getConcoursGeneral() != null) {
            dto.setCgs_id(candidat.getConcoursGeneral().getId());
        }

        return dto;
    }
     ***/







}
