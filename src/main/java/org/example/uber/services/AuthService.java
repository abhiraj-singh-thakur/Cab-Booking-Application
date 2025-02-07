package org.example.uber.services;

import org.example.uber.dto.DriverDTO;
import org.example.uber.dto.SignupDTO;
import org.example.uber.dto.UserDTO;

import java.util.Map;

public interface AuthService {
    Map login(String email, String password);

    UserDTO signup(SignupDTO signupDto);

    DriverDTO onboardNewDriver(Long userId,  String vehicleId);

    String refreshToken(String token);

}
