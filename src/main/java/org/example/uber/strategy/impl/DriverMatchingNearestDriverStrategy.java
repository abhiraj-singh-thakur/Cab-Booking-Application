package org.example.uber.strategy.impl;

import lombok.RequiredArgsConstructor;
import org.example.uber.entities.Driver;
import org.example.uber.entities.RideRequest;
import org.example.uber.repositories.DriverRepository;
import org.example.uber.strategy.DriverMatchingStrategy;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DriverMatchingNearestDriverStrategy implements DriverMatchingStrategy {


    private final DriverRepository driverRepository;

    @Override
    public List<Driver> findMatchingDrivers(RideRequest rideRequest) {
        return driverRepository.findTenNearestDrivers(rideRequest.getPickupLocation());
    }
}
