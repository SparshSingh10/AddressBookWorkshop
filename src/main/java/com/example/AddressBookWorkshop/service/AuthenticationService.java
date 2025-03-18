package com.example.AddressBookWorkshop.service;

import com.example.AddressBookWorkshop.Entity.User;
import com.example.AddressBookWorkshop.Repository.UserRepository;
import com.example.AddressBookWorkshop.dto.LoginDTO;
import com.example.AddressBookWorkshop.dto.PasswordResetDTO;
import com.example.AddressBookWorkshop.dto.UserDTO;
import com.example.AddressBookWorkshop.dto.RegisterDTO;
import com.example.AddressBookWorkshop.exception.UserException;
import com.example.AddressBookWorkshop.interfaces.IAuthenticationService;
import com.example.AddressBookWorkshop.util.JwtToken;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class AuthenticationService implements IAuthenticationService {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtToken jwtToken;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private EmailSenderService emailSenderService;

    @Autowired
    private MessageProducer messageProducer;

    // Register a new user
    @Override
    @Transactional
    public UserDTO registerUser(RegisterDTO registerDTO) throws UserException {
        if (userRepository.findByEmail(registerDTO.getEmail()).isPresent()) {
            throw new UserException("Email '" + registerDTO.getEmail() + "' is already registered!");
        }

        // Encrypt the password
        String encryptedPassword = passwordEncoder.encode(registerDTO.getPassword());

        // Map DTO to entity and set values
        User user = modelMapper.map(registerDTO, User.class);
        user.setPassword(encryptedPassword);
        user.setRole("USER");

        // Save user
        User savedUser = userRepository.save(user);

        // Send welcome email
        emailSenderService.sendEmail(savedUser.getEmail(), "Registration Successful", "Welcome to Address Book!");

        // Send registration message
        String customMessage = "REGISTER|" + savedUser.getEmail() + "|" + savedUser.getName();
        messageProducer.sendMessage(customMessage);

        return modelMapper.map(savedUser, UserDTO.class);
    }

    // Login user
    @Override
    public String loginUser(LoginDTO loginDTO) throws UserException {
        User user = userRepository.findByEmail(loginDTO.getEmail())
                .orElseThrow(() -> new UserException("No user found with email: " + loginDTO.getEmail()));

        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            throw new UserException("Incorrect password. Please try again.");
        }

        // Generate JWT token
        String token = jwtToken.createToken(user.getEmail());
        System.out.println("token is = "+token);

        // Send login success message to RabbitMQ
        String customMessage = "LOGIN|" + user.getEmail() + "|" + user.getName();
        messageProducer.sendMessage(customMessage);

        logger.info("User logged in successfully: {}", user.getEmail());
        return token;
    }

    // Forgot password
    @Override
    @Transactional
    public void forgotPassword(String email) throws UserException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.error("Password reset failed for non-existing email: {}", email);
                    return new UserException("No user found with email: " + email);
                });

        // Generate password reset token
        String token = jwtToken.createToken(user.getEmail());
        user.setResetToken(token);
        userRepository.save(user);

        // Send reset token email
        logger.info("Password reset token generated for email: {}", email);
        emailSenderService.sendEmail(email, "Password Reset Token", "Use this token to reset your password: " + token);

        // Send message to message producer
        String customMessage = "FORGOT|" + user.getEmail() + "|" + user.getName();
        messageProducer.sendMessage(customMessage);
    }

    // Reset password
    @Override
    @Transactional
    public void resetPassword(PasswordResetDTO passwordResetDTO) throws UserException {
        User user = userRepository.findByEmail(passwordResetDTO.getEmail())
                .orElseThrow(() -> new UserException("No user found with email: " + passwordResetDTO.getEmail()));

        // Validate reset token
        if (user.getResetToken() == null || !user.getResetToken().equals(passwordResetDTO.getResetToken())) {
            throw new UserException("Invalid or expired reset token.");
        }

        // Securely update password
        user.setPassword(passwordEncoder.encode(passwordResetDTO.getNewPassword()));
        user.setResetToken(null); // Remove used token
        userRepository.save(user);

        logger.info("Password successfully reset for email: {}", passwordResetDTO.getEmail());
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}
