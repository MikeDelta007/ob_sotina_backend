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

    private static final java.time.format.DateTimeFormatter FORMAT_DATE = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final TicketRestaurantRepository ticketRepo;
    private final UserRepository userRepository;
    private final PersonnelRepository personnelRepository;
    private final ExpressionBesoinRepository expressionBesoinRepo;
    private final com.officedubac.project.caisseAvance.MotifRepository motifRepository;
    private final MotifTicketRestaurantRepository motifTicketRestaurantRepository;

    // ═══════════════════════════════════════════════════════════════
    // CRÉATION (chef de service / Directeur / Admin)
    // ═══════════════════════════════════════════════════════════════
    public TicketRestaurant creer(TicketRestaurantRequest req) {
        // Un ticket restaurant couvre toujours la journée en cours, jamais une autre date.
        LocalDate date = LocalDate.now();
        if (date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY) {
            throw new RuntimeException("Aucune demande de ticket restaurant n'est possible le week-end");
        }

        MotifTicketRestaurant motif = motifTicketRestaurantRepository.findById(req.getMotifId())
                .orElseThrow(() -> new RuntimeException("Motif introuvable"));

        User createur = userRepository.findByLogin(getUsername())
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        // Un agent sélectionné peut être un agent avec compte (User) ou sans compte (Personnel),
        // la liste proposée couvrant tout le personnel.
        List<String> agentIds = req.getAgentIds().stream().distinct().collect(java.util.stream.Collectors.toList());
        List<String> agentNoms = new ArrayList<>();
        List<String> agentServices = new ArrayList<>();
        for (String agentId : agentIds) {
            agentNoms.add(resoudreNomAgent(agentId));
            agentServices.add(resoudreServiceAgent(agentId));
        }

        List<LocalDate> dates = List.of(date);
        verifierAucunDoublon(agentIds, agentNoms, dates);

        BigDecimal montantTotal = TicketRestaurant.montantParJour()
                .multiply(BigDecimal.valueOf(agentIds.size()));

        TicketRestaurant ticket = TicketRestaurant.builder()
                .motifId(motif.getId())
                .motifLibelle(motif.getLibelle())
                .dates(dates)
                .dateDebut(date)
                .dateFin(date)
                .agentIds(agentIds)
                .agentNoms(agentNoms)
                .agentServices(agentServices)
                .nombreJours(1)
                .montantTotal(montantTotal)
                .statut(TicketRestaurant.Statut.EN_ATTENTE)
                .creePar(createur.getLogin())
                .creeParNom(nomComplet(createur))
                .dateCreation(LocalDateTime.now())
                .build();

        return ticketRepo.save(ticket);
    }

    // Aucun agent ne peut avoir deux tickets le même jour : refuse si une date cochée figure déjà
    // sur une demande non rejetée (en attente ou validée) concernant l'un des agents choisis.
    private void verifierAucunDoublon(List<String> agentIds, List<String> agentNoms, List<LocalDate> dates) {
        for (TicketRestaurant existant : ticketRepo.findByStatutNot(TicketRestaurant.Statut.REJETEE)) {
            if (existant.getDates() == null || existant.getAgentIds() == null) continue;
            for (int i = 0; i < agentIds.size(); i++) {
                if (!existant.getAgentIds().contains(agentIds.get(i))) continue;
                for (LocalDate d : dates) {
                    if (existant.getDates().contains(d)) {
                        throw new RuntimeException(agentNoms.get(i) + " a déjà un ticket restaurant le "
                                + d.format(FORMAT_DATE) + " (demande de " + existant.getCreeParNom() + ")");
                    }
                }
            }
        }
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
                .motifId(motifTicketRestaurant().getId())
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

    // ═══════════════════════════════════════════════════════════════
    // MOTIFS (liste déroulante à la création) — liste propre au ticket restaurant
    // ═══════════════════════════════════════════════════════════════
    // Un compte ne voit, à la création, que les motifs sans rôle assigné (visibles de tous) ou
    // dont l'un des rôles correspond à son profil/droits supplémentaires — sauf Admin/Directeur,
    // qui supervisent l'ensemble et voient toujours tout.
    public List<MotifTicketRestaurant> getMotifs() {
        if (hasAuthority("ADMIN") || hasAuthority("DIRECTEUR") || hasAuthority("ASSISTANTE_DIRECTEUR")) {
            return motifTicketRestaurantRepository.findByActifTrueOrderByLibelleAsc();
        }
        java.util.Set<String> mesRoles = SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .map(a -> a.getAuthority())
                .collect(java.util.stream.Collectors.toSet());
        return motifTicketRestaurantRepository.findByActifTrueOrderByLibelleAsc().stream()
                .filter(m -> m.getRoles() == null || m.getRoles().isEmpty()
                        || m.getRoles().stream().anyMatch(mesRoles::contains))
                .toList();
    }

    public List<MotifTicketRestaurant> getAllMotifs() {
        return motifTicketRestaurantRepository.findByOrderByLibelleAsc();
    }

    public MotifTicketRestaurant creerMotif(String libelle, List<String> roles) {
        return motifTicketRestaurantRepository.save(MotifTicketRestaurant.builder().libelle(libelle).roles(roles).actif(true).build());
    }

    public MotifTicketRestaurant modifierMotif(String id, String libelle, boolean actif, List<String> roles) {
        MotifTicketRestaurant motif = motifTicketRestaurantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Motif introuvable"));
        motif.setLibelle(libelle);
        motif.setActif(actif);
        motif.setRoles(roles);
        return motifTicketRestaurantRepository.save(motif);
    }

    public void supprimerMotif(String id) {
        motifTicketRestaurantRepository.findById(id).ifPresent(m -> { m.setActif(false); motifTicketRestaurantRepository.save(m); });
    }

    // ── Utilitaires ──
    private Personnel resoudrePersonnel(String agentId) {
        return personnelRepository.findById(agentId)
                .orElseThrow(() -> new RuntimeException("Agent introuvable : " + agentId));
    }

    private String resoudreNomAgent(String agentId) {
        Personnel p = resoudrePersonnel(agentId);
        String nom = ((p.getFirstname() != null ? p.getFirstname() : "") + " "
                + (p.getLastname() != null ? p.getLastname() : "")).trim();
        return nom.isEmpty() ? "—" : nom;
    }

    private String resoudreServiceAgent(String agentId) {
        Personnel p = resoudrePersonnel(agentId);
        if (p.getDivision() == null || p.getDivision().getLibelle() == null) return "—";
        return p.getDivision().getLibelle();
    }

    private com.officedubac.project.caisseAvance.Motif motifTicketRestaurant() {
        return motifRepository.findFirstByLibelleAndSystemeTrue("Ticket restaurant")
                .orElseGet(() -> motifRepository.save(com.officedubac.project.caisseAvance.Motif.builder()
                        .libelle("Ticket restaurant").actif(true).systeme(true).build()));
    }

    private String nomComplet(User u) {
        if (u.getPersonnel() == null) return u.getLogin();
        String nom = (u.getPersonnel().getFirstname() + " " + u.getPersonnel().getLastname()).trim();
        return nom.isEmpty() ? u.getLogin() : nom;
    }

    private boolean hasAuthority(String authority) {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(authority));
    }

    private String getUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
