package com.officedubac.project.ticketCarburant;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/api/v1/ticket-carburant")
@RequiredArgsConstructor
public class TicketCarburantResource {

    private final TicketCarburantService ticketService;
    private final TicketCarburantPdfService pdfService;

    // Seuls les comptes avec le rôle supplémentaire TICKET_CARBURANT peuvent demander —
    // à la différence des tickets restaurant, le CSA et le Directeur ne créent pas de
    // demande ici, ils ne font que valider.
    private static final String ROLE_DEMANDEUR = "hasAuthority('TICKET_CARBURANT')";
    private static final String ROLES_VALIDATEURS = "hasAnyAuthority('CSA','DIRECTEUR','ASSISTANTE_DIRECTEUR')";
    private static final String ROLES_LECTURE = "hasAnyAuthority('TICKET_CARBURANT','CSA','DIRECTEUR','ASSISTANTE_DIRECTEUR')";

    @PreAuthorize(ROLE_DEMANDEUR)
    @PostMapping
    public ResponseEntity<TicketCarburant> creer(@Valid @RequestBody TicketCarburantRequest req) {
        return ResponseEntity.ok(ticketService.creer(req));
    }

    @PreAuthorize(ROLE_DEMANDEUR)
    @GetMapping("/mine")
    public ResponseEntity<List<TicketCarburant>> getMesTickets() {
        return ResponseEntity.ok(ticketService.getMesTickets());
    }

    @PreAuthorize(ROLES_VALIDATEURS)
    @GetMapping("/a-valider")
    public ResponseEntity<List<TicketCarburant>> getAValider() {
        return ResponseEntity.ok(ticketService.getAValider());
    }

    @PreAuthorize(ROLES_VALIDATEURS)
    @GetMapping("/toutes")
    public ResponseEntity<List<TicketCarburant>> getToutes() {
        return ResponseEntity.ok(ticketService.getToutes());
    }

    @PreAuthorize(ROLES_VALIDATEURS)
    @PutMapping("/{id}/valider")
    public ResponseEntity<TicketCarburant> valider(@PathVariable String id, @RequestBody ValiderTicketCarburantRequest req) {
        return ResponseEntity.ok(ticketService.valider(id, req));
    }

    @PreAuthorize(ROLES_VALIDATEURS)
    @PutMapping("/{id}/rejeter")
    public ResponseEntity<TicketCarburant> rejeter(@PathVariable String id, @Valid @RequestBody RejeterTicketCarburantRequest req) {
        return ResponseEntity.ok(ticketService.rejeter(id, req.getMotif()));
    }

    @PreAuthorize(ROLES_LECTURE)
    @GetMapping("/{id}/fiche.pdf")
    public void telechargerFiche(@PathVariable String id, HttpServletResponse response) throws IOException {
        TicketCarburant ticket = ticketService.getById(id);
        if (ticket.getStatut() != TicketCarburant.Statut.VALIDEE)
            throw new RuntimeException("Cette demande n'est pas encore validée");

        boolean estValidateur = SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .anyMatch(a -> "CSA".equals(a.getAuthority()) || "DIRECTEUR".equals(a.getAuthority())
                        || "ASSISTANTE_DIRECTEUR".equals(a.getAuthority()));
        String appelant = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!estValidateur && !appelant.equals(ticket.getCreePar()))
            throw new org.springframework.security.access.AccessDeniedException("Cette demande ne vous appartient pas");

        byte[] pdf = pdfService.genererFiche(ticket);

        String filename = "demande_carburant_" + id + ".pdf";
        response.setContentType(MediaType.APPLICATION_PDF_VALUE);
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + filename + "\"; filename*=UTF-8''"
                + URLEncoder.encode(filename, StandardCharsets.UTF_8).replace("+", "%20"));
        response.setContentLength(pdf.length);
        response.getOutputStream().write(pdf);
    }

    // ── Motifs (liste déroulante à la création) — liste propre au ticket carburant ──
    @PreAuthorize(ROLE_DEMANDEUR)
    @GetMapping("/motifs")
    public ResponseEntity<List<MotifCarburant>> getMotifs() {
        return ResponseEntity.ok(ticketService.getMotifs());
    }

    // Tous les motifs (actifs et inactifs) — pour l'écran de gestion
    @PreAuthorize("hasAnyAuthority('ADMIN','CSA','DIRECTEUR','CHEF_COMPTABLE')")
    @GetMapping("/motifs/all")
    public ResponseEntity<List<MotifCarburant>> getAllMotifs() {
        return ResponseEntity.ok(ticketService.getAllMotifs());
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','CSA','DIRECTEUR','CHEF_COMPTABLE')")
    @PostMapping("/motifs")
    public ResponseEntity<MotifCarburant> creerMotif(@RequestBody MotifCarburant motif) {
        return ResponseEntity.ok(ticketService.creerMotif(motif.getLibelle()));
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','CSA','DIRECTEUR','CHEF_COMPTABLE')")
    @PutMapping("/motifs/{id}")
    public ResponseEntity<MotifCarburant> modifierMotif(@PathVariable String id, @RequestBody MotifCarburant motif) {
        return ResponseEntity.ok(ticketService.modifierMotif(id, motif.getLibelle(), motif.isActif()));
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','CSA','DIRECTEUR','CHEF_COMPTABLE')")
    @DeleteMapping("/motifs/{id}")
    public ResponseEntity<Void> supprimerMotif(@PathVariable String id) {
        ticketService.supprimerMotif(id);
        return ResponseEntity.noContent().build();
    }
}
