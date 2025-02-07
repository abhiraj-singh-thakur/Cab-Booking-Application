package org.example.uber.strategy;

import org.example.uber.entities.Driver;
import org.example.uber.entities.RideRequest;

import java.util.List;

public interface DriverMatchingStrategy {

    List<Driver> findMatchingDrivers(RideRequest rideRequest);
}
