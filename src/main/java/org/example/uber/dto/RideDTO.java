package org.example.uber.dto;

import lombok.Data;
import org.example.uber.entities.enums.PaymentMethod;
import org.example.uber.entities.enums.RideStatus;

import java.time.LocalDateTime;

@Data
public class RideDTO {
    private Long id;

    private PointDTO pickupLocation;
    private PointDTO dropOffLocation;

    private LocalDateTime createdTime;

    private RiderDTO rider;
    private DriverDTO driver;

    private PaymentMethod paymentMethod;

    private RideStatus status;

    private Double fare;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;

    private String otp;
}
