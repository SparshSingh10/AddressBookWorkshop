package com.example.AddressBookWorkshop.service.ImplementI;

import com.example.AddressBookWorkshop.Entity.User;
import com.example.AddressBookWorkshop.Repository.UserRepository;
import com.example.AddressBookWorkshop.dto.LoginDTO;
import com.example.AddressBookWorkshop.dto.PasswordResetDTO;
import com.example.AddressBookWorkshop.dto.UserDTO;
import com.example.AddressBookWorkshop.dto.RegisterDTO;
import com.example.AddressBookWorkshop.exception.UserException;
import com.example.AddressBookWorkshop.service.EmailSenderService;
import com.example.AddressBookWorkshop.service.Iservice.IAuthenticationService;
import com.example.AddressBookWorkshop.util.JwtToken;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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

    // Register a new user
    @Override
    public UserDTO registerUser(RegisterDTO registerDTO) throws UserException {
        if (userRepository.findByEmail(registerDTO.getEmail()).isPresent()) {
            throw new UserException("Email is already registered!");
        }

        String encryptedPassword = passwordEncoder.encode(registerDTO.getPassword());

        User user = modelMapper.map(registerDTO, User.class);
        user.setPassword(encryptedPassword);
        user.setRole("USER");

        User savedUser = userRepository.save(user);
        emailSenderService.sendEmail(savedUser.getEmail(), "Registration Successful", "Welcome to Address Book!");

        return modelMapper.map(savedUser, UserDTO.class);
    }

    // Login user
    @Override
    public String loginUser(LoginDTO loginDTO) throws UserException {
        User user = userRepository.findByEmail(loginDTO.getEmail())
                .orElseThrow(() -> new UserException("User not found"));

        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            throw new UserException("Invalid credentials");
        }

        return jwtToken.createToken(user.getEmail());
    }

    // Forgot password
    @Override
    public void forgotPassword(String email) throws UserException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.error("Failed password reset attempt for non-existing email: {}", email);
                    return new UserException("User with email " + email + " not found!");
                });

        // Generate a token for password reset
        String token = jwtToken.createToken(user.getEmail());

        // Save the reset token in the user entity
        user.setResetToken(token);
        userRepository.save(user); // Save the updated user entity

        // Log and send the token via email
        logger.info("Password reset token generated and saved for email: {}", email);
        emailSenderService.sendEmail(email, "Password Reset Token", "Your password reset token is: " + token);
    }



    @Override
    public void resetPassword(PasswordResetDTO passwordResetDTO) throws UserException {
        User user = userRepository.findByEmail(passwordResetDTO.getEmail()).orElseThrow(() -> new UserException("User with email " + passwordResetDTO.getEmail() + " not found!"));

        // Verify reset token
        if (user.getResetToken() == null || !user.getResetToken().equals(passwordResetDTO.getResetToken())) {
            throw new UserException("Invalid or expired reset token!");
        }

        // Update password securely
        user.setPassword(passwordEncoder.encode(passwordResetDTO.getNewPassword()));

        // Remove the used reset token
        user.setResetToken(null);

        // Save the updated user details
        userRepository.save(user);

        // Log success
        logger.info("Password successfully reset for email: {}", passwordResetDTO.getEmail());
    }
    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }


}
