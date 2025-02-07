package org.example.uber.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.uber.entities.enums.PaymentMethod;
import org.example.uber.entities.enums.RideRequestStatus;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RideRequestDTO {
    private Long id;

    private PointDTO pickupLocation;
    private PointDTO dropOffLocation;
    private PaymentMethod paymentMethod;

    private LocalDateTime requestedTime;
    private RiderDTO rider;
    private RideRequestStatus status;

    private Double fare;

}
