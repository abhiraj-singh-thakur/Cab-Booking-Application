package org.example.uber.strategy;

import org.example.uber.entities.RideRequest;

public interface RideFairCalculationStrategy {

    double RIDE_FARE_MULTIPLIER = 10;

    double calculateFair(RideRequest rideRequest);
}
