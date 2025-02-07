package org.example.uber.services.implementation;

import org.example.uber.dto.DriverDTO;
import org.example.uber.dto.UserDTO;
import org.example.uber.entities.Driver;
import org.example.uber.expections.ResourceNotFoundException;
import org.example.uber.expections.RuntimeConflictException;
import org.example.uber.repositories.DriverRepository;
import org.springframework.security.core.Authentication;
import org.example.uber.dto.SignupDTO;
import org.example.uber.entities.User;
import org.example.uber.entities.enums.Role;
import org.example.uber.repositories.UserRepository;
import org.example.uber.security.JWTService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest  {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JWTService jwtService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private DriverRepository driverRepository;

    @Mock
    private RiderServiceImpl riderService;

    @Mock
    private WalletServiceImpl walletService;

    @Mock
    private DriverServiceImpl driverService;

    @Spy
    private ModelMapper modelMapper;

    @Spy
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthServiceImpl authService;

    private User user;
    private SignupDTO signupDTO;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setPassword("password");
        user.setRoles(Set.of(Role.RIDER));

        signupDTO = new SignupDTO();
        signupDTO.setEmail("test@example.com");
        signupDTO.setPassword("password");
    }

    @Test
    void testLogin_whenSuccess() {
        // Arrange
        String email = "test@example.com";
        String password = "password";

        Authentication authentication = mock(Authentication.class);

        when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(user);
        when(jwtService.generateAccessToken(user)).thenReturn("accessToken");
        when(jwtService.generateRefreshToken(user)).thenReturn("refreshToken");

        // Act
        Map<String, String> result = authService.login(email, password);

        // Assert
        assertNotNull(result);
        assertEquals("accessToken", result.get("access_token"));
        assertEquals("refreshToken", result.get("refresh_token"));

    }
    @Test
    void testLogin_whenUserNotFound() {
        // Arrange
        String email = "notfound@example.com";
        String password = "password";

        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(authentication);

        // Act & Assert
        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class,
                () -> authService.login(email, password));


        // Verify the exception message
        assertEquals("User with email " + email + " not found", exception.getMessage());

        // Verify method calls
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtService, never()).generateAccessToken(any());
        verify(jwtService, never()).generateRefreshToken(any());
        verify(userRepository, never()).findByEmail(anyString());
    }

    @Test
    void testSignup_whenSuccess(){
//        arrange
        SignupDTO signupDto = new SignupDTO();
        signupDto.setName("Test User");
        signupDto.setEmail("test@example.com");
        signupDto.setPassword("password123");

        User mappedUser = new User();
        mappedUser.setName("Test User");
        mappedUser.setEmail("test@example.com");
        mappedUser.setPassword("password123");
        mappedUser.setRoles(Set.of(Role.RIDER));

        User savedUser = new User();
        savedUser.setName("Test User");
        savedUser.setEmail("test@example.com");
        savedUser.setPassword("password123");
        savedUser.setRoles(Set.of(Role.RIDER));

        UserDTO userDTO = new UserDTO();
        userDTO.setName("Test User");
        userDTO.setEmail("test@example.com");
        userDTO.setRoles(Set.of(Role.RIDER));

        when(userRepository.save(any())).thenReturn(savedUser);
        when(modelMapper.map(signupDto, User.class)).thenReturn(mappedUser);
        when(passwordEncoder.encode(mappedUser.getPassword())).thenReturn("password123");
        when(userRepository.save(mappedUser)).thenReturn(mappedUser);
        when(modelMapper.map(mappedUser, UserDTO.class)).thenReturn(userDTO);


//        act
        UserDTO result = authService.signup(signupDto);

//        assert
        assertThat(result).isNotNull();
        assertEquals("Test User", result.getName());
        assertEquals("test@example.com", result.getEmail());
        assertEquals(Set.of(Role.RIDER), result.getRoles());

//        verify
        verify(riderService).createNewRider(any(User.class));
        verify(walletService).createNewWallet(any(User.class));
    }

    @Test
    void testSignup_whenUserExits(){
        // Arrange
        SignupDTO testSignupDTO = new SignupDTO();
        testSignupDTO.setEmail("test@example.com");
        testSignupDTO.setPassword("password123");


        when(userRepository.findByEmail(testSignupDTO.getEmail())).thenReturn(Optional.of(user));

        // Act & Assert
        RuntimeConflictException exception = assertThrows(RuntimeConflictException.class,
                () -> authService.signup(testSignupDTO));

        assertEquals("Cannot signup, User already exists with email " + testSignupDTO.getEmail(), exception.getMessage());

        // Verify
        verify(userRepository).findByEmail(testSignupDTO.getEmail());
        verify(userRepository, never()).save(any(User.class));
        verify(riderService, never()).createNewRider(any(User.class));
        verify(walletService, never()).createNewWallet(any(User.class));


    }

    @Test
    void testRefreshToken_whenSuccess(){
        // arrange
        String refreshToken = "refreshToken";
        Long userId = 1L;
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("password");
        user.setRoles(Set.of(Role.RIDER));
        user.setId(userId);

        String expected_token = "NewAccessToken";
        when(jwtService.getUserIdFromToken(refreshToken)).thenReturn(userId);
        when(jwtService.generateAccessToken(user)).thenReturn(expected_token);

        // Mock the userRepository.findById() method to return the user
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // act
        String result = authService.refreshToken(refreshToken);

        // assert
        assertNotNull(result);
        assertEquals(expected_token, result);

        // verify
        verify(jwtService).getUserIdFromToken(refreshToken);
        verify(jwtService).generateAccessToken(user);
        verify(userRepository).findById(userId);
    }

    @Test
    void testRefreshToken_whenUserNotFound() {
        // arrange
        String refreshToken = "invalidRefreshToken";
        Long userId = 1L;

        when(jwtService.getUserIdFromToken(refreshToken)).thenReturn(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // act and assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> authService.refreshToken(refreshToken));

        // assert
        assertEquals("User not found with id: " + userId, exception.getMessage());

        // verify
        verify(jwtService).getUserIdFromToken(refreshToken);
        verify(userRepository).findById(userId);
        verify(jwtService, never()).generateAccessToken(any());
    }

    @Test
    void testOnBoardNewDriver_whenSuccess(){
//        arrange
        Long userId = 1L;
        String vehicleId = "vehicleId";
        User user = new User();
        user.setId(userId);
        user.setRoles(new HashSet<>(Collections.singletonList(Role.RIDER)));

        Driver createdDriver = new Driver();
        createdDriver.setUser(user);
        createdDriver.setVehicleId(vehicleId);
        createdDriver.setAvailable(true);


        DriverDTO expectedDriverDTO = new DriverDTO();
        expectedDriverDTO.setAvailable(true);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(driverRepository.existsByVehicleId(vehicleId)).thenReturn(false);
        when(driverService.createNewDriver(any(Driver.class))).thenReturn(createdDriver);
        when(modelMapper.map(createdDriver, DriverDTO.class)).thenReturn(expectedDriverDTO);

//        Act

        DriverDTO result = authService.onboardNewDriver(userId, vehicleId);

//        assert
        assertNotNull(result);
        assertTrue(user.getRoles().contains(Role.DRIVER));
        assertFalse(user.getRoles().contains(Role.RIDER));

//        verify
        verify(userRepository).findById(userId);
        verify(driverRepository).existsByVehicleId(vehicleId);
        verify(driverService).createNewDriver(any(Driver.class));
        verify(userRepository).save(user);
        verify(modelMapper).map(createdDriver, DriverDTO.class);
    }

    @Test
    void testOnBoardNewDriver_whenUserNotFound() {
//        arrange
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
//        act and assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> authService.onboardNewDriver(userId, "vehicleId"));

//        assert
        assertEquals("User with id " + userId + " not found", exception.getMessage());

//        verify
        verify(userRepository).findById(userId);

    }

    @Test
    void testOnBoardNewDriver_whenVehicleFound() {
//        arrange
        Long userId = 1L;
        String vehicleId = "vehicleId";

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(driverRepository.existsByVehicleId(vehicleId)).thenReturn(true);

        RuntimeConflictException exception = assertThrows(RuntimeConflictException.class,
                () -> authService.onboardNewDriver(userId, vehicleId));

        assertEquals("Vehicle with ID " + vehicleId + " is already registered", exception.getMessage());

        verify(userRepository).findById(userId);
        verify(driverRepository).existsByVehicleId(vehicleId);
    }
}