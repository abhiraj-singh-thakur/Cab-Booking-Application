package org.example.uber.services.implementation;

import lombok.RequiredArgsConstructor;
import org.example.uber.dto.DriverDTO;
import org.example.uber.dto.SignupDTO;
import org.example.uber.dto.UserDTO;
import org.example.uber.entities.Driver;
import org.example.uber.entities.User;
import org.example.uber.entities.enums.Role;
import org.example.uber.expections.ResourceNotFoundException;
import org.example.uber.expections.RuntimeConflictException;
import org.example.uber.repositories.DriverRepository;
import org.example.uber.repositories.UserRepository;
import org.example.uber.security.JWTService;
import org.example.uber.services.AuthService;
import org.example.uber.services.RiderService;
import org.example.uber.services.UserService;
import org.example.uber.services.WalletService;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final RiderService riderService;
    private final WalletService walletService;
    private final DriverRepository driverRepository;
    private final DriverServiceImpl driverServiceImpl;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;
    private final UserService userService;


    @Override
    public Map<String, String> login(String email, String password) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );
        User user = (User) authentication.getPrincipal();

        if(user == null) {
            throw new UsernameNotFoundException("User with email " + email + " not found");
        }

        return Map.of(
                "access_token", jwtService.generateAccessToken(user),
                "refresh_token", jwtService.generateRefreshToken(user)
        );
    }


    @Override
    @Transactional
    public UserDTO signup(SignupDTO signupDto) {

        User user = userRepository.findByEmail(signupDto.getEmail()).orElse(null);
        if (user != null) {
            throw new RuntimeConflictException("Cannot signup, User already exists with email " + signupDto.getEmail());
        }

        User mappedUser = modelMapper.map(signupDto, User.class);
        mappedUser.setRoles(Set.of(Role.RIDER));
        mappedUser.setPassword(passwordEncoder.encode(mappedUser.getPassword()));
        User savedUser = userRepository.save(mappedUser);

        riderService.createNewRider(savedUser);
        walletService.createNewWallet(savedUser);

        return modelMapper.map(savedUser, UserDTO.class);

    }

    @Override
    public String refreshToken(String refreshToken) {
        Long userId = jwtService.getUserIdFromToken(refreshToken);
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found " +
                "with id: "+userId));

        return jwtService.generateAccessToken(user);
    }

    @Override
    public DriverDTO onboardNewDriver(Long userId, String vehicleId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + userId + " not found"));

//        if (user.getRoles().contains(Role.RIDER)) {
//            throw new RuntimeConflictException("User with id " + userId + " already has rider role");
//        }
        if (driverRepository.existsByVehicleId(vehicleId)) {
            throw new RuntimeConflictException("Vehicle with ID " + vehicleId + " is already registered");
        }

        Driver createdDriver = Driver.builder()
                .user(user)
                .vehicleId(vehicleId)
                .available(true)
                .build();

        user.getRoles().remove(Role.RIDER);
        user.getRoles().add(Role.DRIVER);
        userRepository.save(user);
        Driver savedDriver = driverServiceImpl.createNewDriver(createdDriver);
        return modelMapper.map(savedDriver, DriverDTO.class);
    }
}

