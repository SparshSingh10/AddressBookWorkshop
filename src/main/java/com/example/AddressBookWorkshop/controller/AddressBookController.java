package com.example.AddressBookWorkshop.controller;

import com.example.AddressBookWorkshop.Entity.User;
import com.example.AddressBookWorkshop.dto.AddressBookEntryDTO;
import com.example.AddressBookWorkshop.dto.ResponseDTO;
import com.example.AddressBookWorkshop.dto.UserEmailDTO;
import com.example.AddressBookWorkshop.service.Iservice.IAddressBookService;
import com.example.AddressBookWorkshop.service.Iservice.IAuthenticationService;
import com.example.AddressBookWorkshop.util.JwtToken;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/addressbook")
public class AddressBookController {

    @Autowired
    private IAddressBookService addressBookService;

    @Autowired
    private IAuthenticationService authenticationService;

    @Autowired
    private ModelMapper modelMapper;

    @GetMapping  // ✅ Remove the extra "/" here
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ResponseDTO<List<AddressBookEntryDTO>>> getContactsByEmail(@RequestParam String email) {
        List<AddressBookEntryDTO> contacts = addressBookService.getContactsByEmail(email);
        return ResponseEntity.ok(new ResponseDTO<>("Success", true, contacts));
    }


    // Add a new contact
    @PostMapping
    public ResponseEntity<ResponseDTO<AddressBookEntryDTO>> addContact(@Valid @RequestBody AddressBookEntryDTO addressBookEntryDTO) {
        AddressBookEntryDTO savedContact = addressBookService.addContact(addressBookEntryDTO);
        return ResponseEntity.ok(new ResponseDTO<>("Success", true, savedContact));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseDTO<AddressBookEntryDTO>> updateContact(@PathVariable Long id,
                                                                          @Valid @RequestBody AddressBookEntryDTO addressBookEntryDTO) {
        AddressBookEntryDTO existingContact = addressBookService.getContact(id);

        if (existingContact == null) {
            return new ResponseEntity<>(new ResponseDTO<>("Contact not found", false, null), HttpStatus.NOT_FOUND);
        }

        if (!existingContact.getEmail().equals(addressBookEntryDTO.getEmail())) {
            return new ResponseEntity<>(new ResponseDTO<>("Unauthorized access", false, null), HttpStatus.FORBIDDEN);
        }

        AddressBookEntryDTO updatedContact = addressBookService.updateContact(id, addressBookEntryDTO);
        return ResponseEntity.ok(new ResponseDTO<>("Success", true, updatedContact));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDTO<String>> deleteContact(@PathVariable Long id, @RequestBody UserEmailDTO userEmailDTO) {
        String email = userEmailDTO.getEmail();

        User user = authenticationService.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        AddressBookEntryDTO contact = addressBookService.getContact(id);

        if (contact == null) {
            return new ResponseEntity<>(new ResponseDTO<>("Contact not found", false, null), HttpStatus.NOT_FOUND);
        }

        if (!contact.getEmail().equals(email) && !"ADMIN".equals(user.getRole())) {
            return new ResponseEntity<>(new ResponseDTO<>("Unauthorized access", false, null), HttpStatus.FORBIDDEN);
        }

        addressBookService.deleteContact(id);
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
            return jwtToken.createToken("sparsh262002@gmail.com");
        }
    }

}
