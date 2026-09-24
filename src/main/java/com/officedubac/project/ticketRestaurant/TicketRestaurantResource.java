package com.officedubac.project.ticketRestaurant;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/api/v1/ticket-restaurant")
@RequiredArgsConstructor
public class TicketRestaurantResource {

    private final TicketRestaurantService ticketService;
    private final TicketRestaurantPdfService pdfService;

    // Accès réservé aux comptes ayant le droit spécifique "Gère Ticket Restaurant" (accordé au
    // cas par cas par un admin, indépendamment du rôle : plusieurs agents/chefs sont aussi ADMIN,
    // PEDAGOGIE ou PLANIFICATION, le rôle seul ne permet pas de les distinguer), plus le Directeur.
    private static final String ROLES = "hasAnyAuthority('TICKET_RESTAURANT','DIRECTEUR')";

    @PreAuthorize(ROLES)
    @PostMapping
    public ResponseEntity<TicketRestaurant> creer(@Valid @RequestBody TicketRestaurantRequest req) {
        return ResponseEntity.ok(ticketService.creer(req));
    }

    @PreAuthorize(ROLES)
    @GetMapping("/mine")
    public ResponseEntity<List<TicketRestaurant>> getMesTickets() {
        return ResponseEntity.ok(ticketService.getMesTickets());
    }

    @PreAuthorize("hasAuthority('DIRECTEUR')")
    @GetMapping("/a-valider")
    public ResponseEntity<List<TicketRestaurant>> getAValider() {
        return ResponseEntity.ok(ticketService.getAValider());
    }

    @PreAuthorize("hasAuthority('DIRECTEUR')")
    @GetMapping("/toutes")
    public ResponseEntity<List<TicketRestaurant>> getToutes() {
        return ResponseEntity.ok(ticketService.getToutes());
    }

    @PreAuthorize("hasAuthority('DIRECTEUR')")
    @PutMapping("/{id}/valider")
    public ResponseEntity<TicketRestaurant> valider(@PathVariable String id) {
        return ResponseEntity.ok(ticketService.valider(id));
    }

    @PreAuthorize("hasAuthority('DIRECTEUR')")
    @PutMapping("/{id}/rejeter")
    public ResponseEntity<TicketRestaurant> rejeter(@PathVariable String id, @Valid @RequestBody RejeterTicketRequest req) {
        return ResponseEntity.ok(ticketService.rejeter(id, req.getMotif()));
    }

    @PreAuthorize(ROLES)
    @GetMapping("/{id}/liste.pdf")
    public void telechargerListe(@PathVariable String id, HttpServletResponse response) throws IOException {
        TicketRestaurant ticket = ticketService.getById(id);
        if (ticket.getStatut() != TicketRestaurant.Statut.VALIDEE) {
            throw new RuntimeException("Cette demande n'est pas encore validée");
        }
        // Seul le Directeur voit toutes les demandes ; les autres uniquement les leurs
        String appelant = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getName();
        boolean directeur = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities().stream().anyMatch(a -> "DIRECTEUR".equals(a.getAuthority()));
        if (!directeur && !appelant.equals(ticket.getCreePar())) {
            throw new org.springframework.security.access.AccessDeniedException("Cette demande ne vous appartient pas");
        }

        byte[] pdf = pdfService.genererListe(ticket);

        String filename = "tickets_restaurant_" + id + ".pdf";
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition",
                "attachment; filename=\"" + filename + "\"; filename*=UTF-8''"
                + URLEncoder.encode(filename, StandardCharsets.UTF_8).replace("+", "%20"));
        response.setContentLength(pdf.length);
        response.getOutputStream().write(pdf);
    }
}
