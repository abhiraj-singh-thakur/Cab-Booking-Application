package org.example.uber.services;

import org.example.uber.dto.DriverDTO;
import org.example.uber.dto.RideDTO;
import org.example.uber.dto.RideRequestDTO;
import org.example.uber.dto.RiderDTO;
import org.example.uber.entities.Rider;
import org.example.uber.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;

public interface RiderService {
    RideRequestDTO requestRide(RideRequestDTO rideRequestDTO);

    RideDTO cancelRide(Long rideId);

    DriverDTO rateDriver(Long rideId, Integer rating);

    RiderDTO getMyProfile();

    Page<RideDTO> getMyAllRides(PageRequest pageRequest);

    Rider createNewRider(User user);

    Rider getCurrentRider();

    RideRequestDTO getRequestById(long requestId);

    Rider updateRiderAvailability(Rider rider, boolean availability);
}
