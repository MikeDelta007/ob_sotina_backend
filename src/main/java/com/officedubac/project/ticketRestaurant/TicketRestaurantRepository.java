package com.officedubac.project.ticketRestaurant;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface TicketRestaurantRepository extends MongoRepository<TicketRestaurant, String> {
    List<TicketRestaurant> findByCreeParOrderByDateCreationDesc(String creePar);
    List<TicketRestaurant> findByStatutOrderByDateCreationDesc(TicketRestaurant.Statut statut);
    List<TicketRestaurant> findAllByOrderByDateCreationDesc();
}
