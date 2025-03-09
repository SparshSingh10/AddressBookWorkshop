package com.example.AddressBookWorkshop.controller;

import com.example.AddressBookWorkshop.Entity.AddressBookEntry;
import com.example.AddressBookWorkshop.Repository.AddressBookRepository;
import com.example.AddressBookWorkshop.dto.AddressBookEntryDTO;
import com.example.AddressBookWorkshop.dto.ResponseDTO;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class AddressBookController {

    @Autowired
    private AddressBookRepository addressBookRepository;

    @Autowired
    private ModelMapper modelMapper;

    // Convert Entity to DTO
    private AddressBookEntryDTO convertToDTO(AddressBookEntry entry) {
        return modelMapper.map(entry, AddressBookEntryDTO.class);
    }

    // Convert DTO to Entity
    private AddressBookEntry convertToEntity(AddressBookEntryDTO dto) {
        return modelMapper.map(dto, AddressBookEntry.class);
    }

    @GetMapping("addressbook")
    public ResponseDTO<List<AddressBookEntryDTO>> getAllContacts() {
        List<AddressBookEntry> entities = addressBookRepository.findAll();
        List<AddressBookEntryDTO> dtoList = new ArrayList<>();

        for (AddressBookEntry entity : entities) {
            dtoList.add(convertToDTO(entity));
        }

        return new ResponseDTO<>("Success", true, dtoList);
    }

    @PostMapping("addressbook")
    public ResponseDTO<AddressBookEntryDTO> addContact(@Valid @RequestBody AddressBookEntryDTO addressBookEntryDTO) {
        AddressBookEntry obj = addressBookRepository.save(convertToEntity(addressBookEntryDTO));
        return new ResponseDTO<>("Success", true, convertToDTO(obj));
    }

    @GetMapping("addressbook/{id}")
    public ResponseDTO<AddressBookEntryDTO> getContact(@PathVariable Long id) {
        Optional<AddressBookEntry> obj = addressBookRepository.findById(id);
        if (obj.isPresent()) {
            return new ResponseDTO<>("Success", true, convertToDTO(obj.get()));
        }
        return new ResponseDTO<>("Failed", false, null);
    }

    @PutMapping("addressbook/{id}")
    public ResponseDTO<AddressBookEntryDTO> updateContact(@PathVariable Long id, @Valid @RequestBody AddressBookEntryDTO addressBookEntryDTO) {
        Optional<AddressBookEntry> optionalEntry = addressBookRepository.findById(id);

        if (optionalEntry.isPresent()) {
            AddressBookEntry obj = optionalEntry.get();
            obj.setName(addressBookEntryDTO.getName());
            obj.setEmail(addressBookEntryDTO.getEmail());
            obj.setPhoneNumber(addressBookEntryDTO.getPhoneNumber());
            obj.setAddress(addressBookEntryDTO.getAddress());
            addressBookRepository.save(obj);
            return new ResponseDTO<>("Success", true, convertToDTO(obj));
        }
        return new ResponseDTO<>("Failed", false, null);
    }

    @DeleteMapping("addressbook/{id}")
    public ResponseDTO<String> deleteContact(@PathVariable Long id) {
        addressBookRepository.deleteById(id);
        return new ResponseDTO<>("Success", true, "Deleted");
    }
}
