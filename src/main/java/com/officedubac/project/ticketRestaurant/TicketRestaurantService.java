package com.officedubac.project.ticketRestaurant;

import com.officedubac.project.expressionBesoin.ExpressionBesoin;
import com.officedubac.project.expressionBesoin.ExpressionBesoinRepository;
import com.officedubac.project.models.User;
import com.officedubac.project.personnel.Personnel;
import com.officedubac.project.personnel.PersonnelRepository;
import com.officedubac.project.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TicketRestaurantService {

    private final TicketRestaurantRepository ticketRepo;
    private final UserRepository userRepository;
    private final PersonnelRepository personnelRepository;
    private final ExpressionBesoinRepository expressionBesoinRepo;

    // ═══════════════════════════════════════════════════════════════
    // CRÉATION (chef de service / Directeur / Admin)
    // ═══════════════════════════════════════════════════════════════
    public TicketRestaurant creer(TicketRestaurantRequest req) {
        if (req.getDateDebut().isBefore(LocalDate.now())) {
            throw new RuntimeException("La date de début ne peut pas être antérieure à aujourd'hui");
        }
        if (req.getDateFin().isBefore(req.getDateDebut())) {
            throw new RuntimeException("La date de fin ne peut pas précéder la date de début");
        }
        if (!req.isLundi() && !req.isMardi() && !req.isMercredi() && !req.isJeudi() && !req.isVendredi()) {
            throw new RuntimeException("Au moins un jour de la semaine doit être coché");
        }

        User createur = userRepository.findByLogin(getUsername())
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        // Un agent sélectionné peut être un agent avec compte (User) ou un agent externe sans
        // compte (Personnel) — même logique de résolution que le bénéficiaire d'une expression
        // de besoin, la liste provenant de /personnel/mes-agents qui mélange les deux.
        List<String> agentNoms = new ArrayList<>();
        for (String agentId : req.getAgentIds()) {
            agentNoms.add(resoudreNomAgent(agentId));
        }

        int nombreJours = compterJours(req.getDateDebut(), req.getDateFin(), req);
        BigDecimal montantTotal = TicketRestaurant.montantParJour()
                .multiply(BigDecimal.valueOf(nombreJours))
                .multiply(BigDecimal.valueOf(req.getAgentIds().size()));

        TicketRestaurant ticket = TicketRestaurant.builder()
                .dateDebut(req.getDateDebut())
                .dateFin(req.getDateFin())
                .lundi(req.isLundi())
                .mardi(req.isMardi())
                .mercredi(req.isMercredi())
                .jeudi(req.isJeudi())
                .vendredi(req.isVendredi())
                .agentIds(req.getAgentIds())
                .agentNoms(agentNoms)
                .nombreJours(nombreJours)
                .montantTotal(montantTotal)
                .statut(TicketRestaurant.Statut.EN_ATTENTE)
                .creePar(createur.getLogin())
                .creeParNom(nomComplet(createur))
                .dateCreation(LocalDateTime.now())
                .build();

        return ticketRepo.save(ticket);
    }

    // Compte, dans [dateDebut, dateFin] inclus, les jours dont le jour de la semaine est coché.
    private int compterJours(LocalDate debut, LocalDate fin, TicketRestaurantRequest req) {
        int n = 0;
        for (LocalDate d = debut; !d.isAfter(fin); d = d.plusDays(1)) {
            if (jourCoche(d.getDayOfWeek(), req)) n++;
        }
        return n;
    }

    private boolean jourCoche(DayOfWeek jour, TicketRestaurantRequest req) {
        return switch (jour) {
            case MONDAY -> req.isLundi();
            case TUESDAY -> req.isMardi();
            case WEDNESDAY -> req.isMercredi();
            case THURSDAY -> req.isJeudi();
            case FRIDAY -> req.isVendredi();
            default -> false;
        };
    }

    // ═══════════════════════════════════════════════════════════════
    // VALIDATION (Directeur)
    // ═══════════════════════════════════════════════════════════════
    public TicketRestaurant valider(String id) {
        TicketRestaurant ticket = getById(id);
        if (ticket.getStatut() != TicketRestaurant.Statut.EN_ATTENTE)
            throw new RuntimeException("Cette demande n'est plus en attente de validation");

        User directeur = userRepository.findByLogin(getUsername())
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        ticket.setValidationDirecteur(true);
        ticket.setValidateurDirecteur(directeur.getLogin());
        ticket.setValidateurDirecteurNom(nomComplet(directeur));
        ticket.setDateValidationDirecteur(LocalDateTime.now());
        ticket.setStatut(TicketRestaurant.Statut.VALIDEE);

        // Fait l'objet d'un décaissement : crée l'expression de besoin correspondante, déjà
        // validée (la validation du Directeur ici en tient lieu), prête pour le traitement
        // comptable puis le mandatement/décaissement — comme n'importe quelle autre dépense.
        ExpressionBesoin eb = ExpressionBesoin.builder()
                .motifLibelle("Ticket restaurant")
                .montantInitial(ticket.getMontantTotal())
                .prixUnitaire(TicketRestaurant.montantParJour())
                .quantite(ticket.getNombreJours() * (ticket.getAgentIds() != null ? ticket.getAgentIds().size() : 0))
                .requiertSatisfaction(false)
                .aFacturePreformat(false)
                .statut(ExpressionBesoin.Statut.VALIDEE)
                .beneficiaireNom("Tickets restaurant — " + (ticket.getAgentNoms() != null ? ticket.getAgentNoms().size() : 0) + " agent(s)")
                .beneficiaireMoiMeme(false)
                .validationDirecteur(true)
                .validateurDirecteur(directeur.getLogin())
                .validateurDirecteurNom(nomComplet(directeur))
                .dateValidationDirecteur(LocalDateTime.now())
                .creePar(ticket.getCreePar())
                .creeParNom(ticket.getCreeParNom())
                .build();
        eb = expressionBesoinRepo.save(eb);
        ticket.setExpressionBesoinId(eb.getId());

        return ticketRepo.save(ticket);
    }

    public TicketRestaurant rejeter(String id, String motif) {
        TicketRestaurant ticket = getById(id);
        if (ticket.getStatut() != TicketRestaurant.Statut.EN_ATTENTE)
            throw new RuntimeException("Cette demande n'est plus en attente de validation");

        User rejetant = userRepository.findByLogin(getUsername())
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        ticket.setStatut(TicketRestaurant.Statut.REJETEE);
        ticket.setMotifRejet(motif);
        ticket.setRejetePar(rejetant.getLogin());
        ticket.setRejeteParNom(nomComplet(rejetant));
        ticket.setDateRejet(LocalDateTime.now());
        return ticketRepo.save(ticket);
    }

    // ═══════════════════════════════════════════════════════════════
    // LECTURE
    // ═══════════════════════════════════════════════════════════════
    public TicketRestaurant getById(String id) {
        return ticketRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Demande de tickets restaurant introuvable : " + id));
    }

    public List<TicketRestaurant> getMesTickets() {
        return ticketRepo.findByCreeParOrderByDateCreationDesc(getUsername());
    }

    public List<TicketRestaurant> getAValider() {
        return ticketRepo.findByStatutOrderByDateCreationDesc(TicketRestaurant.Statut.EN_ATTENTE);
    }

    public List<TicketRestaurant> getToutes() {
        return ticketRepo.findAllByOrderByDateCreationDesc();
    }

    // ── Utilitaires ──
    private String resoudreNomAgent(String agentId) {
        User user = userRepository.findById(agentId).orElse(null);
        if (user != null) return nomComplet(user);

        Personnel personnel = personnelRepository.findById(agentId)
                .orElseThrow(() -> new RuntimeException("Agent introuvable : " + agentId));
        String nom = ((personnel.getFirstname() != null ? personnel.getFirstname() : "") + " "
                + (personnel.getLastname() != null ? personnel.getLastname() : "")).trim();
        return nom.isEmpty() ? "—" : nom;
    }

    private String nomComplet(User u) {
        if (u.getPersonnel() == null) return u.getLogin();
        String nom = (u.getPersonnel().getFirstname() + " " + u.getPersonnel().getLastname()).trim();
        return nom.isEmpty() ? u.getLogin() : nom;
    }

    private String getUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
