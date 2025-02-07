package org.example.uber.services.implementation;

import org.example.uber.dto.*;
import org.example.uber.entities.*;
import org.example.uber.entities.enums.RideRequestStatus;
import org.example.uber.repositories.DriverRepository;
import org.example.uber.repositories.RideRequestRepository;
import org.example.uber.services.DriverService;
import org.example.uber.services.RideRequestService;
import org.example.uber.services.RideService;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DriverServiceImplTest {

    @Mock
    private RideRequestService rideRequestService;

    @Mock
    private RideService rideService;

    @Mock
    private DriverRepository driverRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private DriverServiceImpl driverService;

    private User user;
    private UserDTO userDTO;
    private Driver driver;
    private DriverDTO driverDTO;
    private Point driverLocation;
    private PointDTO driverLocationDTO;
    private PointDTO pickupPointDTO;
    private PointDTO dropOffPointDTO;
    private RideRequest rideRequest;
    private Rider rider;
    private RiderDTO riderDTO;
    private Ride ride;
    private RideDTO rideDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        GeometryFactory geometryFactory = new GeometryFactory();
        user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setEmail("test@test.com");

        userDTO = modelMapper.map(user, UserDTO.class);

        rider = new Rider();
        rider.setId(1L);
        rider.setUser(user);

        riderDTO = new RiderDTO();
        riderDTO.setId(1L);
        riderDTO.setUser(userDTO);

        User driverUser = new User();
        driverUser.setId(2L);

        driver = new Driver();
        driver.setId(1L);
        driver.setUser(driverUser);
        driver.setAvailable(true);
        driverLocation = geometryFactory.createPoint(new Coordinate(-74.005, 40.7127));
        driverLocationDTO = new PointDTO(new double[]{-74.0059, 40.7127});

        driverDTO = modelMapper.map(driverUser, DriverDTO.class);


        Point pickupLocation = geometryFactory.createPoint(new Coordinate(-74.006, 40.7128));
        Point dropOffLocation = geometryFactory.createPoint(new Coordinate(-73.9865, 40.7329));

        pickupPointDTO = new PointDTO(new double[]{-74.006, 40.7128});
        dropOffPointDTO = new PointDTO(new double[]{-73.9865, 40.7329});

        rideRequest = new RideRequest();
        rideRequest.setId(1L);
        rideRequest.setRideRequestStatus(RideRequestStatus.PENDING);
        rideRequest.setPickupLocation(pickupLocation);
        rideRequest.setDropOffLocation(dropOffLocation);

        ride = new Ride();
        ride.setId(1L);
        ride.setPickupLocation(pickupLocation);
        ride.setDropOffLocation(dropOffLocation);

        rideDTO = new RideDTO();
        rideDTO.setId(1L);
        rideDTO.setPickupLocation(pickupPointDTO);
        rideDTO.setDropOffLocation(dropOffPointDTO);
        rideDTO.setDriver(driverDTO);
        rideDTO.setRider(riderDTO);

        System.out.println("rideRequestService: " + rideRequestService);
        System.out.println("driverService: " + driverService);

    }

    @Test
    public void testAcceptRide_whenSuccessful() {
        // Arrange
        when(rideRequestService.findRideRequestById(1L)).thenReturn(rideRequest);
        when(driverService.getCurrentDriver()).thenReturn(driver);
        when(driverService.updateDriverAvailability(driver, false)).thenReturn(driver);
        when(rideService.createNewRide(rideRequest, driver)).thenReturn(ride);
        when(modelMapper.map(ride, RideDTO.class)).thenReturn(rideDTO);

        // Act
        RideDTO result = driverService.acceptRide(1L);

        // Assert
        assertEquals(rideDTO, result);
        verify(rideRequestService, times(1)).findRideRequestById(1L);
        verify(driverService, times(1)).getCurrentDriver();
        verify(driverService, times(1)).updateDriverAvailability(driver, false);
        verify(rideService, times(1)).createNewRide(rideRequest, driver);
        verify(modelMapper, times(1)).map(ride, RideDTO.class);
    }

}