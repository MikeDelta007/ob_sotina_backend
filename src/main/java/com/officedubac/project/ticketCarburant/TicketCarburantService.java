package com.officedubac.project.ticketCarburant;

import com.officedubac.project.models.User;
import com.officedubac.project.models.Ville;
import com.officedubac.project.repository.UserRepository;
import com.officedubac.project.repository.VilleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.Year;
import java.util.List;

// Demande de tickets carburant : CSA puis Directeur valident, le Directeur seul tranche le
// nombre de tickets accordés — comme sur la fiche papier "DEMANDE DE CARBURANT".
@Slf4j
@Service
@RequiredArgsConstructor
public class TicketCarburantService {

    private final TicketCarburantRepository ticketRepo;
    private final UserRepository userRepository;
    private final VilleRepository villeRepository;
    private final MotifCarburantRepository motifRepository;
    private final MongoTemplate mongoTemplate;

    // ═══════════════════════════════════════════════════════════════
    // CRÉATION
    // ═══════════════════════════════════════════════════════════════
    public TicketCarburant creer(TicketCarburantRequest req) {
        if (req.getVilleDepartId().equals(req.getVilleArriveeId()))
            throw new RuntimeException("La ville de départ et la ville d'arrivée doivent être différentes");

        Ville depart = villeRepository.findById(req.getVilleDepartId())
                .orElseThrow(() -> new RuntimeException("Ville de départ introuvable"));
        Ville arrivee = villeRepository.findById(req.getVilleArriveeId())
                .orElseThrow(() -> new RuntimeException("Ville d'arrivée introuvable"));
        MotifCarburant motif = motifRepository.findById(req.getMotifId())
                .orElseThrow(() -> new RuntimeException("Motif introuvable"));

        User createur = userRepository.findByLogin(getUsername())
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        TicketCarburant ticket = TicketCarburant.builder()
                .numeroFiche(genererNumero())
                .divisionLibelle(createur.getPersonnel() != null && createur.getPersonnel().getDivision() != null
                        ? createur.getPersonnel().getDivision().getLibelle() : null)
                .motifId(motif.getId())
                .motifLibelle(motif.getLibelle())
                .date(req.getDate())
                .villeDepartId(depart.getId())
                .villeDepartNom(depart.getName())
                .villeArriveeId(arrivee.getId())
                .villeArriveeNom(arrivee.getName())
                .nombreTicketsDemande(req.getNombreTicketsDemande())
                .statut(TicketCarburant.Statut.EN_ATTENTE)
                .creePar(createur.getLogin())
                .creeParNom(nomComplet(createur))
                .dateCreation(LocalDateTime.now())
                .build();

        return ticketRepo.save(ticket);
    }

    // ═══════════════════════════════════════════════════════════════
    // VALIDATION (CSA / Directeur)
    // ═══════════════════════════════════════════════════════════════
    public TicketCarburant valider(String id, ValiderTicketCarburantRequest req) {
        TicketCarburant ticket = getById(id);
        if (ticket.getStatut() != TicketCarburant.Statut.EN_ATTENTE)
            throw new RuntimeException("Cette demande n'est plus en attente de validation");

        User validateur = userRepository.findByLogin(getUsername())
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
        boolean estCsa = hasAuthority("CSA");
        boolean estDirecteur = hasAuthority("DIRECTEUR") || hasAuthority("ASSISTANTE_DIRECTEUR");
        if (!estCsa && !estDirecteur)
            throw new RuntimeException("Rôle non autorisé à valider une demande de carburant");

        if (estCsa) {
            ticket.setValidationCsa(true);
            ticket.setValidateurCsa(validateur.getLogin());
            ticket.setValidateurCsaNom(nomComplet(validateur));
            ticket.setDateValidationCsa(LocalDateTime.now());
        }
        if (estDirecteur) {
            if (req.getNombreTicketsAccorde() == null || req.getNombreTicketsAccorde() <= 0)
                throw new RuntimeException("Le nombre de tickets accordé est requis");
            ticket.setNombreTicketsAccorde(req.getNombreTicketsAccorde());
            ticket.setValidationDirecteur(true);
            ticket.setValidateurDirecteur(validateur.getLogin());
            ticket.setValidateurDirecteurNom(nomComplet(validateur));
            ticket.setDateValidationDirecteur(LocalDateTime.now());
            ticket.setStatut(TicketCarburant.Statut.VALIDEE);
        }

        return ticketRepo.save(ticket);
    }

