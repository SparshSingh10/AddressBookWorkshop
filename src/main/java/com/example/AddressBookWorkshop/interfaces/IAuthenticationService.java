package com.example.AddressBookWorkshop.interfaces;

import com.example.AddressBookWorkshop.Entity.User;
import com.example.AddressBookWorkshop.dto.LoginDTO;
import com.example.AddressBookWorkshop.dto.PasswordResetDTO;
import com.example.AddressBookWorkshop.dto.UserDTO;
import com.example.AddressBookWorkshop.dto.RegisterDTO;
import com.example.AddressBookWorkshop.exception.UserException;

import java.util.Optional;

public interface IAuthenticationService {
    // Register a new user
    UserDTO registerUser(RegisterDTO registerDTO) throws UserException;

    // Login the user and generate JWT token
    String loginUser(LoginDTO loginDTO) throws UserException;

    // Forgot password - Generate a token and send via email
    void forgotPassword(String email) throws UserException;

    // Reset password (Now using DTO)
    void resetPassword(PasswordResetDTO passwordResetDTO) throws UserException;

    Optional<User> findByEmail(String email);
}
