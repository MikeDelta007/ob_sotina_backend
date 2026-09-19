package com.officedubac.project.controllers;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.*;
import com.officedubac.project.dto.*;
import com.officedubac.project.models.*;
import com.officedubac.project.repository.*;
import com.officedubac.project.services.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.PreDestroy;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Sort;
import org.springframework.format.datetime.DateFormatter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.text.Collator;
import java.text.DecimalFormat;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


@CrossOrigin("*")
@RestController
@Slf4j
@RequestMapping("/api/v1/pdf")
@RequiredArgsConstructor
@Tag(name="PDF Controller", description = "Endpoints responsables de la gestion des PDF")
public class PdfController
{

    @Autowired
    private FusionRepartitionTirageRepository repository;

    @Autowired
    private RepartitionTirageCGSRepository repositorycgs;

    @Autowired
    private RegleMatiereRepository repo;

    @Autowired
    private RegleMatiereCGSRepository repocgs;

    @Autowired
    private FusionRepartitionTirageRepository ftr;

    @Autowired
    private FusionRepartitionFeuilleRepository ffeuil;

    @Autowired
    private TirageJuryMatService tirageJuryMatService;

    @Autowired
    private DecompteFeuilleJuryService decompteFeuilleJuryService;

    @Autowired
    private PdfStatService pdfStatService;

    @GetMapping(value = "/releve-stat", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> genererPdfStatistiques() {
        try {
            byte[] pdfBytes = pdfStatService.genererTableauStatistiques();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("inline", "statistiques_bac_2025.pdf");
            headers.setContentLength(pdfBytes.length);

            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // ============================================================
    // RESSOURCES PARTAGEES : logo reduit, cache des QR codes, pool de generation
    // ============================================================

    /**
     * Taille maximale (px) du logo embarque dans les PDF. Le logo source fait 1920x1280 px
     * pour un affichage a 70 pt : le decoder puis le recompresser pour CHAQUE PDF coutait
     * ~0,4 s et ~30 Ko par fichier. 400 px = ~400 dpi a 70 pt, visuellement identique.
     */
    private static final int LOGO_TAILLE_MAX_PX = 400;

    /** PDF generes en parallele : on laisse un coeur a Tomcat et MongoDB. */
    private static final int NB_THREADS_PDF =
            Math.max(2, Math.min(Runtime.getRuntime().availableProcessors() - 1, 8));

    /** Matieres generees a l'avance : borne la memoire (PDF en attente d'ecriture dans le ZIP). */
    private static final int FENETRE_PDF = NB_THREADS_PDF * 2;

    private static final AtomicInteger COMPTEUR_THREADS_PDF = new AtomicInteger();

    private static final ExecutorService POOL_PDF = Executors.newFixedThreadPool(NB_THREADS_PDF, r -> {
        Thread t = new Thread(r, "pdf-etiquettes-" + COMPTEUR_THREADS_PDF.incrementAndGet());
        t.setDaemon(true);
        return t;
    });

    /**
     * QR codes deja encodes (PNG). Le contenu ne depend que du centre : il est identique pour
     * toutes les matieres, donc on ne l'encode (ZXing + PNG) qu'une seule fois.
     */
    private static final Map<String, QrBits> CACHE_QR = new ConcurrentHashMap<>();
    private static final int CACHE_QR_TAILLE_MAX = 10_000;

    /** QR encode : dimensions et pixels a 1 bit (DeviceGray : 0 = noir, 1 = blanc). */
    private record QrBits(int largeur, int hauteur, byte[] bits) {}

    /**
     * BitMatrix ZXing -> pixels bruts 1 bit, lignes alignees sur l'octet, exactement les memes
     * pixels que MatrixToImageWriter (module sombre = noir, le reste = blanc).
     */
    private static byte[] versBitsGris(BitMatrix matrice) {
        int largeur = matrice.getWidth();
        int hauteur = matrice.getHeight();
        int octetsParLigne = (largeur + 7) / 8;
        byte[] bits = new byte[octetsParLigne * hauteur];
        Arrays.fill(bits, (byte) 0xFF); // tout blanc (y compris les bits de bourrage de fin de ligne)

        for (int y = 0; y < hauteur; y++) {
            for (int x = 0; x < largeur; x++) {
                if (matrice.get(x, y)) {
                    bits[y * octetsParLigne + (x >> 3)] &= (byte) ~(0x80 >> (x & 7)); // noir
                }
            }
        }
        return bits;
    }

    // ================= LOGO EN CACHE (charge et reduit une seule fois) =================
    private final byte[] logoBytes;

    public PdfController(/* vos autres dépendances injectées */) throws IOException {
        byte[] logoOriginal = new ClassPathResource("images/sn.png").getContentAsByteArray();
        this.logoBytes = reduireLogo(logoOriginal, LOGO_TAILLE_MAX_PX);
        prechaufferPolices();
    }

    @PreDestroy
    public void fermerPoolPdf() {
        POOL_PDF.shutdownNow();
    }

    /**
     * Cree une fois les polices standard : OpenPDF les met dans un cache statique non
     * synchronise a la premiere utilisation, ce qui n'est pas sur si plusieurs threads
     * s'y prennent en meme temps. Ensuite le cache n'est plus qu'en lecture.
     */
    private static void prechaufferPolices() {
        try {
            BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, false);
            BaseFont.createFont(BaseFont.HELVETICA_BOLD, BaseFont.WINANSI, false);
        } catch (Exception e) {
            log.warn("Préchauffage des polices impossible : {}", e.getMessage());
        }
    }

    /**
     * Reduit l'image de sorte que sa plus grande dimension ne depasse pas tailleMax.
     * Reduction par paliers de /2 max (bien meilleure qualite qu'un seul passage).
     * L'image d'origine est renvoyee telle quelle si elle est deja assez petite.
     */
    private static byte[] reduireLogo(byte[] source, int tailleMax) throws IOException {
        BufferedImage src = ImageIO.read(new java.io.ByteArrayInputStream(source));
        if (src == null || Math.max(src.getWidth(), src.getHeight()) <= tailleMax) {
            return source;
        }

        double ratio = (double) tailleMax / Math.max(src.getWidth(), src.getHeight());
        int largeurCible = Math.max(1, (int) Math.round(src.getWidth() * ratio));
        int hauteurCible = Math.max(1, (int) Math.round(src.getHeight() * ratio));
        int type = src.getColorModel().hasAlpha() ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB;

        BufferedImage courante = src;
        int largeur = src.getWidth();
        int hauteur = src.getHeight();
        while (largeur > largeurCible || hauteur > hauteurCible) {
            largeur = Math.max(largeurCible, largeur / 2);
            hauteur = Math.max(hauteurCible, hauteur / 2);

            BufferedImage etape = new BufferedImage(largeur, hauteur, type);
            java.awt.Graphics2D g = etape.createGraphics();
            g.setRenderingHint(java.awt.RenderingHints.KEY_INTERPOLATION,
                    java.awt.RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g.setRenderingHint(java.awt.RenderingHints.KEY_RENDERING,
                    java.awt.RenderingHints.VALUE_RENDER_QUALITY);
            g.drawImage(courante, 0, 0, largeur, hauteur, null);
            g.dispose();
            courante = etape;
        }

        ByteArrayOutputStream sortie = new ByteArrayOutputStream();
        ImageIO.write(courante, "png", sortie);
        log.info("Logo des étiquettes réduit : {}x{} ({} Ko) -> {}x{} ({} Ko)",
                src.getWidth(), src.getHeight(), source.length / 1024,
                largeurCible, hauteurCible, sortie.size() / 1024);
        return sortie.toByteArray();
    }

    private List<FusionRepartitionTirage> centresTriesParAcademie() {
        return repository.findAll()
                .stream()
                .sorted(Comparator.comparing(
                        FusionRepartitionTirage::getAcademia,
                        Comparator.nullsLast(String::compareTo)
                ))
                .toList();
    }

    @Operation(summary = "Génération de l'étiquette de table - Format A4 Paysage")
    @GetMapping("/generate-etiquette-paysage")
    public void generateEtiquettes(
            @RequestParam(value = "matiere") String matiere,
            @RequestParam(value = "groupe", required = false) String groupe,
            @RequestParam(value = "session", required = false, defaultValue = "0") int session,
            HttpServletResponse response) throws IOException, DocumentException {

        // ================= NORMALISATION MATIERE =================
        if (matiere == null || matiere.trim().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        if ("TOUTES_LES_MATIERES".equalsIgnoreCase(matiere)) {
            generateToutesLesMatieres(groupe, session, response);
            return;
        }

        List<FusionRepartitionTirage> sortedList = centresTriesParAcademie();

        if (sortedList.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            return;
        }

        // ================= REGLE =================
        RegleMatiere regle = repo.findAll().stream()
                .filter(r -> r.getCode() != null && r.getCode().equalsIgnoreCase(matiere))
                .findFirst()
                .orElse(null);

        if (regle == null) {
            log.warn("Aucune règle trouvée pour {}", matiere);
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            return;
        }

        // Meme routine que pour le ZIP : une seule implementation a maintenir
        byte[] pdfBytes = generatePdfPourMatiere(matiere, groupe, session, sortedList, regle);

        if (pdfBytes.length == 0) {
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            return;
        }

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "inline; filename=etiquettes_bac.pdf");
        response.setContentLength(pdfBytes.length);
        response.getOutputStream().write(pdfBytes);
        response.getOutputStream().flush();
    }

    /** Un centre a imprimer pour une matiere, avec l'effectif du groupe demande. */
    private record CentreAImprimer(FusionRepartitionTirage data, double effectif) {}

    // ============================================================
    // GENERATION D'UN PDF POUR UNE MATIERE (endpoint matiere + zip)
    // Thread-safe : n'utilise que ses parametres et des ressources en lecture seule.
    // ============================================================
    private byte[] generatePdfPourMatiere(
            String matiere,
            String groupe,
            int session,
            List<FusionRepartitionTirage> sortedList,
            RegleMatiere regle
    ) throws IOException, DocumentException {

        // Le groupe, la date et l'horaire ne dependent que de la regle : calcules une fois
        // (et non a chaque centre, comme avant, avec un log par centre si groupe invalide)
        final boolean secondGroupe;
        final String grp;
        final String date;
        final String horaire;

        if ("1ER".equalsIgnoreCase(groupe)) {
            secondGroupe = false;
            grp = "PREMIER GROUPE";
            date = Optional.ofNullable(regle.getDate1()).orElse("");
            horaire = Optional.ofNullable(regle.getHeure1()).orElse("");
        } else if ("2ND".equalsIgnoreCase(groupe)) {
            secondGroupe = true;
            grp = "SECOND GROUPE";
            date = Optional.ofNullable(regle.getDate2()).orElse("");
            horaire = Optional.ofNullable(regle.getHeure2()).orElse("");
        } else {
            log.warn("Groupe non reconnu pour {} : {}", matiere, groupe);
            return new byte[0];
        }

        // Premiere passe : quels centres ont des candidats ? Si aucun, on ne cree meme pas
        // le document (pas de logo a decoder, pas de PdfWriter) et on renvoie vide.
        List<CentreAImprimer> aImprimer = new ArrayList<>();
        for (FusionRepartitionTirage data : sortedList) {
            if (data.getMatieres() == null) {
                continue;
            }

            GroupeMatiere gm = data.getMatieres().get(matiere);
            if (gm == null) {
                continue;
            }

            Double effectif = secondGroupe ? gm.getSecondGroupe() : gm.getPremierGroupe();
            if (effectif == null || effectif <= 0) {
                continue;
            }

            aImprimer.add(new CentreAImprimer(data, effectif));
        }

        // Ne JAMAIS close() un document sans page : OpenPDF leve "The document has no pages"
        if (aImprimer.isEmpty()) {
            return new byte[0];
        }

        // Taille estimee (~2,2 Ko par page + entete) : evite les recopies successives du tampon
        ByteArrayOutputStream output = new ByteArrayOutputStream(50_000 + aImprimer.size() * 2_200);

        Document document = new Document(PageSize.A4.rotate(), 36f, 36f, 10f, 10f);
        PdfWriter.getInstance(document, output);
        document.open();

        Font helv10 = FontFactory.getFont(FontFactory.HELVETICA, 10, Font.NORMAL);
        Font helv12Bold = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 60);
        Font helv14 = FontFactory.getFont(FontFactory.HELVETICA, 17, Font.BOLD);
        Font helv22 = FontFactory.getFont(FontFactory.HELVETICA, 22, Font.NORMAL);
        Font helv24Bold = FontFactory.getFont(FontFactory.HELVETICA, 24, Font.BOLD);
        Font helv16Bold = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
        Font helv16 = FontFactory.getFont(FontFactory.HELVETICA, 16);
        Font helv26Bold = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 30);

        Image logo = Image.getInstance(logoBytes); // logo deja reduit, pas de lecture disque
        logo.scaleToFit(70f, 70f);

        String series = (regle.getSeries() != null && !regle.getSeries().isEmpty())
                ? String.join(" - ", regle.getSeries())
                : "";

        String libelleNormalise = Optional.ofNullable(regle.getValeur()).orElse(matiere);

        boolean premierePage = true;

        for (CentreAImprimer centre : aImprimer) {

            Image qrCode = generateQRCode(buildQRCodeContent__(centre.data()), 120, 120);

            if (!premierePage) {
                document.newPage();
            }

            generateEtiquettePage(
                    session,
                    regle.getChamp(),
                    secondGroupe ? centre.effectif() : 0.0,
                    document,
                    logo,
                    centre.data(),
                    libelleNormalise,
                    series,
                    centre.effectif(),
                    grp,
                    date,
                    horaire,
                    helv10, helv12Bold, helv14, helv22,
                    helv24Bold, helv16Bold, helv26Bold, helv16, qrCode
            );

            premierePage = false;
        }

        document.close(); // une seule fois
        return output.toByteArray();
    }

    // ============================================================
    // GENERATION DU ZIP - PDF produits en parallele, ecrits dans l'ordre alphabetique
    // ============================================================
    private void generateToutesLesMatieres(
            String groupe,
            int session,
            HttpServletResponse response
    ) throws IOException, DocumentException {

        // Le groupe est indispensable : sans lui aucune matière ne produit d'étiquette
        // et on renverrait un zip vide en 200, indiagnosticable côté front.
        if (!"1ER".equalsIgnoreCase(groupe) && !"2ND".equalsIgnoreCase(groupe)) {
            log.warn("Groupe non reconnu pour TOUTES_LES_MATIERES : {}", groupe);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        List<FusionRepartitionTirage> sortedList = centresTriesParAcademie();

        if (sortedList.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            return;
        }

        List<RegleMatiere> toutesLesRegles = repo.findAll();
        if (toutesLesRegles == null || toutesLesRegles.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            return;
        }

        // On écarte d'entrée les matières qui ne concernent pas le groupe demandé
        // (une matière 2NDGRP n'a rien à faire dans l'export du 1er groupe) puis on
        // trie sur l'intitulé pour que les répertoires du ZIP sortent dans l'ordre
        // alphabétique. Collator FRENCH : "ÉCONOMIE" se classe bien avec les E.
        Collator collator = Collator.getInstance(Locale.FRENCH);
        collator.setStrength(Collator.PRIMARY);

        List<RegleMatiere> regles = toutesLesRegles.stream()
                .filter(r -> r != null && r.getCode() != null && !r.getCode().trim().isEmpty())
                .filter(r -> matiereConcerneeParGroupe(r, groupe))
                .sorted(Comparator.comparing(PdfController::libelleMatiere, collator))
                .toList();

        if (regles.isEmpty()) {
            log.warn("Aucune matière ne concerne le groupe {}", groupe);
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            return;
        }

        // Le ZIP est d'abord ecrit dans un fichier temporaire, puis envoye avec son Content-Length.
        // Sans taille annoncee (Transfer-Encoding: chunked), Chrome coupe un XHR/axios en blob a
        // 10 Mio (net::ERR_FAILED, "Code HTTP : undefined") : mesure avec Chrome 153, la meme
        // reponse avec Content-Length est telechargee en entier.
        // Bonus : aucun en-tete n'est envoye avant la fin de la generation, donc on peut encore
        // repondre 204 (rien a generer) ou 500 (vraie erreur) au lieu d'une archive vide.
        long debut = System.nanoTime();
        int nombrePdf = 0;
        Set<String> cheminsUtilises = new HashSet<>();

        // Une tache par matiere. On n'en garde que FENETRE_PDF d'avance sur l'ecriture :
        // tous les coeurs travaillent, mais les PDF en attente restent en nombre borne.
        List<Future<byte[]>> taches = new ArrayList<>(regles.size());
        int aSoumettre = 0;

        Path fichierZip = Files.createTempFile("etiquettes_", ".zip");

        try {
            try (ZipOutputStream zip = new ZipOutputStream(
                    new BufferedOutputStream(Files.newOutputStream(fichierZip)))) {

                // Les PDF sont deja compresses (flux Flate) : inutile de dépenser du CPU
                // a recompresser au maximum.
                zip.setLevel(Deflater.BEST_SPEED);

                for (int i = 0; i < regles.size(); i++) {

                    while (aSoumettre < regles.size() && aSoumettre < i + FENETRE_PDF) {
                        RegleMatiere aGenerer = regles.get(aSoumettre++);
                        taches.add(POOL_PDF.submit(() -> generatePdfPourMatiere(
                                aGenerer.getCode().trim(), groupe, session, sortedList, aGenerer)));
                    }

                    RegleMatiere regle = regles.get(i);
                    String codeMatiere = regle.getCode().trim();
                    String libelleMatiere = libelleMatiere(regle);

                    byte[] pdfBytes;
                    try {
                        pdfBytes = taches.get(i).get();
                    } catch (ExecutionException e) {
                        Throwable cause = e.getCause() != null ? e.getCause() : e;
                        log.error("Échec de génération PDF pour la matière {} ({}) : {}",
                                codeMatiere, libelleMatiere, cause.getMessage(), cause);
                        continue;
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        log.warn("Génération du ZIP interrompue après {} PDF", nombrePdf);
                        response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
                        return;
                    } finally {
                        taches.set(i, null); // libere le PDF des qu'il est consomme
                    }

                    if (pdfBytes == null || pdfBytes.length == 0) {
                        log.info("Aucune étiquette générée pour la matière {} ({})", codeMatiere, libelleMatiere);
                        continue;
                    }

                    String repertoire = sanitizeFileName(libelleMatiere);
                    String nomPdf = sanitizeFileName(codeMatiere)
                            + "_" + groupe.toUpperCase() + "_groupe.pdf";
                    String cheminZip = repertoire + "/" + nomPdf;

                    // Deux règles peuvent porter le même code/libellé : sans ce garde-fou
                    // putNextEntry lève une ZipException.
                    if (!cheminsUtilises.add(cheminZip)) {
                        String base = cheminZip.substring(0, cheminZip.length() - 4);
                        int suffixe = 2;
                        while (!cheminsUtilises.add(base + "_" + suffixe + ".pdf")) {
                            suffixe++;
                        }
                        cheminZip = base + "_" + suffixe + ".pdf";
                    }

                    zip.putNextEntry(new ZipEntry(cheminZip));
                    zip.write(pdfBytes);
                    zip.closeEntry();

                    nombrePdf++;
                    log.info("PDF ajouté au ZIP : {}", cheminZip);
                }
            } finally {
                // Erreur ou interruption : on arrête les matières encore en cours ou en attente
                for (Future<byte[]> tache : taches) {
                    if (tache != null) {
                        tache.cancel(true);
                    }
                }
            }

            if (nombrePdf == 0) {
                log.warn("Aucune étiquette n'a été générée pour toutes les matières");
                response.setStatus(HttpServletResponse.SC_NO_CONTENT);
                return;
            }

            long tailleZip = Files.size(fichierZip);

            log.info("ZIP des étiquettes ({}) : {} PDF, {} Ko, généré en {} ms ({} threads)",
                    groupe, nombrePdf, tailleZip / 1024,
                    (System.nanoTime() - debut) / 1_000_000, NB_THREADS_PDF);

            response.setContentType("application/zip");
            response.setHeader("Content-Disposition",
                    "attachment; filename=etiquettes_toutes_matieres_"
                            + groupe.toUpperCase() + "_groupe.zip");
            response.setContentLengthLong(tailleZip);

            try (InputStream source = Files.newInputStream(fichierZip)) {
                source.transferTo(response.getOutputStream());
                response.flushBuffer();
            } catch (IOException e) {
                // Le client a coupé la connexion (onglet fermé, rechargement...). Inutile de
                // remonter l'exception : le RestExceptionHandler tenterait d'écrire du JSON dans
                // une réponse déjà typée application/zip et ajouterait une 2e erreur au log.
                log.warn("Téléchargement du ZIP interrompu par le client : {}", e.getMessage());
            }
        } finally {
            Files.deleteIfExists(fichierZip);
        }
    }

    /**
     * Intitulé de la matière : la valeur de la règle, à défaut son code.
     */
    private static String libelleMatiere(RegleMatiere regle) {
        return Optional
                .ofNullable(regle.getValeur())
                .filter(v -> !v.isBlank())
                .orElse(regle.getCode().trim());
    }

    /**
     * Le champ groupe de RegleMatiere vaut 1ERGRP, 2NDGRP ou 1ER2NDGRP.
     * Inutile de générer une matière du 2nd groupe quand on exporte le 1er (et
     * inversement). Une règle sans groupe est traitée comme du 1er groupe,
     * cohérent avec le calcul des effectifs dans TirageJuryMatService.
     */
    private static boolean matiereConcerneeParGroupe(RegleMatiere regle, String groupe) {
        String groupeRegle = Optional.ofNullable(regle.getGroupe())
                .map(String::trim)
                .map(String::toUpperCase)
                .orElse("");

        if ("1ER2NDGRP".equals(groupeRegle)) {
            return true;
        }

        if ("1ER".equalsIgnoreCase(groupe)) {
            return groupeRegle.isEmpty() || "1ERGRP".equals(groupeRegle);
        }

        return "2NDGRP".equals(groupeRegle);
    }

    private String sanitizeFileName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return "sans_nom";
        }
        return name.trim()
                .replaceAll("[\\\\/:*?\"<>|]", "_")
                .replaceAll("\\s+", " ");
    }

