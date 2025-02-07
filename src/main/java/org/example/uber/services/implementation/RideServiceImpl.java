package org.example.uber.services.implementation;

import lombok.RequiredArgsConstructor;
import org.example.uber.entities.Driver;
import org.example.uber.entities.Ride;
import org.example.uber.entities.RideRequest;
import org.example.uber.entities.Rider;
import org.example.uber.entities.enums.RideStatus;
import org.example.uber.expections.ResourceNotFoundException;
import org.example.uber.repositories.RideRepository;
import org.example.uber.repositories.RideRequestRepository;
import org.example.uber.services.RideRequestService;
import org.example.uber.services.RideService;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class RideServiceImpl implements RideService {

    private final RideRepository rideRepository;
    private final RideRequestService rideRequestService;
    private final ModelMapper modelMapper;
    private final RideRequestRepository rideRequestRepository;

    @Override
    public Ride getRideById(Long id) {
        return rideRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Ride with id " + id + " not found"));
    }


    @Override
    public Ride createNewRide(RideRequest rideRequest, Driver driver) {
        Ride ride = modelMapper.map(rideRequest, Ride.class);
        ride.setDriver(driver);
        ride.setRideStatus(RideStatus.CONFIRMED);
        ride.setOtp(generateRandomOTP());
        ride.setId(null);

        rideRequestService.update(rideRequest);
        return rideRepository.save(ride);
    }

    @Override
    public Ride updateRideStatus(Ride ride, RideStatus rideStatus) {
        ride.setRideStatus(rideStatus);
        return rideRepository.save(ride);
    }

    @Override
    public Page<Ride> getAllRidesOfRider(Rider rider, PageRequest pageRequest) {
        return rideRepository.findByRider(rider, pageRequest);
    }

    @Override
    public Page<Ride> getAllRidesOfDriver(Driver driver, PageRequest pageRequest) {
        return rideRepository.findByDriver(driver, pageRequest);
    }

    private String generateRandomOTP() {
        Random random = new Random();
        return String.format("%04d", random.nextInt(1000));
    }
}
