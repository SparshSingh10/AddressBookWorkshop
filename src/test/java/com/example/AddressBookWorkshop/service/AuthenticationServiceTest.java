
package com.example.AddressBookWorkshop.service;

import com.example.AddressBookWorkshop.Entity.User;
import com.example.AddressBookWorkshop.Repository.UserRepository;
import com.example.AddressBookWorkshop.dto.LoginDTO;
import com.example.AddressBookWorkshop.dto.PasswordResetDTO;
import com.example.AddressBookWorkshop.dto.RegisterDTO;
import com.example.AddressBookWorkshop.dto.UserDTO;
import com.example.AddressBookWorkshop.exception.UserException;
import com.example.AddressBookWorkshop.util.JwtToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
        import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // Enables Mockito Annotations
class AuthenticationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtToken jwtToken;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private EmailSenderService emailSenderService;

    @Mock
    private MessageProducer messageProducer;

    @InjectMocks
    private AuthenticationService authenticationService;

    private RegisterDTO registerDTO;
    private LoginDTO loginDTO;
    private User mockUser;

    @BeforeEach
    void setUp() {
        registerDTO = new RegisterDTO();
        registerDTO.setEmail("test@example.com");
        registerDTO.setPassword("password123");

        loginDTO = new LoginDTO();
        loginDTO.setEmail("test@example.com");
        loginDTO.setPassword("password123");

        mockUser = new User();
        mockUser.setEmail("test@example.com");
        mockUser.setPassword("encodedPassword");
        mockUser.setRole("USER");
    }

    // ✅ Test User Registration
    @Test
    void testRegisterUser_Success() throws UserException {
        when(userRepository.findByEmail(registerDTO.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(registerDTO.getPassword())).thenReturn("encodedPassword");
        when(modelMapper.map(registerDTO, User.class)).thenReturn(mockUser);
        when(userRepository.save(any(User.class))).thenReturn(mockUser);
        when(modelMapper.map(mockUser, UserDTO.class)).thenReturn(new UserDTO());

        UserDTO registeredUser = authenticationService.registerUser(registerDTO);

        assertNotNull(registeredUser);
        verify(emailSenderService).sendEmail(anyString(), anyString(), anyString()); // Ensure email is sent
        verify(messageProducer).sendMessage(anyString()); // Ensure message is sent
    }

    // ❌ Test User Registration Failure (Duplicate Email)
    @Test
    void testRegisterUser_Failure_EmailExists() {
        when(userRepository.findByEmail(registerDTO.getEmail())).thenReturn(Optional.of(mockUser));

        UserException exception = assertThrows(UserException.class, () -> {
            authenticationService.registerUser(registerDTO);
        });

        assertEquals("Email 'test@example.com' is already registered!", exception.getMessage());
    }

    // ✅ Test Successful Login
    @Test
    void testLoginUser_Success() throws UserException {
        when(userRepository.findByEmail(loginDTO.getEmail())).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches(loginDTO.getPassword(), mockUser.getPassword())).thenReturn(true);
        when(jwtToken.createToken(mockUser.getEmail())).thenReturn("mocked-jwt-token");

        String token = authenticationService.loginUser(loginDTO);

        assertNotNull(token);
        assertEquals("mocked-jwt-token", token);
        verify(messageProducer).sendMessage(anyString());
    }

    // ❌ Test Login Failure (Incorrect Password)
    @Test
    void testLoginUser_Failure_IncorrectPassword() {
        when(userRepository.findByEmail(loginDTO.getEmail())).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches(loginDTO.getPassword(), mockUser.getPassword())).thenReturn(false);

        UserException exception = assertThrows(UserException.class, () -> {
            authenticationService.loginUser(loginDTO);
        });

        assertEquals("Incorrect password. Please try again.", exception.getMessage());
    }

    // ✅ Test Forgot Password
    @Test
    void testForgotPassword_Success() throws UserException {
        when(userRepository.findByEmail(mockUser.getEmail())).thenReturn(Optional.of(mockUser));
        when(jwtToken.createToken(mockUser.getEmail())).thenReturn("reset-token");

        authenticationService.forgotPassword(mockUser.getEmail());

        assertEquals("reset-token", mockUser.getResetToken());
        verify(emailSenderService).sendEmail(anyString(), anyString(), anyString());
        verify(messageProducer).sendMessage(anyString());
    }

    // ❌ Test Forgot Password Failure (User Not Found)
    @Test
    void testForgotPassword_Failure_UserNotFound() {
        when(userRepository.findByEmail(mockUser.getEmail())).thenReturn(Optional.empty());

        UserException exception = assertThrows(UserException.class, () -> {
            authenticationService.forgotPassword(mockUser.getEmail());
        });

        assertEquals("No user found with email: test@example.com", exception.getMessage());
    }

    // ✅ Test Reset Password
    @Test
    void testResetPassword_Success() throws UserException {
        PasswordResetDTO passwordResetDTO = new PasswordResetDTO();
        passwordResetDTO.setEmail(mockUser.getEmail());
        passwordResetDTO.setResetToken("valid-token");
        passwordResetDTO.setNewPassword("newPassword");

        mockUser.setResetToken("valid-token");

        when(userRepository.findByEmail(mockUser.getEmail())).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.encode(passwordResetDTO.getNewPassword())).thenReturn("encodedNewPassword");

        authenticationService.resetPassword(passwordResetDTO);

        assertEquals("encodedNewPassword", mockUser.getPassword());
        assertNull(mockUser.getResetToken()); // Reset token should be null after success
        verify(userRepository).save(mockUser);
    }

    // ❌ Test Reset Password Failure (Invalid Token)
    @Test
    void testResetPassword_Failure_InvalidToken() {
        PasswordResetDTO passwordResetDTO = new PasswordResetDTO();
        passwordResetDTO.setEmail(mockUser.getEmail());
        passwordResetDTO.setResetToken("invalid-token");
        passwordResetDTO.setNewPassword("newPassword");

        mockUser.setResetToken("valid-token");

        when(userRepository.findByEmail(mockUser.getEmail())).thenReturn(Optional.of(mockUser));

        UserException exception = assertThrows(UserException.class, () -> {
            authenticationService.resetPassword(passwordResetDTO);
        });

        assertEquals("Invalid or expired reset token.", exception.getMessage());
    }
}
