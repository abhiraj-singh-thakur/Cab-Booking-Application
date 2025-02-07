package org.example.uber.services.implementation;

import lombok.RequiredArgsConstructor;
import org.example.uber.dto.DriverDTO;
import org.example.uber.dto.RideDTO;
import org.example.uber.dto.RiderDTO;
import org.example.uber.entities.*;
import org.example.uber.entities.enums.RideRequestStatus;
import org.example.uber.entities.enums.RideStatus;
import org.example.uber.expections.ResourceNotFoundException;
import org.example.uber.repositories.DriverRepository;
import org.example.uber.repositories.RideRepository;
import org.example.uber.services.*;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class DriverServiceImpl implements DriverService {

    private final RideRequestService rideRequestService;
    private final DriverRepository driverRepository;
    private final RideService rideService;
    private final ModelMapper modelMapper;
    private final PaymentService paymentService;
    private final RatingService ratingService;

    @Override
    @Transactional
    public RideDTO acceptRide(Long rideRequestId) {
        RideRequest rideRequest = rideRequestService.findRideRequestById(rideRequestId);
        if (!rideRequest.getRideRequestStatus().equals(RideRequestStatus.PENDING))
            throw new RuntimeException("Ride request status is not PENDING, " + rideRequest.getRideRequestStatus());

        Driver currentDriver = getCurrentDriver();

        if (!currentDriver.getAvailable())
            throw new RuntimeException("Driver is not available");

        Driver savedDriver = updateDriverAvailability(currentDriver, false);

        Ride ride = rideService.createNewRide(rideRequest, savedDriver);

        return modelMapper.map(ride, RideDTO.class);
    }

    @Override
    public RideDTO cancelRide(Long rideId) {
        Ride ride = rideService.getRideById(rideId);
        Driver driver = getCurrentDriver();

        if (!driver.equals(ride.getDriver()))
            throw new RuntimeException("Driver cannot cancel a ride, as it is accepted by other driver");

        if (!ride.getRideStatus().equals(RideStatus.CONFIRMED))
            throw new RuntimeException("Ride cannot be cancelled, invalid status " + ride.getRideStatus());

        if (!ride.getRideStatus().equals(RideStatus.CANCELLED))
            throw new RuntimeException("Ride already cancelled, " + ride.getRideStatus());

        rideService.updateRideStatus(ride, RideStatus.CANCELLED);
        updateDriverAvailability(driver, true);

        return modelMapper.map(ride, RideDTO.class);
    }

    @Override
    public RideDTO startRide(Long rideId, String otp) {
        Ride ride = rideService.getRideById(rideId);
        Driver driver = getCurrentDriver();

        if (!driver.equals(ride.getDriver()))
            throw new RuntimeException("Driver cannot start a ride, as it is accepted by other driver");

        if (!ride.getRideStatus().equals(RideStatus.CONFIRMED))
            throw new RuntimeException("Ride status is not confirmed, " + ride.getRideStatus());

        if (!ride.getOtp().equals(otp))
            throw new RuntimeException("Ride OTP is not valid");

        ride.setStartedAt(LocalDateTime.now());
        Ride savedRide = rideService.updateRideStatus(ride, RideStatus.ONGOING);

        paymentService.createNewPayment(savedRide);
        ratingService.createNewRating(savedRide);

        return modelMapper.map(savedRide, RideDTO.class);
    }

    @Override
    public RideDTO endRide(Long rideId) {
        Ride ride = rideService.getRideById(rideId);
        Driver driver = getCurrentDriver();

        if (!driver.equals(ride.getDriver())) {
            throw new RuntimeException("Driver cannot start a ride as he has not accepted it earlier");
        }

        if (!ride.getRideStatus().equals(RideStatus.ONGOING)) {
            throw new RuntimeException("Ride status is not ONGOING hence cannot be ended, status: " + ride.getRideStatus());
        }

        ride.setEndedAt(LocalDateTime.now());
        Ride savedRide = rideService.updateRideStatus(ride, RideStatus.ENDED);
        updateDriverAvailability(driver, true);
        Rider rider = ride.getRider();
        rider.setAvailable(true);

        paymentService.processPayment(ride);

        return modelMapper.map(savedRide, RideDTO.class);
    }

    @Override
    public RiderDTO rateRider(Long rideId, Integer rating) {
        Ride ride = rideService.getRideById(rideId);
        Driver driver = getCurrentDriver();

        if (!driver.equals(ride.getDriver())) {
            throw new RuntimeException("Driver is not the owner of this ride");
        }
        if (!ride.getRideStatus().equals(RideStatus.ENDED)) {
            throw new RuntimeException("Ride status is not Ended hence cannot be rated, status: " + ride.getRideStatus());
        }
        return ratingService.rateRider(ride, rating);
    }

    @Override
    public DriverDTO getMyProfile() {
        Driver currentDriver = getCurrentDriver();
        return modelMapper.map(currentDriver, DriverDTO.class);
    }

    @Override
    public Page<RideDTO> getMyAllRides(PageRequest pageRequest) {
        return rideService.getAllRidesOfDriver(getCurrentDriver(), pageRequest).map(
                ride -> modelMapper.map(ride, RideDTO.class)
        );
    }

    @Override
    public Driver getCurrentDriver() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return driverRepository.findByUser(user).orElseThrow(() -> new ResourceNotFoundException("Driver not associated with current user, id: " + user.getId()));
    }

    @Override
    public Driver updateDriverAvailability(Driver driver, boolean availability) {
        driver.setAvailable(availability);
        driverRepository.save(driver);
        return driver;
    }

    @Override
    public Driver createNewDriver(Driver driver) {
        return driverRepository.save(driver);
    }
}
