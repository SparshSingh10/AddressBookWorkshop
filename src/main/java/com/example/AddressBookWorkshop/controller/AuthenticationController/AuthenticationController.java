package com.example.AddressBookWorkshop.controller.AuthenticationController;

import com.example.AddressBookWorkshop.dto.*;
import com.example.AddressBookWorkshop.exception.UserException;
import com.example.AddressBookWorkshop.service.Iservice.IAuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    @Autowired
    private IAuthenticationService authenticationService;

    // Endpoint for user registration
    @PostMapping("/register")
    public ResponseEntity<UserDTO> registerUser(@RequestBody RegisterDTO registerDTO) throws UserException {
        UserDTO userDTO = authenticationService.registerUser(registerDTO);
        return ResponseEntity.ok(userDTO);
    }

    // Endpoint for user login
    @PostMapping("/login")
    public ResponseEntity<ResponseDTO> loginUser(@RequestBody LoginDTO loginDTO) {
        try {
            // Attempt to login and get the token
            String token = authenticationService.loginUser(loginDTO);
            // Create a success response with the token
            ResponseDTO responseUserDTO = new ResponseDTO("Login successfully!!", true, token);
            return new ResponseEntity<>(responseUserDTO, HttpStatus.OK);
        } catch (UserException e) {
            // Create failure response with error message and a false success flag
            ResponseDTO responseUserDTO = new ResponseDTO(e.getMessage(), false, null);
            return new ResponseEntity<>(responseUserDTO, HttpStatus.BAD_REQUEST);
        }
    }

    // Forgot Password - Generate token and send via email
    @PostMapping("/forgotPassword/{email}")
    public ResponseEntity<ResponseDTO> forgotPassword(@PathVariable String email) throws UserException {
        authenticationService.forgotPassword(email);
        return ResponseEntity.ok(new ResponseDTO("Reset token has been sent to your email!", true, email));
    }
    @PutMapping("/resetPassword")
    public ResponseEntity<ResponseDTO> resetPassword(@RequestBody PasswordResetDTO passwordResetDTO) throws UserException {
        authenticationService.resetPassword(passwordResetDTO);
        return ResponseEntity.ok(new ResponseDTO("Password reset successfully!", true, passwordResetDTO.getEmail()));
    }


}
