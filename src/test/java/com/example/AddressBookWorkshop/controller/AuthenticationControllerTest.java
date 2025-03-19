package com.example.AddressBookWorkshop.controller;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.example.AddressBookWorkshop.controller.AuthenticationController.AuthenticationController;
import com.example.AddressBookWorkshop.dto.*;
import com.example.AddressBookWorkshop.exception.UserException;
import com.example.AddressBookWorkshop.interfaces.IAuthenticationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

class AuthenticationControllerTest {

    @Mock
    private IAuthenticationService authenticationService;

    @InjectMocks
    private AuthenticationController authenticationController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegisterUser() throws UserException {
        RegisterDTO registerDTO = new RegisterDTO();
        UserDTO userDTO = new UserDTO();
        when(authenticationService.registerUser(registerDTO)).thenReturn(userDTO);

        ResponseEntity<UserDTO> response = authenticationController.registerUser(registerDTO);
        assertNotNull(response.getBody());
    }

    @Test
    void testLoginUser_Success() throws UserException {
        LoginDTO loginDTO = new LoginDTO();
        String token = "test-token";
        when(authenticationService.loginUser(loginDTO)).thenReturn(token);

        ResponseEntity<ResponseDTO> response = authenticationController.loginUser(loginDTO);
        assertTrue(response.getBody().isSuccess());
        assertEquals("Login successfully!!", response.getBody().getMessage());
    }

    @Test
    void testLoginUser_Failure() throws UserException {
        LoginDTO loginDTO = new LoginDTO();
        when(authenticationService.loginUser(loginDTO)).thenThrow(new UserException("Invalid credentials"));

        ResponseEntity<ResponseDTO> response = authenticationController.loginUser(loginDTO);
        assertFalse(response.getBody().isSuccess());
        assertEquals("Invalid credentials", response.getBody().getMessage());
    }

    @Test
    void testForgotPassword() throws UserException {
        String email = "test@example.com";
        doNothing().when(authenticationService).forgotPassword(email);

        ResponseEntity<ResponseDTO> response = authenticationController.forgotPassword(email);
        assertTrue(response.getBody().isSuccess());
    }

    @Test
    void testResetPassword() throws UserException {
        PasswordResetDTO passwordResetDTO = new PasswordResetDTO();
        passwordResetDTO.setEmail("test@example.com");
        doNothing().when(authenticationService).resetPassword(passwordResetDTO);

        ResponseEntity<ResponseDTO> response = authenticationController.resetPassword(passwordResetDTO);
        assertTrue(response.getBody().isSuccess());
    }
}