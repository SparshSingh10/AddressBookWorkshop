package com.example.AddressBookWorkshop.controller;

import com.example.AddressBookWorkshop.Entity.AddressBookEntry;
import com.example.AddressBookWorkshop.Entity.User;
import com.example.AddressBookWorkshop.dto.AddressBookEntryDTO;
import com.example.AddressBookWorkshop.dto.ResponseDTO;
import com.example.AddressBookWorkshop.dto.UserEmailDTO;
import com.example.AddressBookWorkshop.service.Iservice.IAddressBookService;
import com.example.AddressBookWorkshop.service.Iservice.IAuthenticationService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    // Fetch contacts by email provided in the request
    @GetMapping("/api/addressbook")
    public ResponseEntity<ResponseDTO<List<AddressBookEntryDTO>>> getContactsByEmail(@RequestParam String email) {
        List<AddressBookEntryDTO> contacts = addressBookService.getContactsByEmail(email);
        return ResponseEntity.ok(new ResponseDTO<>("Success", true, contacts));
    }

//    // Only Admin can fetch all contacts or return only contacts that match the email from the request
//    @GetMapping
//    public ResponseEntity<ResponseDTO<List<AddressBookEntryDTO>>> getAllContacts(@RequestBody String email) {
//        // Filter contacts based on email
//        List<AddressBookEntryDTO> contacts = addressBookService.getContactsByEmail(email);
//        return ResponseEntity.ok(new ResponseDTO<>("Success", true, contacts));
//    }

    // Add a new contact
    @PostMapping
    public ResponseEntity<ResponseDTO<AddressBookEntryDTO>> addContact(@Valid @RequestBody AddressBookEntryDTO addressBookEntryDTO) {
        // Ensure the user can only add their own contacts by verifying the email in DTO
        AddressBookEntryDTO savedContact = addressBookService.addContact(addressBookEntryDTO);
        return ResponseEntity.ok(new ResponseDTO<>("Success", true, savedContact));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseDTO<AddressBookEntryDTO>> updateContact(@PathVariable Long id,
                                                                          @Valid @RequestBody AddressBookEntryDTO addressBookEntryDTO) {
        AddressBookEntryDTO existingContact = addressBookService.getContact(id);

        // Extract email from the request body
        String email = addressBookEntryDTO.getEmail();

        // Only allow updating the contact if the email matches
        if (!existingContact.getEmail().equals(email)) {
            return new ResponseEntity<>(new ResponseDTO<>("Unauthorized access", false, null), HttpStatus.FORBIDDEN);
        }

        AddressBookEntryDTO updatedContact = addressBookService.updateContact(id, addressBookEntryDTO);
        return ResponseEntity.ok(new ResponseDTO<>("Success", true, updatedContact));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDTO<String>> deleteContact(@PathVariable Long id, @RequestBody UserEmailDTO userEmailDTO) {
        String email = userEmailDTO.getEmail();

        // Check if the user exists by the provided email
        User user = authenticationService.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Fetch the contact to be deleted
        AddressBookEntryDTO contact = addressBookService.getContact(id);

        // Check if the contact exists
        if (contact == null) {
            return new ResponseEntity<>(new ResponseDTO<>("Contact not found", false, null), HttpStatus.NOT_FOUND);
        }

        // Allow deletion only if the email matches or the user is an admin
        if (!contact.getEmail().equals(email) && !user.getRole().equals("ADMIN")) {
            return new ResponseEntity<>(new ResponseDTO<>("Unauthorized access", false, null), HttpStatus.FORBIDDEN);
        }

        // Proceed to delete the contact
        addressBookService.deleteContact(id);
        return ResponseEntity.ok(new ResponseDTO<>("Contact deleted successfully", true, "Deleted"));
    }



}
