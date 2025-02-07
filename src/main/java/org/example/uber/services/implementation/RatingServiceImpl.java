package org.example.uber.services.implementation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.uber.dto.DriverDTO;
import org.example.uber.dto.RiderDTO;
import org.example.uber.entities.Driver;
import org.example.uber.entities.Rating;
import org.example.uber.entities.Ride;
import org.example.uber.entities.Rider;
import org.example.uber.expections.InternalServerErrorException;
import org.example.uber.expections.ResourceNotFoundException;
import org.example.uber.expections.RuntimeConflictException;
import org.example.uber.repositories.DriverRepository;
import org.example.uber.repositories.RatingRepository;
import org.example.uber.repositories.RiderRepository;
import org.example.uber.services.RatingService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RatingServiceImpl implements RatingService {

    private final RatingRepository ratingRepository;
    private final DriverRepository driverRepository;
    private final RiderRepository riderRepository;
    private final ModelMapper modelMapper;

    @Override
    public DriverDTO rateDriver(Ride ride, Integer rating) {
        Driver driver = ride.getDriver();
        Rating ratingObj = ratingRepository.findByRide(ride)
                .orElseThrow(() -> new ResourceNotFoundException("Rating not found for ride with id: "+ride.getId()));

        if(ratingObj.getDriverRating() != null)
            throw new RuntimeConflictException("Driver has already been rated, cannot rate again");

        ratingObj.setDriverRating(rating);

        ratingRepository.save(ratingObj);

        Double newRating = ratingRepository.findByDriver(driver)
                .stream()
                .mapToDouble(Rating::getDriverRating)
                .average().orElse(0.0);
        driver.setRating(newRating);

        Driver savedDriver = driverRepository.save(driver);
        return modelMapper.map(savedDriver, DriverDTO.class);
    }

    @Override
    public RiderDTO rateRider(Ride ride, Integer rating) {
        try {
            Rider rider = ride.getRider();
            Rating ratingObj = ratingRepository.findByRide(ride)
                    .orElseThrow(() -> new ResourceNotFoundException("Rating not found for ride with id: "+ride.getId()));
            if(ratingObj.getRiderRating() != null)
                throw new RuntimeConflictException("Rider has already been rated, cannot rate again");

            ratingObj.setRiderRating(rating);

            ratingRepository.save(ratingObj);

            Double newRating = ratingRepository.findByRider(rider)
                    .stream()
                    .mapToDouble(Rating::getRiderRating)
                    .average().orElse(0.0);
            rider.setRatings(newRating);

            Rider savedRider = riderRepository.save(rider);
            return modelMapper.map(savedRider, RiderDTO.class);
        } catch (ResourceNotFoundException | RuntimeConflictException e) {
            log.error("Error while rating rider: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while rating rider: {}", e.getMessage());
            throw new InternalServerErrorException("An unexpected error occurred while processing the rating", e);
        }
    }

    @Override
    public void createNewRating(Ride ride) {
        Rating rating = Rating.builder()
                .rider(ride.getRider())
                .driver(ride.getDriver())
                .ride(ride)
                .build();
        ratingRepository.save(rating);
    }
}