    @Operation(summary = "Génération de l'étiquette de table - Format A4 Paysage")
    @GetMapping("/generate-etiquetteCantine-paysage")
    public void generateEtiquettesCantine(HttpServletResponse response) throws IOException, DocumentException
    {

        List<FusionRepartitionTirage> sortedList = repository.findAll()
                .stream()
                .sorted(Comparator.comparing(FusionRepartitionTirage::getAcademia))
                .toList();

        if (sortedList.isEmpty())
        {
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            return;
        }


        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "inline; filename=etiquettes_bac.pdf");

        Document document = new Document(PageSize.A4.rotate(), 36f, 36f, 10f, 10f);
        PdfWriter.getInstance(document, response.getOutputStream());
        document.open();

        // ================= POLICES =================
        Font helv10 = FontFactory.getFont(FontFactory.HELVETICA, 10, Font.NORMAL);
        Font helv12Bold = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 60);
        Font helv14 = FontFactory.getFont(FontFactory.HELVETICA, 17, Font.BOLD);
        Font helv22 = FontFactory.getFont(FontFactory.HELVETICA, 22, Font.NORMAL);
        Font helv24Bold = FontFactory.getFont(FontFactory.HELVETICA, 24, Font.BOLD);
        Font helv16Bold = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
        Font helv16 = FontFactory.getFont(FontFactory.HELVETICA, 16);
        Font helv26Bold = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 30);

        Image logo = Image.getInstance(logoBytes); // logo déjà réduit et en cache
        logo.scaleToFit(70f, 70f);

        // ================= PARCOURS OPTIMISÉ =================
        for (FusionRepartitionTirage data : sortedList) {

            if (data.getMatieres() == null) {
                continue;
            }

            // ================= EFFECTIF =================
            Double effectif = 0.0;
            Double effT2ndG = 0.0;
            String grp = null;
            String date = "";
            String horaire = "";

            String qrContent = buildQRCodeContent__(data);
            Image qrCode = generateQRCode(qrContent, 120, 120);

            String series = (data.getSeries() != null && !data.getSeries().isEmpty())
                    ? String.join(" - ", data.getSeries())
                    : "";

            // ================= GENERATION =================
            generateEtiquetteCantinePage(
                    effT2ndG,
                    document,
                    logo,
                    data,
                    series,
                    effectif,
                    "CENTRE",
                    date,
                    horaire,
                    helv10, helv12Bold, helv14, helv22,
                    helv24Bold, helv16Bold, helv26Bold, helv16, qrCode
            );

            document.newPage();
        }

        document.close();
    }


    @Operation(summary = "Génération de l'étiquette de table - Format A4 Paysage")
    @GetMapping("/generate-etiquetteCGS-paysage")
    public void generateEtiquettes(HttpServletResponse response) throws IOException, DocumentException {
        List<RepartitionTirageCGS> list = repositorycgs.findAllByOrderByDisciplineAsc();
        //log.info(list.toString());
        if (list.isEmpty())
        {
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            return;
        }

        // 🔹 Charger les règles
        List<RegleMatiereCGS> regles = repocgs.findAll();
        Map<String, RegleMatiereCGS> regleParCode = regles.stream()
                .collect(Collectors.toMap(
                        r -> r.getValeur().toUpperCase(),
                        r -> r,
                        (r1, r2) -> r1
                ));

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "inline; filename=etiquettes_bac.pdf");

        // 🔹 Polices
        Font helv10 = FontFactory.getFont(FontFactory.HELVETICA, 10, Font.NORMAL);
        Font helv12Bold = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 60);
        Font helv14 = FontFactory.getFont(FontFactory.HELVETICA, 17, Font.BOLD);
        Font helv22 = FontFactory.getFont(FontFactory.HELVETICA, 22, Font.NORMAL);
        Font helv24Bold = FontFactory.getFont(FontFactory.HELVETICA, 24, Font.BOLD);
        Font helv16Bold = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
        Font helv16 = FontFactory.getFont(FontFactory.HELVETICA, 16);
        Font helv26Bold = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 26);

        Document document = new Document(PageSize.A4.rotate(), 36f, 36f, 36f, 36f);
        PdfWriter.getInstance(document, response.getOutputStream());
        document.open();

        // 🔹 Logo (déjà réduit et en cache)
        Image logo = Image.getInstance(logoBytes);
        logo.scaleToFit(70f, 70f);

        // 🔹 Parcours optimisé : pour chaque centre et discipline
        for (RepartitionTirageCGS data : list)
        {
            if (data.getDiscipline() == null) continue;

            String matiere = data.getDiscipline().toUpperCase();
            RegleMatiereCGS regle = regleParCode.get(matiere);
            if (regle == null)
            {
                log.warn("Aucune règle trouvée pour {}", matiere);
                continue;
            }

            String series = (data.getSeries() != null && !data.getSeries().isEmpty())
                    ? String.join(", ", data.getSeries())
                    : "";

            // 🔹 Génération pour PREMIERE

            if (data.getEff1ere() != null && data.getEff1ere() > 0)
            {
                generateEtiquetteCGSPage(
                        document,
                        logo,
                        data,
                        matiere,
                        series,
                        data.getEff1ere().longValue(),
                        "PREMIERE",
                        Optional.ofNullable(regle.getDate()).orElse(""),
                        Optional.ofNullable(regle.getHeure()).orElse(""),
                        helv10, helv12Bold, helv14, helv22,
                        helv24Bold, helv16Bold, helv26Bold, helv16
                );
                document.newPage();
            }

            // 🔹 Génération pour TERMINALE

            if (data.getEffTle() != null && data.getEffTle() > 0)
            {
                generateEtiquetteCGSPage(
                        document,
                        logo,
                        data,
                        matiere,
                        series,
                        data.getEffTle().longValue(),
                        "TERMINALE",
                        Optional.ofNullable(regle.getDate()).orElse(""),
                        Optional.ofNullable(regle.getHeure()).orElse(""),
                        helv10, helv12Bold, helv14, helv22,
                        helv24Bold, helv16Bold, helv26Bold, helv16
                );
                document.newPage();
            }

        }

        document.close();
    }


    private void generateEtiquettePage(int session_, String type_lv, double effT2ndG, Document document, Image logo, FusionRepartitionTirage data,
                                       String libelleMatiere, String serie, double effectif, String grp, String date, String horaire,
                                       Font f10, Font f12Bold, Font f14, Font f22, Font f22Bold, Font f16Bold, Font f26Bold, Font f16, Image qrCode) throws DocumentException {
        // --- 1. EN-TÊTE ---
        PdfPTable header = new PdfPTable(3);
        header.setWidthPercentage(100);
        header.setWidths(new float[]{0.55f, 4f, 1.5f});

        PdfPCell imageCell = new PdfPCell(logo);
        imageCell.setBorder(Rectangle.NO_BORDER);
        imageCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        imageCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        header.addCell(imageCell);

        // Cellule texte
        Paragraph headerText = new Paragraph(
                "REPUBLIQUE DU SENEGAL\nUn Peuple - Un But - Une Foi\n" +
                        "Ministère de l'Enseignement supérieur, de la Recherche et de l'Innovation" +
                        "\nOffice du Baccalauréat",
                f10
        );
        headerText.setLeading(14f, 0);
        headerText.setAlignment(Element.ALIGN_LEFT);

        PdfPCell textCell = new PdfPCell(headerText);
        textCell.setBorder(Rectangle.NO_BORDER);
        textCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        textCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        header.addCell(textCell);

        // ===== CELLULE DROITE : CODE ACADEMIE + QR CODE =====
        PdfPCell rightCell_ = new PdfPCell();
        rightCell_.setBorder(Rectangle.NO_BORDER);
        rightCell_.setVerticalAlignment(Element.ALIGN_MIDDLE);
        rightCell_.setHorizontalAlignment(Element.ALIGN_CENTER);

        // Tableau interne pour aligner code Académie et QR Code
        PdfPTable rightInnerTable = new PdfPTable(2);
        rightInnerTable.setWidthPercentage(100);
        rightInnerTable.setWidths(new float[]{1.45f, 1f});

        // Cellule code Académie
        PdfPCell codeAcademieCell = new PdfPCell(new Phrase(data.getAcademia(), f12Bold));
        codeAcademieCell.setBorder(Rectangle.NO_BORDER);
        codeAcademieCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        codeAcademieCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        rightInnerTable.addCell(codeAcademieCell);

        // Cellule QR Code (si disponible)
        PdfPCell qrCell = new PdfPCell();
        qrCell.setBorder(Rectangle.NO_BORDER);
        qrCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        qrCell.setVerticalAlignment(Element.ALIGN_MIDDLE);

        if (qrCode != null)
        {
            // Redimensionner le QR Code (plus petit pour l'en-tête)
            Image smallQr = Image.getInstance(qrCode);
            smallQr.scaleAbsolute(100, 100);
            smallQr.setAlignment(Element.ALIGN_CENTER);
            qrCell.addElement(smallQr);
        }
        else
        {
            qrCell.addElement(new Phrase("", f10));
        }
        rightInnerTable.addCell(qrCell);

        rightCell_.addElement(rightInnerTable);
        header.addCell(rightCell_);

        document.add(header);

        // --- 2. TITRES CENTRAUX ---
        Paragraph office = new Paragraph("OFFICE DU BACCALAUREAT", f16Bold);
        office.setAlignment(Element.ALIGN_CENTER);
        office.setSpacingBefore(1f);
        document.add(office);

        Paragraph session = null;
        Paragraph groupe = null;

        if (session_ == 1)
        {
            session = new Paragraph("BACCALAUREAT SESSION NORMALE " + data.getSession(), f22Bold);
        }

        if (session_ == 2)
        {
            session = new Paragraph("BACCALAUREAT SESSION DE REMPLACEMENT " + data.getSession(), f22Bold);
        }

        if ("PREMIER GROUPE".equals(grp))
        {
            groupe = new Paragraph("[EPREUVE DU PREMIER GROUPE]", f22Bold);
        }

        if ("SECOND GROUPE".equals(grp))
        {
            groupe = new Paragraph("[EPREUVE DU SECOND GROUPE]", f22Bold);
        }

        assert session != null;
        session.setAlignment(Element.ALIGN_CENTER);
        session.setSpacingAfter(1f);
        document.add(session);

        assert groupe != null;
        groupe.setAlignment(Element.ALIGN_CENTER);
        groupe.setSpacingAfter(10f);
        document.add(groupe);

        // --- 3. TABLEAU DES INFORMATIONS ---
        PdfPTable info = new PdfPTable(2);
        info.setWidthPercentage(100);
        info.setWidths(new float[]{1.2f, 4f});

        addInfoRow(info, "ACADEMIE :", getAcademieFullName(data.getAcademia()), f14, f22);
        addInfoRow(info, "CENTRE :", data.getCentreEcrit(), f14, f22);
        addInfoRow(info, "JURY :", Boolean.TRUE.equals(data.getCs()) ? "CS" : String.valueOf(data.getJury()), f14, f22);
        addInfoRow(info, "SERIE (S) :", serie, f14, f22); // à affiner si plusieurs séries possibles
        // System.out.println("OK" + grp);
        String ntValue;
        if ("PREMIER GROUPE".equals(grp))
        {
            long nt = Math.round(effectif * 1.05) + 1;
            ntValue = String.valueOf(nt);
        }
        else if ("SECOND GROUPE".equals(grp)) {
            long nt = Math.round(effT2ndG) + 1;
            ntValue = String.valueOf(nt);
        } else {
            ntValue = "";
        }

        addInfoRow(info, "CANDIDATS : ", Math.round(effectif) + "        NT : " + ntValue, f14, f22);

        document.add(info);
        document.add(new Paragraph("\n"));

        // --- 4. BAS DE PAGE : EPREUVE et CALENDRIER ---
        PdfPTable footer = new PdfPTable(2);
        footer.setWidthPercentage(100);
        footer.setWidths(new float[]{2.55f, 1.5f});
        footer.setSpacingBefore(5f);

        // Cellule gauche
        PdfPCell leftCell = new PdfPCell();
        leftCell.setBorder(Rectangle.BOX);
        leftCell.setPadding(15f);

        Font normal = new Font(Font.HELVETICA, 16, Font.NORMAL);
        Font bold = new Font(Font.HELVETICA, 16, Font.BOLD);

        Paragraph epreuveTitle = new Paragraph();
        epreuveTitle.add(new Chunk("EPREUVE DE", normal));
        epreuveTitle.setAlignment(Element.ALIGN_CENTER);
        leftCell.addElement(epreuveTitle);

        // Modifiez cette partie
        String suffixe = "";
        if (
                type_lv.equalsIgnoreCase("matiere1")
                        && !libelleMatiere.equalsIgnoreCase("Génie mécanique")
                        && !libelleMatiere.equalsIgnoreCase("Génie Electrique")
        ) {
            suffixe = "(LV1)";
        }
        else if (
                type_lv.equalsIgnoreCase("matiere2")
                        && !libelleMatiere.equalsIgnoreCase("ECONOMIE")
        ) {
            suffixe = "(LV2)";
        }

        Paragraph epreuveLibelle = new Paragraph(libelleMatiere.toUpperCase() + "\n" + suffixe, f26Bold);
        epreuveLibelle.setAlignment(Element.ALIGN_CENTER);
        epreuveLibelle.setSpacingBefore(2f);
        epreuveLibelle.setLeading(0f, 1.20f);  // Réduit l'espacement entre les lignes
        leftCell.addElement(epreuveLibelle);

        footer.addCell(leftCell);

        // Cellule droite
        PdfPCell rightCell = new PdfPCell();
        rightCell.setBorder(Rectangle.BOX);
        rightCell.setPadding(15f);

        PdfPTable calTable = new PdfPTable(2);
        calTable.setWidthPercentage(100);
        calTable.setWidths(new float[]{1.25f, 2f});

        PdfPCell calTitle = new PdfPCell(new Phrase("CALENDRIER", f16));
        calTitle.setColspan(2);
        calTitle.setBorder(Rectangle.BOX);
        calTitle.setHorizontalAlignment(Element.ALIGN_CENTER);
        calTitle.setPadding(5f);
        calTable.addCell(calTitle);

        PdfPCell dateLabel = new PdfPCell(new Phrase("DATE :", f16));
        dateLabel.setBorder(Rectangle.BOX);
        dateLabel.setPadding(5f);
        calTable.addCell(dateLabel);

        PdfPCell dateValue = new PdfPCell(new Phrase(date != null ? date : "", f14));
        dateValue.setBorder(Rectangle.BOX);
        dateValue.setPadding(5f);
        calTable.addCell(dateValue);

        PdfPCell horaireLabel = new PdfPCell(new Phrase("HORAIRE :", f16));
        horaireLabel.setBorder(Rectangle.BOX);
        horaireLabel.setPadding(5f);
        calTable.addCell(horaireLabel);

        PdfPCell horaireValue = new PdfPCell(new Phrase(horaire != null ? horaire : "", f14));
        horaireValue.setBorder(Rectangle.BOX);
        horaireValue.setPadding(5f);
        calTable.addCell(horaireValue);

        rightCell.addElement(calTable);
        footer.addCell(rightCell);

        document.add(footer);
    }


    private void generateEtiquetteCantinePage(double effT2ndG, Document document, Image logo, FusionRepartitionTirage data, String serie, double effectif, String grp, String date, String horaire,
                                       Font f10, Font f12Bold, Font f14, Font f22, Font f22Bold, Font f16Bold, Font f26Bold, Font f16, Image qrCode) throws DocumentException {
        // --- 1. EN-TÊTE ---
        PdfPTable header = new PdfPTable(3);
        header.setWidthPercentage(100);
        header.setWidths(new float[]{0.55f, 4f, 1.5f});

        PdfPCell imageCell = new PdfPCell(logo);
        imageCell.setBorder(Rectangle.NO_BORDER);
        imageCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        imageCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        header.addCell(imageCell);

        // Cellule texte
        Paragraph headerText = new Paragraph(
                "REPUBLIQUE DU SENEGAL\nUn Peuple - Un But - Une Foi\n" +
                        "Ministère de l'Enseignement supérieur, de la Recherche et de l'Innovation" +
                        "\nOffice du Baccalauréat",
                f10
        );
        headerText.setLeading(14f, 0);
        headerText.setAlignment(Element.ALIGN_LEFT);

        PdfPCell textCell = new PdfPCell(headerText);
        textCell.setBorder(Rectangle.NO_BORDER);
        textCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        textCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        header.addCell(textCell);

        // ===== CELLULE DROITE : CODE ACADEMIE + QR CODE =====
        PdfPCell rightCell_ = new PdfPCell();
        rightCell_.setBorder(Rectangle.NO_BORDER);
        rightCell_.setVerticalAlignment(Element.ALIGN_MIDDLE);
        rightCell_.setHorizontalAlignment(Element.ALIGN_CENTER);

        // Tableau interne pour aligner code Académie et QR Code
        PdfPTable rightInnerTable = new PdfPTable(2);
        rightInnerTable.setWidthPercentage(100);
        rightInnerTable.setWidths(new float[]{1.45f, 1f});

        // Cellule code Académie
        PdfPCell codeAcademieCell = new PdfPCell(new Phrase(data.getAcademia(), f12Bold));
        codeAcademieCell.setBorder(Rectangle.NO_BORDER);
        codeAcademieCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        codeAcademieCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        rightInnerTable.addCell(codeAcademieCell);

        // Cellule QR Code (si disponible)
        PdfPCell qrCell = new PdfPCell();
        qrCell.setBorder(Rectangle.NO_BORDER);
        qrCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        qrCell.setVerticalAlignment(Element.ALIGN_MIDDLE);

        if (qrCode != null)
        {
            // Redimensionner le QR Code (plus petit pour l'en-tête)
            Image smallQr = Image.getInstance(qrCode);
            smallQr.scaleAbsolute(100, 100);
            smallQr.setAlignment(Element.ALIGN_CENTER);
            qrCell.addElement(smallQr);
        }
        else
        {
            qrCell.addElement(new Phrase("", f10));
        }
        rightInnerTable.addCell(qrCell);

        rightCell_.addElement(rightInnerTable);
        header.addCell(rightCell_);

        document.add(header);

        // --- 2. TITRES CENTRAUX ---
        Paragraph office = new Paragraph("OFFICE DU BACCALAUREAT", f16Bold);
        office.setAlignment(Element.ALIGN_CENTER);
        office.setSpacingBefore(2.5f);
        document.add(office);

        Paragraph session = new Paragraph("BACCALAUREAT GENERAL SESSION NORMALE " + data.getSession(), f22Bold);
        session.setAlignment(Element.ALIGN_CENTER);
        session.setSpacingAfter(20f);
        document.add(session);

        // --- 3. TABLEAU DES INFORMATIONS ---
        PdfPTable info = new PdfPTable(2);
        info.setWidthPercentage(100);
        info.setWidths(new float[]{1.2f, 4f});
        String ntValue = "DK 20";
        addInfoRow(info, "ACADEMIE :", getAcademieFullName(data.getAcademia()), f14, f22);
        addInfoRow(info, "CENTRE :", data.getCentreEcrit(), f14, f22);
        addInfoRow(info, "JURY :", Boolean.TRUE.equals(data.getCs()) ? "CS - [CLE CC : " + data.getCC() + "]" + " / [CLE PJ : " + data.getPJ() + "]" : data.getJury() + " - [CLE CC : " + data.getCC() + "]" + " / [CLE PJ : " + data.getPJ() + "]", f14, f22);
        addInfoRow(info, "SERIE (S) :", serie, f14, f22);
        addInfoRow(info, "ETABLISSEMENT : ", data.getCentreEcrit(), f14, f22);

        document.add(info);
        document.add(new Paragraph("\n"));

        // --- 4. BAS DE PAGE : EPREUVE et CALENDRIER ---
        PdfPTable footer = new PdfPTable(1);
        footer.setWidthPercentage(100);
        //footer.setWidths(new float[]{2.55f, 1.5f});
        footer.setSpacingBefore(5f);

        // Cellule gauche
        PdfPCell leftCell = new PdfPCell();
        leftCell.setBorder(Rectangle.BOX);
        leftCell.setPadding(15f);

        Font normal = new Font(Font.HELVETICA, 16, Font.NORMAL);
        Font bold = new Font(Font.HELVETICA, 16, Font.BOLD);

        Paragraph epreuveTitle = new Paragraph();
        epreuveTitle.add(new Chunk(grp, bold));
        epreuveTitle.setAlignment(Element.ALIGN_CENTER);
        leftCell.addElement(epreuveTitle);

        Paragraph epreuveLibelle = new Paragraph(data.getCentreEcrit().toUpperCase(), f26Bold);
        epreuveLibelle.setAlignment(Element.ALIGN_CENTER);
        epreuveLibelle.setSpacingBefore(2f);
        epreuveLibelle.setLeading(0f, 1.20f);  // Réduit l'espacement entre les lignes
        leftCell.addElement(epreuveLibelle);

        footer.addCell(leftCell);

        document.add(footer);
    }


    private void generateEtiquetteCGSPage(Document document, Image logo, RepartitionTirageCGS data,
                                       String libelleMatiere, String serie, long effectif, String grp, String date, String horaire,
                                       Font f10, Font f12Bold, Font f14, Font f22, Font f22Bold, Font f16Bold, Font f26Bold, Font f16) throws DocumentException {
        // --- 1. EN-TÊTE ---
        PdfPTable header = new PdfPTable(3);
        header.setWidthPercentage(100);
        header.setWidths(new float[]{0.55f, 4f, 1f});

        PdfPCell imageCell = new PdfPCell(logo);
        imageCell.setBorder(Rectangle.NO_BORDER);
        imageCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        imageCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        header.addCell(imageCell);

        // Cellule texte
        Paragraph headerText = new Paragraph(
                "REPUBLIQUE DU SENEGAL\nUn Peuple - Un But - Une Foi\n" +
                        "Ministère de l'Enseignement supérieur, de la Recherche et de l'Innovation" +
                        "\nOffice du Baccalauréat",
                f10
        );
        headerText.setLeading(14f, 0);
        headerText.setAlignment(Element.ALIGN_LEFT);

        PdfPCell textCell = new PdfPCell(headerText);
        textCell.setBorder(Rectangle.NO_BORDER);
        textCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        textCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        header.addCell(textCell);


        PdfPCell codeAcademie = new PdfPCell(new Phrase("", f12Bold));
        codeAcademie.setBorder(Rectangle.NO_BORDER);
        codeAcademie.setHorizontalAlignment(Element.ALIGN_CENTER);
        codeAcademie.setVerticalAlignment(Element.ALIGN_MIDDLE);
        header.addCell(codeAcademie);

        document.add(header);

        // --- 2. TITRES CENTRAUX ---
        Paragraph office = new Paragraph("OFFICE DU BACCALAUREAT", f16Bold);
        office.setAlignment(Element.ALIGN_CENTER);
        office.setSpacingBefore(10f);
        document.add(office);

        Paragraph session = new Paragraph("CONCOURS GENERAL SENEGALAIS\nSESSION " + data.getSession(), f22Bold);
        session.setAlignment(Element.ALIGN_CENTER);
        session.setSpacingAfter(20f);
        document.add(session);

        // --- 3. TABLEAU DES INFORMATIONS ---
        PdfPTable info = new PdfPTable(2);
        info.setWidthPercentage(100);
        info.setWidths(new float[]{1.2f, 4f});

        addInfoRow(info, "ACADEMIE :", data.getAcademia(), f14, f22);
        addInfoRow(info, "CENTRE :", data.getCentreEcrit(), f14, f22);
        addInfoRow(info, "CLASSE :", grp, f14, f22);
        // addInfoRow(info, "SERIE (S) :", serie, f14, f22); // à affiner si plusieurs séries possibles
        addInfoRow(info, "SERIE (S) :", "TOUTES SERIES", f14, f22); // à affiner si plusieurs séries possibles
        addInfoRow(info, "CANDIDATS :", effectif + "        NT : " + (Math.round(effectif * 1.055) + 1), f14, f22); // NT = effectif par défaut

        document.add(info);
        document.add(new Paragraph("\n"));

        // --- 4. BAS DE PAGE : EPREUVE et CALENDRIER ---
        PdfPTable footer = new PdfPTable(2);
        footer.setWidthPercentage(100);
        footer.setWidths(new float[]{2.55f, 1.5f});
        footer.setSpacingBefore(10f);

        // Cellule gauche
        PdfPCell leftCell = new PdfPCell();
        leftCell.setBorder(Rectangle.BOX);
        leftCell.setPadding(15f);

        Paragraph epreuveTitle = new Paragraph("EPREUVE", f16);
        epreuveTitle.setAlignment(Element.ALIGN_CENTER);
        leftCell.addElement(epreuveTitle);

        Paragraph epreuveLibelle = new Paragraph(libelleMatiere.toUpperCase(), f26Bold);
        epreuveLibelle.setAlignment(Element.ALIGN_CENTER);
        epreuveLibelle.setSpacingBefore(5f);
        leftCell.addElement(epreuveLibelle);

        footer.addCell(leftCell);

        // Cellule droite
        PdfPCell rightCell = new PdfPCell();
        rightCell.setBorder(Rectangle.BOX);
        rightCell.setPadding(15f);

        PdfPTable calTable = new PdfPTable(2);
        calTable.setWidthPercentage(100);
        calTable.setWidths(new float[]{1.25f, 2f});

        PdfPCell calTitle = new PdfPCell(new Phrase("CALENDRIER", f16));
        calTitle.setColspan(2);
        calTitle.setBorder(Rectangle.BOX);
        calTitle.setHorizontalAlignment(Element.ALIGN_CENTER);
        calTitle.setPadding(8f);
        calTable.addCell(calTitle);

        PdfPCell dateLabel = new PdfPCell(new Phrase("DATE :", f16));
        dateLabel.setBorder(Rectangle.BOX);
        dateLabel.setPadding(8f);
        calTable.addCell(dateLabel);

        PdfPCell dateValue = new PdfPCell(new Phrase(date != null ? date : "", f14));
        dateValue.setBorder(Rectangle.BOX);
        dateValue.setPadding(8f);
        calTable.addCell(dateValue);

        PdfPCell horaireLabel = new PdfPCell(new Phrase("HORAIRE :", f16));
        horaireLabel.setBorder(Rectangle.BOX);
        horaireLabel.setPadding(8f);
        calTable.addCell(horaireLabel);

        PdfPCell horaireValue = new PdfPCell(new Phrase(horaire != null ? horaire : "", f14));
        horaireValue.setBorder(Rectangle.BOX);
        horaireValue.setPadding(8f);
        calTable.addCell(horaireValue);

        rightCell.addElement(calTable);
        footer.addCell(rightCell);

        document.add(footer);
    }

    public String getAcademieFullName(String code) {
        if (code == null) return "";
        switch (code.toUpperCase()) {
            case "PK": return "PIKINE - GUEDIAWAYE";
            case "DK": return "DAKAR";
            case "RF": return "RUFISQUE";
            case "DL": return "DIOURBEL";
            case "FK": return "FATICK";
            case "KF": return "KAFFRINE";
            case "KL": return "KAOLACK";
            case "KG": return "KEDOUGOU";
            case "KD": return "KOLDA";
            case "LG": return "LOUGA";
            case "MT": return "MATAM";
            case "SL": return "SAINT LOUIS";
            case "SD": return "SEDHIOU";
            case "TA": return "TAMBACOUNDA";
            case "TB": return "TAMBACOUNDA"; // TA et TB tous deux TAMBACOUNDA
            case "TH": return "THIES";
            case "ZG": return "ZIGUINCHOR";
            default: return code; // si inconnu, on retourne le code
        }
    }

    private void addInfoRow(PdfPTable table, String label, String value, Font labelFont, Font valueFont) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, labelFont));
        labelCell.setBorder(Rectangle.NO_BORDER);
        labelCell.setPaddingBottom(8f);
        labelCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.addCell(labelCell);

        PdfPCell valueCell = new PdfPCell(new Phrase(value, valueFont));
        valueCell.setBorder(Rectangle.NO_BORDER);
        valueCell.setPaddingBottom(8f);
        labelCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.addCell(valueCell);
    }

    // Classes internes pour définir les matières
    @FunctionalInterface
    private interface EffectifExtractor {
        long extract(FusionRepartitionTirage data);
    }

    @FunctionalInterface
    private interface DateExtractor {
        String extract(FusionRepartitionTirage data);
    }

    @FunctionalInterface
    private interface HoraireExtractor {
        String extract(FusionRepartitionTirage data);
    }

    private static class MatiereDefinition {
        String libelle;
        EffectifExtractor effectifExtractor;
        DateExtractor dateExtractor;
        HoraireExtractor horaireExtractor;

        String listeSerie;

        MatiereDefinition(String libelle, EffectifExtractor effExt, DateExtractor dateExt, HoraireExtractor horExt, String listeSerie) {
            this.libelle = libelle;
            this.effectifExtractor = effExt;
            this.dateExtractor = dateExt;
            this.horaireExtractor = horExt;
            this.listeSerie = listeSerie;

        }

        // Ajoutez cette méthode dans votre contrôleur

    }


    @Operation(summary = "Génération du document BDR LS")
    @PostMapping("/generate-bdr")
    public void generateBDRDocument(HttpServletResponse response, @RequestBody List<Integer> jurysExclus) throws IOException, DocumentException
    {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "inline; filename=BDR_LS_2025.pdf");

        Document document = new Document(PageSize.A4, 20f, 20f, 20f, 45f);
        PdfWriter writer = PdfWriter.getInstance(document, response.getOutputStream());
        FooterEvent event = new FooterEvent(initializeFonts().verySmallFont);
        writer.setPageEvent(event);
        document.open();

        try
        {
            // Ajout immédiat d'un élément pour éviter le document vide
            //document.add(new Paragraph("Génération du document en cours...", new Font(Font.HELVETICA, 12)));

            // Initialisation des polices
            FontConfiguration fonts = initializeFonts();

            // Chargement et configuration du logo
            Image logo = loadAndScaleLogo();

            List<FusionRepartitionTirage> allFRT = null;

            // log.info(jurysExclus.toString());

            if (!jurysExclus.isEmpty())
            {
                log.info("ici");
                allFRT = ftr.findAllByJuryNotIn(jurysExclus).stream()
                        .sorted(Comparator.comparing(FusionRepartitionTirage::getAcademia))
                        .toList();
            }
            else
            {
                log.info("héééé");
                allFRT = ftr.findAll().stream()
                        .sorted(Comparator.comparing(FusionRepartitionTirage::getAcademia))
                        .toList();
            }

            if (allFRT.isEmpty())
            {
                document.add(new Paragraph("Aucune répartition trouvée pour cette session.", fonts.normalFont));
            }
            else
            {
                for (int i = 0; i < allFRT.size(); i++)
                {
                    FusionRepartitionTirage repartition = allFRT.get(i);

                    RepartitionCompleteDTO data = tirageJuryMatService.construire(repartition);
                    if (data == null)
                    {
                        System.out.println("Données nulles pour cette répartition");
                        document.add(new Paragraph("Données incomplètes pour ce centre.", fonts.normalFont));
                        continue;
                    }

                    buildDocument(document, fonts, logo, data);

                    if (i < allFRT.size() - 1)
                    {
                        document.newPage();
                    }
                }
            }

        } catch (Exception e) {
            System.err.println("Erreur dans la génération du PDF : " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de la génération du PDF", e);
        } finally {
            if (document != null && document.isOpen()) {
                document.close();
            }
        }
    }

    @Operation(summary = "Génération du document BDR LS")
    @GetMapping("/generate-bdr-cgs")
    public void generateBDRDocument_(HttpServletResponse response) throws IOException, DocumentException {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "inline; filename=BDR_LS_2025.pdf");

        Document document = new Document(PageSize.A4, 50f, 50f, 50f, 50f);
        PdfWriter writer = PdfWriter.getInstance(document, response.getOutputStream());
        FooterEvent event = new FooterEvent(initializeFonts().verySmallFont);
        writer.setPageEvent(event);
        document.open();

        try {

            FontConfiguration fonts = initializeFonts();
            Image logo = loadAndScaleLogo();
            List<RepartitionCompleteCGSDTO> allRep = tirageJuryMatService.construire_();
            //System.out.println("Nombre de répartitions trouvées : " + allFRT.size());

            if (allRep.isEmpty()) {
                document.add(new Paragraph("Aucune répartition trouvée pour cette session.", fonts.normalFont));
            }
            else
            {
                int index = 0;
                for (RepartitionCompleteCGSDTO data : allRep)
                {
                    if (data == null) {
                        System.out.println("Données nulles pour cette répartition");
                        document.add(new Paragraph("Données incomplètes pour ce centre.", fonts.normalFont));
                        continue;
                    }

                    // Afficher les données reçues
                    // System.out.println("Données construites : session=" + data.getSession() + ", centre=" + data.getCentre() + ", nbMatieres=" + (data.getMatieres() != null ? data.getMatieres().size() : 0));

                    buildDocumentCGS(document, fonts, logo, data);

                    if (index < allRep.size() - 1) {
                        document.newPage();
                    }
                    index++;
                }
            }

        } catch (Exception e) {
            System.err.println("Erreur dans la génération du PDF : " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de la génération du PDF", e);
        } finally {
            if (document != null && document.isOpen()) {
                document.close();
            }
        }
    }

    @Operation(summary = "Génération du document BDR LS")
    @GetMapping("/generate-bdr-feuilles")
    public void generateBDRFDocument(HttpServletResponse response) throws IOException, DocumentException {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "inline; filename=BDR_LS_2025.pdf");

        Document document = new Document(PageSize.A4, 50f, 50f, 35f, 50f);
        PdfWriter writer = PdfWriter.getInstance(document, response.getOutputStream());
        FooterEvent2 event = new FooterEvent2(initializeFonts().smallFont);
        writer.setPageEvent(event);
        document.open();

        try {
            // Initialisation des polices
            FontConfiguration fonts = initializeFonts();

            // Chargement et configuration du logo
            Image logo = loadAndScaleLogo();

            // Récupération des données
            List<FusionRepartitionFeuille> allFRT =
                    ffeuil.findAll(
                            Sort.by(Sort.Direction.DESC, "cp")
                                    .and(Sort.by(Sort.Direction.DESC, "cs"))
                                    .and(Sort.by("academia"))
                    );

            if (allFRT.isEmpty())
            {
                document.add(new Paragraph("Aucune répartition trouvée pour cette session.", fonts.normalFont));
            }
            else
            {
                int cpCounter = 0;
                int csCounter = 0;
                int blCounter = 0;

                for (int i = 0; i < allFRT.size(); i++)
                {
                    FusionRepartitionFeuille repartition = allFRT.get(i);
                    RepartitionCompleteFDTO data = decompteFeuilleJuryService.construire(repartition);

                    if (data == null)
                    {
                        System.out.println("Données nulles pour cette répartition");
                        document.add(new Paragraph("Données incomplètes pour ce centre.", fonts.normalFont));
                        continue;
                    }

                    // Choisir le compteur en fonction du type
                    int compteur;
                    if (data.getCp() != null && data.getCp())
                    {
                        cpCounter++;
                        compteur = cpCounter;
                    }
                    else if (data.getCs() != null && data.getCs())
                    {
                        csCounter++;
                        compteur = csCounter;
                    }
                    else
                    {
                        blCounter++;
                        compteur = blCounter;
                    }
                    // Construction du document avec le bon compteur
                    buildBDRFDocument(document, fonts, logo, data, compteur);
                    // Nouvelle page sauf pour le dernier
                    if (i < allFRT.size() - 1)
                    {
                        document.newPage();
                    }
                }
            }

        } catch (Exception e) {
            System.err.println("Erreur dans la génération du PDF : " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de la génération du PDF", e);
        } finally {
            if (document != null && document.isOpen()) {
                document.close();
            }
        }
    }

    private void buildBDRFDocument(Document document, FontConfiguration fonts, Image logo, RepartitionCompleteFDTO data, int a) throws DocumentException, UnsupportedEncodingException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String dateHeure = LocalDate.now().format(formatter);

        // Récupérer les booléens cp et cs depuis data
        boolean cp = data.getCp() != null && data.getCp();
        boolean cs = data.getCs() != null && data.getCs();

        // Header avec logo et les booléens cp/cs
        document.add(createHeader(logo, fonts, a, cp, cs));
        document.add(new Paragraph(" "));

        // Section Directeur et date
        document.add(createDirectorSection(fonts, dateHeure));
        document.add(new Paragraph(" "));

        // Titre principal
        document.add(createTitleSection(fonts, data));
        document.add(new Paragraph(" "));

        // Informations du centre
        document.add(createCentreInfoSection(fonts, data));

        // Tableau des matériels
        document.add(createMaterialTable(fonts, data));

        // Section signatures
        document.add(createSignaturesSection(fonts));
    }

    private PdfPTable createHeader(Image logo, FontConfiguration fonts, int a, boolean cp, boolean cs) throws DocumentException {
        PdfPTable header = new PdfPTable(3);
        header.setWidthPercentage(100);
        header.setWidths(new float[]{0.90f, 2f, 2.65f});

        // Cellule logo
        PdfPCell imageCell = new PdfPCell(logo);
        imageCell.setBorder(Rectangle.NO_BORDER);
        imageCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        imageCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        header.addCell(imageCell);

        // Cellule texte
        Paragraph headerText = new Paragraph(
                "REPUBLIQUE DU SENEGAL\nUn Peuple - Un But - Une Foi\n" +
                        "Ministère de l'Enseignement supérieur, \nde la Recherche et de l'Innovation" +
                        "\nOffice du Baccalauréat",
                fonts.normalFont
        );
        headerText.setLeading(14f, 0);
        headerText.setAlignment(Element.ALIGN_LEFT);

        PdfPCell textCell = new PdfPCell(headerText);
        textCell.setBorder(Rectangle.NO_BORDER);
        textCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        textCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        header.addCell(textCell);

        // Cellule référence - Adaptation selon cp/cs
        DecimalFormat df = new DecimalFormat("0000");
        String numeroFormate = df.format(a);

        // Déterminer le type de bon
        String typeBon = "";
        if (cp)
        {
            typeBon = "BL-CP";
        } 
        else if (cs) 
        {
            typeBon = "BL-CS";
        }

        String texte = "N°_" + typeBon + " " + numeroFormate + "/UCAD/OB/PFE/PF/aat";

        PdfPCell refCell = new PdfPCell(new Phrase(texte, fonts.boldFont));
        refCell.setBorder(Rectangle.NO_BORDER);
        refCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        refCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        header.addCell(refCell);

        return header;
    }

    private PdfPTable createDirectorSection(FontConfiguration fonts, String dateHeure) {
        PdfPTable headerTable = new PdfPTable(2);
        headerTable.setWidthPercentage(100);

        PdfPCell leftCell = new PdfPCell(new Phrase("LE DIRECTEUR", fonts.titleFont_));
        leftCell.setBorder(Rectangle.NO_BORDER);
        leftCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        leftCell.setVerticalAlignment(Element.ALIGN_MIDDLE);

        PdfPCell rightCell = new PdfPCell(new Phrase("Dakar, le " + dateHeure, fonts.normalFont_));
        rightCell.setBorder(Rectangle.NO_BORDER);
        rightCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        rightCell.setVerticalAlignment(Element.ALIGN_MIDDLE);

        headerTable.addCell(leftCell);
        headerTable.addCell(rightCell);

        return headerTable;
    }

    private PdfPTable createTitleSection(FontConfiguration fonts, RepartitionCompleteFDTO data)
    {
        PdfPTable titre = new PdfPTable(1);
        titre.setWidthPercentage(100);

        PdfPCell titre1 = new PdfPCell(new Phrase("BACCALAUREAT DE L'ENSEIGNEMENT SECONDAIRE - SESSION " + data.getSession(), fonts.titleFont_));
        PdfPCell titre2 = new PdfPCell(new Phrase("BORDEREAU DE LIVRAISON DU MATERIEL DE COMPOSITION", fonts.titleFont_));

        titre1.setBorder(PdfPCell.NO_BORDER);
        titre1.setHorizontalAlignment(Element.ALIGN_CENTER);
        titre1.setVerticalAlignment(Element.ALIGN_MIDDLE);

        titre2.setBorder(PdfPCell.NO_BORDER);
        titre2.setHorizontalAlignment(Element.ALIGN_CENTER);
        titre2.setVerticalAlignment(Element.ALIGN_MIDDLE);

        titre.addCell(titre1);
        titre.addCell(titre2);

        return titre;
    }

    private Image generateQRCode(String content, int width, int height) throws DocumentException
    {
        try {
            // Le QR ne depend que de (contenu, taille) : on ne l'encode qu'une fois par centre,
            // meme si ce centre apparait dans les 100+ matieres d'un export.
            String cle = content + "|" + width + "x" + height;
            QrBits qr = CACHE_QR.get(cle);

            if (qr == null) {
                // 1. Encoder le contenu en matrice de bits
                BitMatrix bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, width, height);
                qr = new QrBits(bitMatrix.getWidth(), bitMatrix.getHeight(), versBitsGris(bitMatrix));

                if (CACHE_QR.size() >= CACHE_QR_TAILLE_MAX) {
                    CACHE_QR.clear(); // garde-fou : le cache ne peut pas grossir sans limite
                }
                CACHE_QR.put(cle, qr);
            }

            // 2. Image iText brute (1 bit, niveaux de gris) : aucun PNG a encoder puis a
            //    redecoder via ImageIO a chaque page (c'etait le principal cout par page).
            //    Une Image neuve par appel (les Image ne se partagent pas entre threads) et une
            //    copie du tableau, pour que le cache ne soit jamais modifie.
            //    ImgRaw directement : Image.getInstance(w, h, 1, 1, data) convertirait en CCITT G4
            //    avec une convention noir/blanc differente de DeviceGray.
            return new ImgRaw(qr.largeur(), qr.hauteur(), 1, 1, qr.bits().clone());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String buildQRCodeContent(RepartitionCompleteFDTO data) throws UnsupportedEncodingException
    {
        String centreName = data.getCentre() != null ? data.getCentre() : "";
        String localite = data.getLocalite() != null ? data.getLocalite() : "";
        // Encoder le nom du centre pour l'URL
        String encodedCentre = URLEncoder.encode(centreName, "UTF-8");
        String encodedLocalite = URLEncoder.encode(localite, "UTF-8");

        // Option 1 : Recherche par nom (recommandé)
        String googleMapsUrl = "https://www.google.com/maps/search/?api=1&query=" + encodedCentre + "+" + encodedLocalite;

        return googleMapsUrl;
    }

    private String buildQRCodeContent_(RepartitionCompleteDTO data) throws UnsupportedEncodingException
    {
        String centreName = data.getCentre() != null ? data.getCentre() : "";
        String localite = data.getCentre() != null ? data.getCentre()  : "";
        // Encoder le nom du centre pour l'URL
        String encodedCentre = URLEncoder.encode(centreName, "UTF-8");
        String encodedLocalite = URLEncoder.encode(localite, "UTF-8");

        // Option 1 : Recherche par nom (recommandé)
        String googleMapsUrl = "https://www.google.com/maps/search/?api=1&query=" + encodedCentre + "+" + encodedLocalite;

        return googleMapsUrl;
    }

    private String buildQRCodeContent__(FusionRepartitionTirage data) throws UnsupportedEncodingException
    {
        String centreName = data.getCentreEcrit() != null ? data.getCentreEcrit() : "";
        // Encoder le nom du centre pour l'URL
        String encodedCentre = URLEncoder.encode(centreName, "UTF-8");
        // Option 1 : Recherche par nom (recommandé)
        String googleMapsUrl = "https://www.google.com/maps/search/?api=1&query=" + encodedCentre;

        return googleMapsUrl;
    }

    private PdfPTable createCentreInfoSection(FontConfiguration fonts, RepartitionCompleteFDTO data) throws DocumentException, UnsupportedEncodingException {
        PdfPTable infoTable = new PdfPTable(2);  // 2 colonnes (texte + QR)
        infoTable.setWidthPercentage(100);
        infoTable.setWidths(new float[]{2f, 1f});  // 2/3 texte, 1/3 QR

        boolean cp = data.getCp() != null && data.getCp();
        boolean cs = data.getCs() != null && data.getCs();

        // ===== PARTIE GAUCHE : Informations texte =====
        PdfPTable leftTable = new PdfPTable(1);
        leftTable.setWidthPercentage(100);

        // Ajouter les informations selon le type CP ou CS
        addInfoRow(leftTable, "ACADEMIE : ", data.getAcademie(), fonts);

        if (cp && !cs)
        {
            addInfoRow(leftTable, "LOCALITE : ", data.getLocalite(), fonts);
            addInfoRow(leftTable, "CENTRE : ", data.getCentre(), fonts);
            addInfoRow(leftTable, "NOMBRE DE JURY : ", String.valueOf(data.getNbJury()), fonts);
            addInfoRow(leftTable, "NOMBRE DE CANDIDATS : ", String.valueOf(data.getEffectif()), fonts);
        }
        else if (cs && !cp)
        {
            addInfoRow(leftTable, "LOCALITE DU CENTRE PRINCIPAL : ", data.getLocalite(), fonts);
            addInfoRow(leftTable, "CENTRE SECONDAIRE : ", data.getCentre(), fonts);
            addInfoRow(leftTable, "NOMBRE DE CANDIDATS : ", String.valueOf(data.getEffectif()), fonts);
        }

        PdfPCell leftCell = new PdfPCell(leftTable);
        leftCell.setBorder(Rectangle.NO_BORDER);
        leftCell.setVerticalAlignment(Element.ALIGN_TOP);
        infoTable.addCell(leftCell);

        // ===== PARTIE DROITE : QR Code =====
        // Construction du contenu du QR Code
        String qrContent = buildQRCodeContent(data);

        Image qrImage = generateQRCode(qrContent, 120, 120);

        PdfPCell qrCell;
        if (qrImage != null)
        {
            qrImage.setAlignment(Element.ALIGN_CENTER);
            qrImage.scaleAbsolute(100, 100);

            PdfPTable qrTable = new PdfPTable(1);
            qrTable.setWidthPercentage(100);

            PdfPCell imageCell = new PdfPCell(qrImage);
            imageCell.setBorder(Rectangle.NO_BORDER);
            imageCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            qrTable.addCell(imageCell);

            qrCell = new PdfPCell(qrTable);
        }
        else
        {
            qrCell = new PdfPCell(new Phrase("", fonts.normalFont));
        }

        qrCell.setBorder(Rectangle.NO_BORDER);
        qrCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        qrCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        infoTable.addCell(qrCell);

        return infoTable;
    }

    private void addInfoRow(PdfPTable table, String label, String value, FontConfiguration fonts) {
        Paragraph paragraph = new Paragraph();
        paragraph.add(new Chunk(label, fonts.normalFont_));
        paragraph.add(new Chunk(value, fonts.headerFont));
        paragraph.setAlignment(Element.ALIGN_CENTER);

        PdfPCell cell = new PdfPCell(paragraph);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPadding(2f);
        table.addCell(cell);
    }

    private PdfPTable createMaterialTable(FontConfiguration fonts, RepartitionCompleteFDTO data) {
        PdfPTable table = new PdfPTable(7);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{2.5f, 1.5f, 1.5f, 1.5f, 1.5f, 1.5f, 1.5f});

        // Headers
        String[] headers = {
                "Matériel de composition", "Quantité prévue", "Stock",
                "Quantité remise au 1er passage", "Reliquat",
                "Quantité remise au 2ème passage", "Reliquat"
        };

        for (String h : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(h, fonts.boldFont));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setPadding(5f);
            table.addCell(cell);
        }

        // Lignes de données
        addMaterialRow(table, "Feuilles doubles", String.valueOf(data.getFd()), fonts);
        addMaterialRow(table, "Feuilles intercalaires", String.valueOf(data.getIc()), fonts);
        addMaterialRow(table, "Feuilles de brouillon", String.valueOf(data.getFb()), fonts);

        return table;
    }

    private void addMaterialRow(PdfPTable table, String material, String quantity, FontConfiguration fonts) {
        table.addCell(centeredCell(material, fonts.normalFont));
        table.addCell(centeredCell(quantity, fonts.headerFont));
        // Cellules vides
        for (int i = 0; i < 5; i++) {
            table.addCell(centeredCell("", fonts.normalFont));
        }
    }

    private PdfPTable createSignaturesSection(FontConfiguration fonts)
    {
        PdfPTable signaturesTable = new PdfPTable(2);
        signaturesTable.setWidthPercentage(100);
        signaturesTable.setSpacingBefore(15f);
        signaturesTable.setWidths(new float[]{1f, 1f});

        String signatureBase = "Date du %s :\n\n..................................................................\n\n" +
                "Prénoms et NOM du réceptionnaire :\n\n..................................................................\n\n" +
                "Téléphone :\n\n..................................................................\n\n" +
                "Adresse email :\n\n..................................................................\n\n" +
                "Signature et cachet :\n\n..................................................................";

        PdfPCell cell1 = createSignatureCell(String.format(signatureBase, "1er passage"), fonts);
        PdfPCell cell2 = createSignatureCell(String.format(signatureBase, "2ème passage"), fonts);

        signaturesTable.addCell(cell1);
        signaturesTable.addCell(cell2);

        return signaturesTable;
    }

    private PdfPCell createSignatureCell(String text, FontConfiguration fonts)
    {
        PdfPCell cell = new PdfPCell();
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPadding(10);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPhrase(new Phrase(text, fonts.normalFont_));
        return cell;
    }

    private PdfPCell centeredCell(String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(5f);
        return cell;
    }

    private Image loadAndScaleLogo() throws IOException, DocumentException
    {
        Image logo = Image.getInstance(new ClassPathResource("images/sn.png").getInputStream().readAllBytes());
        logo.scaleToFit(75f, 70f);
        return logo;
    }

    private int getCurrentSession()
    {
        return 2025; // À rendre dynamique selon vos besoins
    }

    /**
     * Configuration des polices utilisées dans le document
     */
    private FontConfiguration initializeFonts() {
        FontConfiguration fonts = new FontConfiguration();
        fonts.titleFont = FontFactory.getFont(FontFactory.HELVETICA, 16);
        fonts.titleFont_ = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
        fonts.normalFont = FontFactory.getFont(FontFactory.HELVETICA, 10);
        fonts.normalFont_ = FontFactory.getFont(FontFactory.HELVETICA, 12);
        fonts.boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11);
        fonts.headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
        fonts.smallFont = FontFactory.getFont(FontFactory.HELVETICA, 9);
        fonts.verySmallFont = FontFactory.getFont(FontFactory.HELVETICA, 8);
        fonts.boldSmallFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);
        return fonts;
    }
    /**
     * Construit l'intégralité du document
     */
    private void buildDocument(Document document, FontConfiguration fonts, Image logo, RepartitionCompleteDTO data) throws DocumentException, UnsupportedEncodingException {
        // En-tête avec logo

        String qrContent = buildQRCodeContent_(data);
        Image qrCode = generateQRCode(qrContent, 120, 120);

        assert qrCode != null;
        addHeader(document, fonts, logo, qrCode);
        // System.out.println("addHeader"); // LOG
        // Informations principales
        addMainInfo(document, fonts, data, qrCode);
        // System.out.println("addMainInfo"); // LOG
        // Tableau des disciplines
        addDisciplinesTable(document, fonts, data, qrCode);
        addFooter(document, fonts);
        addFooter_(document, fonts);
        //System.out.println("addDisciplinesTable"); // LOG
    }

    private void buildDocumentCGS(Document document, FontConfiguration fonts, Image logo, RepartitionCompleteCGSDTO data) throws DocumentException {
        // En-tête avec logo
        //addHeader(document, fonts, logo);
        // System.out.println("addHeader"); // LOG
        // Informations principales
        addMainInfoCGS(document, fonts, data);
        // System.out.println("addMainInfo"); // LOG
        // Tableau des disciplines
        addDisciplinesTableCGS(document, fonts, data);
        addFooter(document, fonts);
        addFooter_(document, fonts);
        //System.out.println("addDisciplinesTable"); // LOG
    }

    /**
     * Ajoute l'en-tête avec logo, texte républicain et code
     */
    private void addHeader(
            Document document,
            FontConfiguration fonts,
            Image logo,
            Image qrCode) throws DocumentException {

        PdfPTable header = new PdfPTable(4);
        header.setWidthPercentage(100);
        header.setWidths(new float[]{0.90f, 2f, 1.25f, 3f});

        // Cellule logo
        logo.scalePercent(3.4F);
        PdfPCell imageCell = new PdfPCell(logo);
        imageCell.setBorder(Rectangle.NO_BORDER);
        imageCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        imageCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        header.addCell(imageCell);

        // Cellule texte République
        Paragraph headerText = new Paragraph(
                "REPUBLIQUE DU SENEGAL\n" +
                        "Un Peuple - Un But - Une Foi\n" +
                        "Ministère de l'Enseignement supérieur,\n" +
                        "de la Recherche et de l'Innovation\n" +
                        "Office du Baccalauréat",
                fonts.verySmallFont
        );

        headerText.setLeading(14f, 0);
        headerText.setAlignment(Element.ALIGN_LEFT);

        PdfPCell textCell = new PdfPCell(headerText);
        textCell.setBorder(Rectangle.NO_BORDER);
        textCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        textCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        header.addCell(textCell);

        // Cellule QR Code (au milieu)
        qrCode.scalePercent(65f);

        PdfPCell qrCell = new PdfPCell(qrCode);
        qrCell.setBorder(Rectangle.NO_BORDER);
        qrCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        qrCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        header.addCell(qrCell);

        // Cellule titre
        PdfPCell codeAcademie = new PdfPCell(
                new Phrase(
                        "BORDEREAU DE CONVOYAGE\nDE SUJETS",
                        fonts.boldFont
                )
        );
        codeAcademie.setBorder(Rectangle.NO_BORDER);
        codeAcademie.setHorizontalAlignment(Element.ALIGN_CENTER);
        codeAcademie.setVerticalAlignment(Element.ALIGN_MIDDLE);
        header.addCell(codeAcademie);

        document.add(header);
    }

    private void addHeader_(Document document, FontConfiguration fonts, Image logo) throws DocumentException {
        PdfPTable header = new PdfPTable(3);
        header.setWidthPercentage(100);
        header.setWidths(new float[]{0.90f, 2.5f, 3.5f});

        // Cellule logo
        PdfPCell imageCell = new PdfPCell(logo);
        imageCell.setBorder(Rectangle.NO_BORDER);
        imageCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        imageCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        header.addCell(imageCell);

        // Cellule texte
        Paragraph headerText = new Paragraph(
                "REPUBLIQUE DU SENEGAL\nUn Peuple - Un But - Une Foi\n" +
                        "Ministère de l'Enseignement supérieur, \nde la Recherche et de l'Innovation" +
                        "\nOffice du Baccalauréat",
                fonts.verySmallFont
        );
        headerText.setLeading(14f, 0);
        headerText.setAlignment(Element.ALIGN_LEFT);

        PdfPCell textCell = new PdfPCell(headerText);
        textCell.setBorder(Rectangle.NO_BORDER);
        textCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        textCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        header.addCell(textCell);

        // Cellule code académie
        PdfPCell codeAcademie = new PdfPCell(new Phrase("N°_BL-CS 01/UCAD/OB/PFE/PF/aat", fonts.boldFont));
        codeAcademie.setBorder(Rectangle.NO_BORDER);
        codeAcademie.setHorizontalAlignment(Element.ALIGN_CENTER);
        codeAcademie.setVerticalAlignment(Element.ALIGN_MIDDLE);
        header.addCell(codeAcademie);

        document.add(header);
    }


    private void addFooter(Document document, FontConfiguration fonts) throws DocumentException {
        PdfPTable header = new PdfPTable(2);
        header.setWidthPercentage(100);
        header.setWidths(new float[]{5f, 5f});

        int a = Year.now().getValue();

        // Cellule texte
        Paragraph headerText = new Paragraph(
                "Reçu à......................................le......../......../" + a,
                fonts.boldFont
        );
        headerText.setLeading(14f, 0);
        headerText.setAlignment(Element.ALIGN_LEFT);

        PdfPCell textCell = new PdfPCell(headerText);
        textCell.setBorder(Rectangle.NO_BORDER);
        textCell.setPaddingTop(10f);
        textCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        textCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        header.addCell(textCell);

        // Cellule code académie
        PdfPCell codeAcademie = new PdfPCell(new Phrase("Par..........................................................", fonts.boldFont));
        codeAcademie.setBorder(Rectangle.NO_BORDER);
        codeAcademie.setPaddingTop(10f);
        codeAcademie.setHorizontalAlignment(Element.ALIGN_CENTER);
        codeAcademie.setVerticalAlignment(Element.ALIGN_MIDDLE);
        header.addCell(codeAcademie);

        document.add(header);
    }


    private void addFooter_(Document document, FontConfiguration fonts) throws DocumentException {
        PdfPTable header = new PdfPTable(2);
        header.setWidthPercentage(100);
        header.setWidths(new float[]{5f, 5f});

        // Cellule texte
        Paragraph headerText = new Paragraph(
                "Observations : ",
                fonts.boldFont
        );
        headerText.setLeading(14f, 0);
        headerText.setAlignment(Element.ALIGN_LEFT);

        PdfPCell textCell = new PdfPCell(headerText);
        textCell.setBorder(Rectangle.NO_BORDER);
        textCell.setPaddingTop(15f);
        textCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        textCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        header.addCell(textCell);

        // Cellule code académie
        PdfPCell codeAcademie = new PdfPCell(new Phrase("CONVOYEUR (Signature + Nom complet)", fonts.boldFont));
        codeAcademie.setBorder(Rectangle.NO_BORDER);
        codeAcademie.setPaddingTop(15f);
        codeAcademie.setHorizontalAlignment(Element.ALIGN_CENTER);
        codeAcademie.setVerticalAlignment(Element.ALIGN_MIDDLE);
        header.addCell(codeAcademie);

        document.add(header);
    }



    private void addMainInfo(Document document, FontConfiguration fonts, RepartitionCompleteDTO data, Image qrCode) throws DocumentException {
        // Titre
        Paragraph titre = new Paragraph("BACCALAUREAT GENERAL SESSION NORMALE " + data.getSession(), fonts.boldFont);
        titre.setAlignment(Element.ALIGN_CENTER);
        titre.setSpacingBefore(5f);
        titre.setSpacingAfter(10f);
        document.add(titre);

        // Tableau principal
        PdfPTable mainInfoTable = new PdfPTable(1);
        mainInfoTable.setWidthPercentage(100);

        // Ligne Académie / Centre
        PdfPTable firstLineTable = new PdfPTable(2);
        firstLineTable.setWidthPercentage(100);
        firstLineTable.setWidths(new float[]{5f, 5f});

        String academie = data.getAcademie() != null ? data.getAcademie() : "N/A";
        String centre = data.getCentre() != null ? data.getCentre() : "N/A";

        PdfPCell academieCell = new PdfPCell(new Phrase("Académie : " + getAcademieFullName(academie), fonts.boldFont));
        academieCell.setBorder(Rectangle.NO_BORDER);
        academieCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        academieCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        academieCell.setPaddingBottom(5f);
        academieCell.setPaddingTop(5f);
        firstLineTable.addCell(academieCell);

        String result = (Boolean.TRUE.equals(data.getCp()) ? " (CP)" : "") + (Boolean.TRUE.equals(data.getCs()) ? " (CS)" : "");

        PdfPCell centreCell = new PdfPCell(new Phrase("Centre d'écrit : " + centre + result, fonts.boldFont));
        centreCell.setBorder(Rectangle.NO_BORDER);
        centreCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        centreCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        centreCell.setPaddingBottom(5f);
        centreCell.setPaddingTop(5f);
        firstLineTable.addCell(centreCell);

        mainInfoTable.addCell(new PdfPCell(firstLineTable));

        // Sort series alphabetically for consistent display
        List<String> distinctSeries = data.getSeries();

        int seriesCount = distinctSeries.size();
        String seriesList = "";
        if (seriesCount > 1) {
            seriesList = String.join(", ", distinctSeries);
        }
        if (seriesCount == 1) {
            seriesList = String.join("", distinctSeries);
        }

        // Format max value (adjust formatting as needed)
        String maxValueStr = String.valueOf(data.getEffectif());

        // Ligne Jury, Effectif, Nbre séries, Séries
        PdfPTable secondLineTable = new PdfPTable(4);
        secondLineTable.setWidthPercentage(100);
        secondLineTable.setWidths(new float[]{1.5f, 1f, 2f, 1.5f});

        secondLineTable.addCell(createCell("JURY : " + data.getJury(), fonts.normalFont, false));
        secondLineTable.addCell(createCell("EFF. : " + maxValueStr, fonts.normalFont, false));
        secondLineTable.addCell(createCell("NBR DE SERIE (S) : " + seriesCount, fonts.normalFont, false));
        secondLineTable.addCell(createCell("SERIE (S) : " + seriesList, fonts.normalFont, false));

        mainInfoTable.addCell(new PdfPCell(secondLineTable));

        document.add(mainInfoTable);
    }

    private void addMainInfoCGS(Document document, FontConfiguration fonts, RepartitionCompleteCGSDTO data) throws DocumentException {
        // Titre
        Paragraph titre = new Paragraph("CONCOURS GENERAL SENEGALAIS - SESSION : " + data.getSession(), fonts.boldFont);
        titre.setAlignment(Element.ALIGN_CENTER);
        titre.setSpacingBefore(5f);
        titre.setSpacingAfter(5f);
        document.add(titre);

        // Tableau principal
        PdfPTable mainInfoTable = new PdfPTable(1);
        mainInfoTable.setWidthPercentage(100);

        // Ligne Académie / Centre
        PdfPTable firstLineTable = new PdfPTable(2);
        firstLineTable.setWidthPercentage(100);
        firstLineTable.setWidths(new float[]{5f, 5f});

        String academie = data.getAcademie() != null ? data.getAcademie() : "N/A";
        String centre = data.getCentre() != null ? data.getCentre() : "N/A";

        PdfPCell academieCell = new PdfPCell(new Phrase(academie, fonts.boldFont));
        academieCell.setBorder(Rectangle.NO_BORDER);
        academieCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        academieCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        academieCell.setPaddingBottom(5f);
        academieCell.setPaddingTop(5f);
        firstLineTable.addCell(academieCell);

        PdfPCell centreCell = new PdfPCell(new Phrase("Centre d'écrit : " + centre, fonts.boldFont));
        centreCell.setBorder(Rectangle.NO_BORDER);
        centreCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        academieCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        centreCell.setPaddingBottom(5f);
        centreCell.setPaddingTop(5f);
        firstLineTable.addCell(centreCell);

        mainInfoTable.addCell(new PdfPCell(firstLineTable));

        List<String> distinctSeries = data.getSeries();

        int seriesCount = distinctSeries.size();
        String seriesList = "";
        if (seriesCount > 1)
        {
            seriesList = String.join(", ", distinctSeries);
        }
        if (seriesCount == 1)
        {
            seriesList = String.join("", distinctSeries);
        }

        // Format max value (adjust formatting as needed)
        String maxValueStr = String.valueOf(data.getEffectif());

        // Ligne Jury, Effectif, Nbre séries, Séries
        PdfPTable secondLineTable = new PdfPTable(3);
        secondLineTable.setWidthPercentage(100);
        secondLineTable.setWidths(new float[]{2f, 2f, 2f});

        secondLineTable.addCell(createCell("EFFECTIF DU CENTRE : " + maxValueStr, fonts.normalFont, false));
        // seriesCount
        secondLineTable.addCell(createCell("NBR. DE SERIE (S) : " + "-", fonts.normalFont, false));
        // seriesList
        secondLineTable.addCell(createCell("SERIE (S) : " + "TOUTES SERIES", fonts.normalFont, false));

        mainInfoTable.addCell(new PdfPCell(secondLineTable));

        document.add(mainInfoTable);
    }


    private void addMainInfo_(Document document, FontConfiguration fonts, RepartitionCompleteFDTO data) throws DocumentException {
        // Titre
        Paragraph titre = new Paragraph("BACCALAUREAT DE L'ENSEIGNEMENT SECONDAIRE - SESSION " + data.getSession(), fonts.boldFont);
        titre.setAlignment(Element.ALIGN_CENTER);
        titre.setSpacingBefore(5f);
        titre.setSpacingAfter(5f);
        document.add(titre);

        // Tableau principal
        PdfPTable mainInfoTable = new PdfPTable(1);
        mainInfoTable.setWidthPercentage(100);

        // Ligne Académie / Centre
        PdfPTable firstLineTable = new PdfPTable(2);
        firstLineTable.setWidthPercentage(100);
        firstLineTable.setWidths(new float[]{5f, 5f});

        String academie = data.getAcademie() != null ? data.getAcademie() : "N/A";
        String centre = data.getCentre() != null ? data.getCentre() : "N/A";

        PdfPCell academieCell = new PdfPCell(new Phrase("Académie : " + getAcademieFullName(academie), fonts.boldFont));
        academieCell.setBorder(Rectangle.NO_BORDER);
        academieCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        academieCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        academieCell.setPaddingBottom(5f);
        academieCell.setPaddingTop(5f);
        firstLineTable.addCell(academieCell);

        String result = (Boolean.TRUE.equals(data.getCp()) ? " (CP)" : "") + (Boolean.TRUE.equals(data.getCs()) ? " (CS)" : "");

        PdfPCell centreCell = new PdfPCell(new Phrase("Centre d'écrit : " + centre + result, fonts.boldFont));
        centreCell.setBorder(Rectangle.NO_BORDER);
        centreCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        academieCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        centreCell.setPaddingBottom(5f);
        centreCell.setPaddingTop(5f);
        firstLineTable.addCell(centreCell);

        mainInfoTable.addCell(new PdfPCell(firstLineTable));

        // Format max value (adjust formatting as needed)
        String maxValueStr = String.valueOf(data.getEffectif());

        // Ligne Jury, Effectif, Nbre séries, Séries
        PdfPTable secondLineTable = new PdfPTable(4);
        secondLineTable.setWidthPercentage(100);
        secondLineTable.setWidths(new float[]{1.5f, 1f, 2f, 1.5f});

        secondLineTable.addCell(createCell("NB. JURY : " + data.getNbJury(), fonts.normalFont, false));
        secondLineTable.addCell(createCell("EFF. : " + maxValueStr, fonts.normalFont, false));
        secondLineTable.addCell(createCell("NBR DE SERIE (S) : " , fonts.normalFont, false));
        secondLineTable.addCell(createCell("SERIE (S) : " , fonts.normalFont, false));

        mainInfoTable.addCell(new PdfPCell(secondLineTable));

        document.add(mainInfoTable);
    }


    private PdfPCell createCell(String text, Font font, boolean isBold) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPaddingBottom(5f);
        return cell;
    }


    private void addDisciplinesTable(Document document, FontConfiguration fonts, RepartitionCompleteDTO data, Image qrCode) throws DocumentException
    {
        PdfPTable mainTable = new PdfPTable(4);
        mainTable.setWidthPercentage(100);
        mainTable.setWidths(new float[]{3f, 1f, 1.5f, 1.5f});
        // En-têtes du tableau
        addTableHeaders(mainTable, fonts);

        AtomicInteger totalPremierIndicator = new AtomicInteger();
        AtomicInteger totalSecondIndicator = new AtomicInteger();

        // Lignes de données
        if (data.getMatieres() != null && !data.getMatieres().isEmpty())
        {
            data.getMatieres().stream()
                    .sorted(Comparator.comparing(MatiereComposeeDTO::getNom, Comparator.naturalOrder()))
                    .forEach(row -> {

                        double premier = row.getPremierGroupe();
                        double second = row.getSecondGroupe();
                        String value = premier > 0 ? "1" : "0";
                        String value_ = second > 0 ? "1" : "0";

                        if (premier > 0)
                        {
                            totalPremierIndicator.getAndIncrement();
                        }

                        if (second > 0)
                        {
                            totalSecondIndicator.getAndIncrement();
                        }

                        String champ = row.getChamp();
                        String mappedChamp;
                        List<String> target = Arrays.asList("L2", "L'1", "L1B", "L1A", "LA", "L-AR");

                        if ("matiere1".equals(champ) && row.getSeries().stream().anyMatch(target::contains))
                        {
                            mappedChamp = " - LV1";
                        }
                        else if ("matiere2".equals(champ))
                        {
                            mappedChamp = " - LV2";
                        }
                        else
                        {
                            mappedChamp = "";
                        }

                        Double groupe = (row.getPremierGroupe() == 0) ? row.getSecondGroupe() : row.getPremierGroupe();

                        addDisciplineRow(mainTable,
                                row.getNom() + mappedChamp + " (" + String.join(", ", row.getSeries()) + ")",
                                groupe.toString(),
                                value, value_,
                                fonts.normalFont
                        );
                    });
        }

        else
        {
            // Ajouter une ligne indiquant l'absence de données
            PdfPCell emptyCell = new PdfPCell(new Phrase("Aucune matière à afficher", fonts.normalFont));
            emptyCell.setColspan(4);
            emptyCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            emptyCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            emptyCell.setPadding(5f);
            mainTable.addCell(emptyCell);
        }

        // Ligne total
        addTotalRow(mainTable, fonts, totalPremierIndicator.get(), totalSecondIndicator.get());
        document.add(mainTable);
    }

    private void addDisciplinesTableCGS(Document document, FontConfiguration fonts, RepartitionCompleteCGSDTO data) throws DocumentException {
        PdfPTable mainTable = new PdfPTable(5);
        mainTable.setWidthPercentage(100);
        mainTable.setWidths(new float[]{3f, 1.5f, 1.5f, 1.5f, 1.5f});
        // En-têtes du tableau
        addTableHeadersCGS(mainTable, fonts);

        AtomicInteger totalPremierIndicator = new AtomicInteger();
        AtomicInteger totalTerminaleIndicator = new AtomicInteger();

        // Lignes de données
        if (data.getMatieres() != null && !data.getMatieres().isEmpty()) {

            data.getMatieres().stream()
                    .sorted(Comparator.comparing(MatiereComposeeCGSDTO::getDiscipline, Comparator.naturalOrder()))
                    .forEach(row -> {

                        String value1 = row.getPremiere() > 0 ? "1" : "0";
                        String value2 = row.getTerminale() > 0 ? "1" : "0";

                        if (row.getPremiere() > 0)
                        {
                            totalPremierIndicator.getAndIncrement();
                        }

                        if (row.getTerminale() > 0)
                        {
                            totalTerminaleIndicator.getAndIncrement();
                        }

                        addDisciplineRowCGS(
                                mainTable,
                                row.getDiscipline(),
                                row.getPremiere().toString(),
                                row.getTerminale().toString(),
                                value1, value2,
                                fonts.normalFont
                        );
                    });
        }

        else
        {
            // Ajouter une ligne indiquant l'absence de données
            PdfPCell emptyCell = new PdfPCell(new Phrase("Aucune matière à afficher", fonts.normalFont));
            emptyCell.setColspan(5);
            emptyCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            emptyCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            emptyCell.setPadding(5f);
            mainTable.addCell(emptyCell);
        }

        // Ligne total
        addTotalRowCGS(mainTable, fonts, totalPremierIndicator.get(), totalTerminaleIndicator.get());
        document.add(mainTable);
    }



    /**
     * Ajoute les en-têtes du tableau
     */
    private void addTableHeaders(PdfPTable table, FontConfiguration fonts) {
        String[] headers = {"DISCIPLINES", "EFFECTIF", "ENVELOPPE\n 1ER GROUPE", "ENVELOPPE\n 2ND GROUPE"};
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, fonts.boldFont));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setPadding(5f);
            table.addCell(cell);
        }
    }

    private void addTableHeadersCGS(PdfPTable table, FontConfiguration fonts) {
        String[] headers = {"DISCIPLINES","EFFECTIFS EN\nPREMIERE", "EFFECTIFS EN\nTERMINALE", "ENVELOPPES EN\nPREMIERE", "ENVELOPPES EN\nTERMINALE", };
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, fonts.boldSmallFont));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setPadding(5f);
            table.addCell(cell);
        }
    }

    /**
     * Ajoute toutes les lignes de disciplines
     */

    /**
     * Ajoute une ligne de discipline au tableau
     */
    private void addDisciplineRow(PdfPTable table, String discipline, String effectif,
                                  String groupe1, String groupe2, Font font) {
        // Discipline
        PdfPCell discCell = new PdfPCell(new Phrase(discipline, font));
        discCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        discCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        discCell.setPadding(2f);
        table.addCell(discCell);

        // Effectif
        PdfPCell effCell = new PdfPCell(new Phrase(effectif, font));
        effCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        effCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        effCell.setPadding(2f);
        table.addCell(effCell);

        // Groupe 1
        PdfPCell g1Cell = new PdfPCell(new Phrase(groupe1, font));
        g1Cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        g1Cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        g1Cell.setPadding(2f);
        table.addCell(g1Cell);

        // Groupe 2
        PdfPCell g2Cell = new PdfPCell(new Phrase(groupe2, font));
        g2Cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        g2Cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        g2Cell.setPadding(2f);
        table.addCell(g2Cell);
    }

    private void addDisciplineRowCGS(PdfPTable table, String discipline, String env1ere,
                                     String envTle, String cdt1ere, String cdtTle, Font font) {
        // Discipline
        PdfPCell discCell = new PdfPCell(new Phrase(discipline, font));
        discCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        discCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        discCell.setPadding(2f);
        table.addCell(discCell);

        PdfPCell env1ere_ = new PdfPCell(new Phrase(env1ere, font));
        env1ere_.setHorizontalAlignment(Element.ALIGN_CENTER);
        env1ere_.setVerticalAlignment(Element.ALIGN_MIDDLE);
        env1ere_.setPadding(2f);
        table.addCell(env1ere_);

        PdfPCell envTle_ = new PdfPCell(new Phrase(envTle, font));
        envTle_.setHorizontalAlignment(Element.ALIGN_CENTER);
        envTle_.setVerticalAlignment(Element.ALIGN_MIDDLE);
        envTle_.setPadding(2f);
        table.addCell(envTle_);

        PdfPCell cdt1ere_ = new PdfPCell(new Phrase(cdt1ere, font));
        cdt1ere_.setHorizontalAlignment(Element.ALIGN_CENTER);
        cdt1ere_.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cdt1ere_.setPadding(2f);
        table.addCell(cdt1ere_);

        PdfPCell cdtTle_ = new PdfPCell(new Phrase(cdtTle, font));
        cdtTle_.setHorizontalAlignment(Element.ALIGN_CENTER);
        cdtTle_.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cdtTle_.setPadding(2f);
        table.addCell(cdtTle_);
    }

    /**
     * Ajoute la ligne TOTAL ENV
     */
    private void addTotalRow(PdfPTable table, FontConfiguration fonts, int total1, int total2) {
        PdfPCell totalLabel = new PdfPCell(new Phrase("TOTAL DES ENVELOPPES A LIVRER ", fonts.boldFont));
        totalLabel.setColspan(2);
        totalLabel.setHorizontalAlignment(Element.ALIGN_RIGHT);
        totalLabel.setPadding(2f);
        table.addCell(totalLabel);

        PdfPCell totalValue = new PdfPCell(new Phrase(String.valueOf(total1), fonts.boldFont));
        totalValue.setHorizontalAlignment(Element.ALIGN_CENTER);
        totalValue.setPadding(2f);
        table.addCell(totalValue);

        PdfPCell totalValue2 = new PdfPCell(new Phrase(String.valueOf(total2), fonts.boldFont));
        totalValue2.setHorizontalAlignment(Element.ALIGN_CENTER);
        totalValue2.setPadding(2f);
        table.addCell(totalValue2);
    }


    private void addTotalRowCGS(PdfPTable table, FontConfiguration fonts, int total1, int total2) {
        PdfPCell totalLabel = new PdfPCell(new Phrase("TOTAL DES ENVELOPPES A LIVRER DANS LE CENTRE", fonts.boldFont));
        totalLabel.setColspan(3);
        totalLabel.setHorizontalAlignment(Element.ALIGN_RIGHT);
        totalLabel.setPadding(2f);
        table.addCell(totalLabel);

        PdfPCell totalValue = new PdfPCell(new Phrase(String.valueOf(total1), fonts.boldFont));
        totalValue.setHorizontalAlignment(Element.ALIGN_CENTER);
        totalValue.setPadding(2f);
        table.addCell(totalValue);

        PdfPCell totalValue2 = new PdfPCell(new Phrase(String.valueOf(total2), fonts.boldFont));
        totalValue2.setHorizontalAlignment(Element.ALIGN_CENTER);
        totalValue2.setPadding(2f);
        table.addCell(totalValue2);
    }

    /**
     * Classes internes pour organiser les données
     */
    private static class FontConfiguration {
        Font titleFont;
        Font normalFont;
        Font titleFont_;
        Font normalFont_;
        Font boldFont;
        Font headerFont;
        Font smallFont;
        Font verySmallFont;
        Font boldSmallFont;
    }


}
