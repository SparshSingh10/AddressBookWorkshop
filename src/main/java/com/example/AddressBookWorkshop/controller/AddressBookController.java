package com.example.AddressBookWorkshop.controller;

import com.example.AddressBookWorkshop.Entity.AddressBookEntry;
import com.example.AddressBookWorkshop.Repository.AddressBookRepository;
import com.example.AddressBookWorkshop.dto.AddressBookEntryDTO;
import com.example.AddressBookWorkshop.dto.ResponseDTO;
import com.example.AddressBookWorkshop.service.Iservice.IAddressBookService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/addressbook")
public class AddressBookController {

    @Autowired
    private IAddressBookService addressBookService;

    @Autowired
    private ModelMapper modelMapper;

    @GetMapping
    public ResponseDTO<List<AddressBookEntryDTO>> getAllContacts() {
        return new ResponseDTO<>("Success", true,addressBookService.getAllContacts());
    }

    @PostMapping
    public ResponseDTO<AddressBookEntryDTO> addContact(@Valid @RequestBody AddressBookEntryDTO addressBookEntryDTO) {
        return new ResponseDTO<>("Success", true, addressBookService.addContact(addressBookEntryDTO));
    }

    @PutMapping("/{id}")
    public ResponseDTO<AddressBookEntryDTO> updateContact(@PathVariable Long id, @Valid @RequestBody AddressBookEntryDTO addressBookEntryDTO) {
        return new ResponseDTO<>("Success", true, addressBookService.updateContact(id, addressBookEntryDTO));
    }

    @GetMapping("/{id}")
    public ResponseDTO<AddressBookEntryDTO> getContact(@PathVariable Long id) {
        return new ResponseDTO<>("Success", true, addressBookService.getContact(id));
    }
    @DeleteMapping("/{id}")
    public ResponseDTO<String> deleteContact(@PathVariable Long id) {
        return new ResponseDTO<>("Success", true, addressBookService.deleteContact(id));
    }
}
