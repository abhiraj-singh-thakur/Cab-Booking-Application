package org.example.uber.services.implementation;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.uber.dto.DriverDTO;
import org.example.uber.dto.RideDTO;
import org.example.uber.dto.RideRequestDTO;
import org.example.uber.dto.RiderDTO;
import org.example.uber.entities.*;
import org.example.uber.entities.enums.RideRequestStatus;
import org.example.uber.entities.enums.RideStatus;
import org.example.uber.expections.ResourceNotFoundException;
import org.example.uber.repositories.RideRequestRepository;
import org.example.uber.repositories.RiderRepository;
import org.example.uber.services.DriverService;
import org.example.uber.services.RatingService;
import org.example.uber.services.RideService;
import org.example.uber.services.RiderService;
import org.example.uber.strategy.StrategyManager;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RiderServiceImpl implements RiderService {

    private final ModelMapper modelMapper;
    private final StrategyManager strategyManager;
    private final RideRequestRepository rideRequestRepository;
    private final RiderRepository riderRepository;
    private final RideService rideService;
    private final RatingService ratingService;
    private final DriverService driverService;

    @Override
    @Transactional
    public RideRequestDTO requestRide(RideRequestDTO rideRequestDTO) {

        Rider rider = getCurrentRider();

        if (!rider.getAvailable())
            throw new ResourceNotFoundException("Cannot create a new ride as it has not been available");

        RideRequest rideRequest = modelMapper.map(rideRequestDTO, RideRequest.class);
        rideRequest.setRideRequestStatus(RideRequestStatus.PENDING);
        rideRequest.setRider(rider);

        Rider savedRider = updateRiderAvailability(rider, false);

        Double fare = strategyManager.rideFairCalculationStrategy().calculateFair(rideRequest);
        rideRequest.setFare(fare);

        RideRequest savedRideRequest = rideRequestRepository.save(rideRequest);

        List<Driver> drivers = strategyManager
                .driverMatchingStrategy(rider.getRatings()).findMatchingDrivers(rideRequest);

//        TODO : Send notification to all the drivers about this ride request

        return modelMapper.map(savedRideRequest, RideRequestDTO.class);
    }

    @Override
    public RideRequestDTO getRequestById(long requestId) {
        RideRequest rideRequest = rideRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("RideRequest not found with id: " + requestId));

        return modelMapper.map(rideRequest, RideRequestDTO.class);
    }

    @Override
    public RideDTO cancelRide(Long rideId) {

        Rider rider = getCurrentRider();
        Ride ride = rideService.getRideById(rideId);

        if (!rider.equals(ride.getRider())) {
            throw new RuntimeException(("Rider does not own this ride with id: " + rideId));
        }

        if (!ride.getRideStatus().equals(RideStatus.CONFIRMED)) {
            throw new RuntimeException("Ride cannot be cancelled, invalid status: " + ride.getRideStatus());
        }

        Ride savedRide = rideService.updateRideStatus(ride, RideStatus.CANCELLED);
        driverService.updateDriverAvailability(ride.getDriver(), true);
        updateRiderAvailability(rider, true);

        return modelMapper.map(savedRide, RideDTO.class);
    }

    @Override
    public DriverDTO rateDriver(Long rideId, Integer rating) {
        Ride ride = rideService.getRideById(rideId);
        Rider rider = getCurrentRider();

        if(!rider.equals(ride.getRider())) {
            throw new RuntimeException("Rider is not the owner of this Ride");
        }

        if(!ride.getRideStatus().equals(RideStatus.ENDED)) {
            throw new RuntimeException("Ride status is not Ended hence cannot start rating, status: "+ride.getRideStatus());
        }

        return ratingService.rateDriver(ride, rating);
    }

    @Override
    public RiderDTO getMyProfile() {
        Rider currentRider = getCurrentRider();
        return modelMapper.map(currentRider, RiderDTO.class);
    }

    @Override
    public Page<RideDTO> getMyAllRides(PageRequest pageRequest) {
        return rideService.getAllRidesOfRider(getCurrentRider(), pageRequest).map(
                ride -> modelMapper.map(ride, RideDTO.class)
        );
    }

    @Override
    public Rider createNewRider(User user) {
        Rider rider = Rider.builder()
                .user(user)
                .ratings(0.0)
                .available(true)
                .build();
        return riderRepository.save(rider);
    }

    @Override
    public Rider getCurrentRider() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return riderRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Rider not found with id: " + user.getId()));
    }

    @Override
    public Rider updateRiderAvailability(Rider rider, boolean availability) {
        rider.setAvailable(availability);
        riderRepository.save(rider);
        return rider;
    }
}
