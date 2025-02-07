package org.example.uber.controllers;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.uber.dto.*;
import org.example.uber.services.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    ResponseEntity<UserDTO> signIn(@RequestBody SignupDTO signupDTO) {
        return new ResponseEntity<>(authService.signup(signupDTO), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO loginRequestDTO, HttpServletResponse response) {
        Map<String, String> tokens = authService.login(loginRequestDTO.getEmail(), loginRequestDTO.getPassword());

        Cookie cookie = new Cookie("refresh_token", tokens.get("refresh_token"));
        cookie.setHttpOnly(true);

        response.addCookie(cookie);

        return ResponseEntity.ok(new LoginResponseDTO(tokens.get("access_token")));
    }

    @PostMapping("/refresh")
    ResponseEntity<LoginResponseDTO> refresh(HttpServletRequest request) {
        System.out.println(request);

        String refreshToken = Arrays.stream(request.getCookies()).
                filter(cookie -> "refresh_token".equals(cookie.getName()))
                .findFirst()
                .map(Cookie::getValue)
                .orElseThrow(() -> new AuthenticationServiceException("Refresh token not found inside the Cookies"));

        String accessToken = authService.refreshToken(refreshToken);

        return ResponseEntity.ok(new LoginResponseDTO(accessToken));
    }

    @Secured("ROLE_ADMIN")
    @PostMapping("/onboardNewDrivers/{userId}")
    ResponseEntity<DriverDTO> onboardNewDrivers(@PathVariable Long userId, @RequestBody OnBoardDriverDTO onBoardDriverDTO){
        return new ResponseEntity<>(authService.onboardNewDriver(userId, onBoardDriverDTO.getVehicleId()), HttpStatus.CREATED);
    }
}
