package org.example.uber.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DriverDTO {
    private Long id;
    private UserDTO user;
    private Double ratings;
    private String vehicleId;
    private Boolean available;
}
