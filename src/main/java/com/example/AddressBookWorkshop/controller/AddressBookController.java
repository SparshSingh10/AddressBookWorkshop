package com.example.AddressBookWorkshop.controller;

import com.example.AddressBookWorkshop.Entity.User;
import com.example.AddressBookWorkshop.dto.AddressBookEntryDTO;
import com.example.AddressBookWorkshop.dto.ResponseDTO;
import com.example.AddressBookWorkshop.interfaces.IAddressBookService;
import com.example.AddressBookWorkshop.interfaces.IAuthenticationService;
import com.example.AddressBookWorkshop.util.JwtToken;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/addressbook")
public class AddressBookController {

    @Autowired
    private IAddressBookService addressBookService;

    @Autowired
    private IAuthenticationService authenticationService;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private JwtToken jwtToken;


    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ResponseDTO<List<AddressBookEntryDTO>>> getContactsByEmail(
            @RequestHeader("Authorization") String token) {

        log.info("Received request to fetch contacts by email");

        // Extract email from the JWT token
        String userEmail = jwtToken.getEmailFromToken(token.replace("Bearer ", ""));
        if (userEmail == null) {
            log.error("Invalid or expired token");
            return new ResponseEntity<>(new ResponseDTO<>("Invalid or expired token", false, null), HttpStatus.UNAUTHORIZED);
        }

        log.info("Fetching contacts for user: {}", userEmail);
        List<AddressBookEntryDTO> contacts = addressBookService.getContactsByEmail(userEmail);
        return ResponseEntity.ok(new ResponseDTO<>("Success", true, contacts));
    }


    // Add a new contact
    @PostMapping
    public ResponseEntity<ResponseDTO<AddressBookEntryDTO>> addContact(@Valid @RequestBody AddressBookEntryDTO addressBookEntryDTO) {
        log.info("Adding new contact: {}", addressBookEntryDTO);
        AddressBookEntryDTO savedContact = addressBookService.addContact(addressBookEntryDTO);
        return ResponseEntity.ok(new ResponseDTO<>("Success", true, savedContact));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseDTO<AddressBookEntryDTO>> updateContact(
            @PathVariable Long id,
            @Valid @RequestBody AddressBookEntryDTO addressBookEntryDTO,
            @RequestHeader("Authorization") String token) {

        log.info("Updating contact with id: {}", id);

        // Remove "Bearer " prefix and extract email from the JWT token
        String userEmail = jwtToken.getEmailFromToken(token.replace("Bearer ", ""));
        if (userEmail == null) {
            log.error("Invalid or expired token");
            return new ResponseEntity<>(new ResponseDTO<>("Invalid or expired token", false, null), HttpStatus.UNAUTHORIZED);
        }

        // Get the existing contact
        AddressBookEntryDTO existingContact = addressBookService.getContact(id);
        if (existingContact == null) {
            log.error("Contact with id {} not found", id);
            return new ResponseEntity<>(new ResponseDTO<>("Contact not found", false, null), HttpStatus.NOT_FOUND);
        }

        // Validate if the user is authorized to update this contact
        if (!existingContact.getEmail().equals(userEmail)) {
            log.error("Unauthorized access attempt for contact id: {} by user: {}", id, userEmail);
            return new ResponseEntity<>(new ResponseDTO<>("Unauthorized access", false, null), HttpStatus.FORBIDDEN);
        }

        // Perform update
        AddressBookEntryDTO updatedContact = addressBookService.updateContact(id, addressBookEntryDTO);
        log.info("Contact updated successfully: {}", updatedContact);
        return ResponseEntity.ok(new ResponseDTO<>("Success", true, updatedContact));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDTO<String>> deleteContact(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token) {

        log.info("Deleting contact with id: {}", id);

        // Extract email from JWT token
        String userEmail = jwtToken.getEmailFromToken(token.replace("Bearer ", ""));
        if (userEmail == null) {
            log.error("Invalid or expired token");
            return new ResponseEntity<>(new ResponseDTO<>("Invalid or expired token", false, null), HttpStatus.UNAUTHORIZED);
        }

        // Find user by email
        User user = authenticationService.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        log.info("User found: {}", user);

        // Get the contact by ID
        AddressBookEntryDTO contact = addressBookService.getContact(id);
        if (contact == null) {
            log.error("Contact with id {} not found", id);
            return new ResponseEntity<>(new ResponseDTO<>("Contact not found", false, null), HttpStatus.NOT_FOUND);
        }

        // Validate if the user is authorized to delete this contact
        if (!contact.getEmail().equals(userEmail) && !"ADMIN".equals(user.getRole())) {
            log.error("Unauthorized deletion attempt for contact id: {} by user: {}", id, userEmail);
            return new ResponseEntity<>(new ResponseDTO<>("Unauthorized access", false, null), HttpStatus.FORBIDDEN);
        }

        // Perform deletion
        addressBookService.deleteContact(id);
        log.info("Contact with id {} deleted successfully", id);
        return ResponseEntity.ok(new ResponseDTO<>("Contact deleted successfully", true, "Deleted"));
    }

    @RestController
    @RequestMapping("/test")
    public class TestController {
        private final JwtToken jwtToken;

        public TestController(JwtToken jwtToken) {
            this.jwtToken = jwtToken;
        }

        @GetMapping("/generateToken")
        public String generateToken() {
            log.info("Generating token for user: sparsh262002@gmail.com");
            return jwtToken.createToken("sparsh262002@gmail.com");
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ResponseDTO<AddressBookEntryDTO>> getContactById(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token) {

        log.info("Fetching contact with id: {}", id);

        // Extract email from the JWT token
        String userEmail = jwtToken.getEmailFromToken(token.replace("Bearer ", ""));
        if (userEmail == null) {
            log.error("Invalid or expired token");
            return new ResponseEntity<>(new ResponseDTO<>("Invalid or expired token", false, null), HttpStatus.UNAUTHORIZED);
        }

        // Get the contact
        AddressBookEntryDTO contact = addressBookService.getContact(id);
        if (contact == null) {
            log.error("Contact with id {} not found", id);
            return new ResponseEntity<>(new ResponseDTO<>("Contact not found", false, null), HttpStatus.NOT_FOUND);
        }

        // Check if the user is authorized to view this contact
        if (!contact.getEmail().equals(userEmail)) {
            log.error("Unauthorized access attempt for contact id: {} by user: {}", id, userEmail);
            return new ResponseEntity<>(new ResponseDTO<>("Unauthorized access", false, null), HttpStatus.FORBIDDEN);
        }

        log.info("Contact fetched successfully: {}", contact);
        return ResponseEntity.ok(new ResponseDTO<>("Success", true, contact));
    }
}
