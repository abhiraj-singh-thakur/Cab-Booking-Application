package org.example.uber.repositories;

import org.example.uber.entities.Driver;
import org.example.uber.entities.Rating;
import org.example.uber.entities.Ride;
import org.example.uber.entities.Rider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RatingRepository extends JpaRepository<Rating, Long> {
    List<Rating> findByRider(Rider rider);

    List<Rating> findByDriver(Driver driver);

    Optional<Rating> findByRide(Ride ride);
}
