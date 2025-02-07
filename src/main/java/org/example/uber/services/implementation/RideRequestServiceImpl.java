package org.example.uber.services.implementation;

import lombok.RequiredArgsConstructor;
import org.example.uber.entities.RideRequest;
import org.example.uber.expections.ResourceNotFoundException;
import org.example.uber.repositories.RideRequestRepository;
import org.example.uber.services.RideRequestService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RideRequestServiceImpl implements RideRequestService {

    private final RideRequestRepository rideRequestRepository;

    @Override
    public RideRequest findRideRequestById(Long rideRequestId) {
        return rideRequestRepository.findById(rideRequestId)
                .orElseThrow(() -> new ResourceNotFoundException("RideRequest not found with id: " + rideRequestId));
    }

    @Override
    public void update(RideRequest rideRequest) {
        RideRequest toSave = rideRequestRepository.findById(rideRequest.getId())
                .orElseThrow(() -> new ResourceNotFoundException("RideRequest not found with id: " + rideRequest.getId()));
        rideRequestRepository.save(toSave);
    }
}
