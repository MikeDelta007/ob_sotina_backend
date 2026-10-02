package com.officedubac.project.ticketRestaurant;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface MotifTicketRestaurantRepository extends MongoRepository<MotifTicketRestaurant, String> {
    List<MotifTicketRestaurant> findByActifTrueOrderByLibelleAsc();
    List<MotifTicketRestaurant> findByOrderByLibelleAsc();
}
