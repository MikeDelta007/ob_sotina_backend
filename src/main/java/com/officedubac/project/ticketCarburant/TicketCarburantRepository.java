package com.officedubac.project.ticketCarburant;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface TicketCarburantRepository extends MongoRepository<TicketCarburant, String> {
    List<TicketCarburant> findByCreeParOrderByDateCreationDesc(String creePar);
    List<TicketCarburant> findByStatutOrderByDateCreationDesc(TicketCarburant.Statut statut);
    List<TicketCarburant> findAllByOrderByDateCreationDesc();
}
