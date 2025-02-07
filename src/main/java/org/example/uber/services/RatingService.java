package org.example.uber.services;

import org.example.uber.dto.DriverDTO;
import org.example.uber.dto.RiderDTO;
import org.example.uber.entities.Ride;

public interface RatingService {

    DriverDTO rateDriver(Ride ride, Integer rating);
    RiderDTO rateRider(Ride ride, Integer rating);
    void createNewRating(Ride ride);
}
