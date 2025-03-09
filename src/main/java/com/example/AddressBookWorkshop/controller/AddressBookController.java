package com.example.AddressBookWorkshop.controller;

import com.example.AddressBookWorkshop.Entity.AddressBookEntry;
import com.example.AddressBookWorkshop.Repository.AddressBookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/contacts") // Base URL
public class AddressBookController {

    @Autowired
    private AddressBookRepository addressBookRepository;

    // Endpoint to save a contact
    @PostMapping("/add")
    public ResponseEntity<AddressBookEntry> addContact(@RequestBody AddressBookEntry entry) {
        AddressBookEntry savedEntry = addressBookRepository.save(entry);
        return ResponseEntity.ok(savedEntry);
    }

    // Endpoint to get all contacts
    @GetMapping("/all")
    public ResponseEntity<List<AddressBookEntry>> getContacts() {
        List<AddressBookEntry> savedEntries = addressBookRepository.findAll();
        return ResponseEntity.ok(savedEntries);
    }
}