    // Comme pour les expressions de besoin : le Directeur tranche toujours définitivement,
    // y compris après un rejet du CSA — un rejet CSA seul n'est donc jamais final ici.
    public TicketCarburant rejeter(String id, String motif) {
        TicketCarburant ticket = getById(id);
        if (ticket.getStatut() != TicketCarburant.Statut.EN_ATTENTE)
            throw new RuntimeException("Cette demande ne peut plus être rejetée");

        User rejetant = userRepository.findByLogin(getUsername())
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
        boolean estCsa = hasAuthority("CSA");
        boolean estDirecteur = hasAuthority("DIRECTEUR") || hasAuthority("ASSISTANTE_DIRECTEUR");
        if (!estCsa && !estDirecteur)
            throw new RuntimeException("Rôle non autorisé à rejeter une demande de carburant");

        if (estCsa) {
            ticket.setRejetCsa(true);
            ticket.setMotifRejetCsa(motif);
            ticket.setRejeteParCsaNom(nomComplet(rejetant));
            ticket.setDateRejetCsa(LocalDateTime.now());
        } else {
            ticket.setStatut(TicketCarburant.Statut.REJETEE);
            ticket.setMotifRejet(motif);
            ticket.setRejetePar(rejetant.getLogin());
            ticket.setRejeteParNom(nomComplet(rejetant));
            ticket.setDateRejet(LocalDateTime.now());
        }

        return ticketRepo.save(ticket);
    }

    // ═══════════════════════════════════════════════════════════════
    // LECTURE
    // ═══════════════════════════════════════════════════════════════
    public TicketCarburant getById(String id) {
        return ticketRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Demande de carburant introuvable : " + id));
    }

    public List<TicketCarburant> getMesTickets() {
        return ticketRepo.findByCreeParOrderByDateCreationDesc(getUsername());
    }

    // Ne montre que ce qu'il reste réellement à valider pour le rôle connecté.
    public List<TicketCarburant> getAValider() {
        boolean estCsa = hasAuthority("CSA");
        boolean estDirecteur = hasAuthority("DIRECTEUR") || hasAuthority("ASSISTANTE_DIRECTEUR");
        return ticketRepo.findByStatutOrderByDateCreationDesc(TicketCarburant.Statut.EN_ATTENTE).stream()
                .filter(t -> (estCsa && !t.isValidationCsa() && !t.isRejetCsa()) || (estDirecteur && !t.isValidationDirecteur()))
                .toList();
    }

    public List<TicketCarburant> getToutes() {
        return ticketRepo.findAllByOrderByDateCreationDesc();
    }

    // ═══════════════════════════════════════════════════════════════
    // MOTIFS (liste déroulante à la création) — liste propre au ticket carburant
    // ═══════════════════════════════════════════════════════════════
    public List<MotifCarburant> getMotifs() {
        return motifRepository.findByActifTrueOrderByLibelleAsc();
    }

    public List<MotifCarburant> getAllMotifs() {
        return motifRepository.findByOrderByLibelleAsc();
    }

    public MotifCarburant creerMotif(String libelle) {
        return motifRepository.save(MotifCarburant.builder().libelle(libelle).actif(true).build());
    }

    public MotifCarburant modifierMotif(String id, String libelle, boolean actif) {
        MotifCarburant motif = motifRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Motif introuvable"));
        motif.setLibelle(libelle);
        motif.setActif(actif);
        return motifRepository.save(motif);
    }

    public void supprimerMotif(String id) {
        motifRepository.findById(id).ifPresent(m -> { m.setActif(false); motifRepository.save(m); });
    }

    // ── Numéro de fiche : CARB_<année>_<NN>, séquence atomique par année ──
    private String genererNumero() {
        int annee = Year.now().getValue();
        org.bson.Document compteur = mongoTemplate.findAndModify(
                Query.query(Criteria.where("_id").is("ticket_carburant_" + annee)),
                new Update().inc("seq", 1),
                FindAndModifyOptions.options().returnNew(true).upsert(true),
                org.bson.Document.class, "sequences");
        return "CARB_" + annee + "_" + String.format("%02d", compteur.getInteger("seq"));
    }

    // ── Utilitaires ──
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
