package org.example.uber.controllers;

import org.example.uber.entities.Ride;
import org.example.uber.repositories.RideRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

@RequestMapping("/ride")
public class RideController {

    private final RideRepository rideRepository;

    public RideController(RideRepository rideRepository) {
        this.rideRepository = rideRepository;
    }

    @GetMapping("/{rideId}")
    public ResponseEntity<Optional<Ride>> getRide(@PathVariable Long rideId) {
        Optional<Ride> ride = rideRepository.findById(rideId);
        if (ride.isPresent()) {
            return ResponseEntity.ok(ride);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}
