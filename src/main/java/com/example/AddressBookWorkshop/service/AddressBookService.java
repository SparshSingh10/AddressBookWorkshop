package com.example.AddressBookWorkshop.service;

import com.example.AddressBookWorkshop.Entity.AddressBookEntry;
import com.example.AddressBookWorkshop.Entity.User;
import com.example.AddressBookWorkshop.Repository.AddressBookRepository;
import com.example.AddressBookWorkshop.Repository.UserRepository;
import com.example.AddressBookWorkshop.dto.AddressBookEntryDTO;
import com.example.AddressBookWorkshop.interfaces.IAddressBookService;
import lombok.extern.slf4j.Slf4j;  // Lombok's Slf4j annotation
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j  // Lombok will automatically create the logger instance
@Service
public class AddressBookService implements IAddressBookService {

    @Autowired
    private AddressBookRepository addressBookRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public Optional<User> findByEmail(String email) {
        log.info("Searching for user with email: {}", email);
        return userRepository.findByEmail(email);
    }

    @Override
    @Cacheable(value = "allAddressBookCache", key = "'allContacts'")
    public List<AddressBookEntryDTO> getAllContacts() {
        log.info("Fetching all contacts");
        List<AddressBookEntry> modeLis = addressBookRepository.findAll();
        List<AddressBookEntryDTO> dtoList = new ArrayList<>();

        for (AddressBookEntry modobj : modeLis) {
            dtoList.add(modelMapper.map(modobj, AddressBookEntryDTO.class));
        }
        return dtoList;
    }




    @Override
    @Cacheable(value = "addressBookCache", key = "#id")
    public AddressBookEntryDTO getContact(Long id) {
        log.info("Fetching contact with ID: {}", id);
        AddressBookEntry obj = addressBookRepository.findById(id).orElse(null);
        if (obj == null) {
            log.warn("Contact with ID {} not found", id);
        }
        return (obj != null) ? modelMapper.map(obj, AddressBookEntryDTO.class) : null;
    }

    @Override
    public AddressBookEntryDTO addContact(AddressBookEntryDTO addressBookEntryDTO) {
        log.info("Adding a new contact: {}", addressBookEntryDTO);
        AddressBookEntry obj = modelMapper.map(addressBookEntryDTO, AddressBookEntry.class);
        addressBookRepository.save(obj);
        return modelMapper.map(obj, AddressBookEntryDTO.class);
    }

    @Override
    public AddressBookEntryDTO updateContact(Long id, AddressBookEntryDTO addressBookEntryDTO) {
        log.info("Updating contact with ID: {}", id);
        AddressBookEntry obj = addressBookRepository.findById(id).orElse(null);
        if (obj != null) {
            obj.setName(addressBookEntryDTO.getName());
            obj.setEmail(addressBookEntryDTO.getEmail());
            obj.setPhoneNumber(addressBookEntryDTO.getPhoneNumber());
            obj.setAddress(addressBookEntryDTO.getAddress());
            addressBookRepository.save(obj);
            log.info("Contact updated successfully: {}", obj);
            return modelMapper.map(obj, AddressBookEntryDTO.class);
        }
        log.warn("Contact with ID {} not found for update", id);
        return null;
    }

    @Override
    public String deleteContact(Long id) {
        log.info("Deleting contact with ID: {}", id);
        if (!addressBookRepository.existsById(id)) {
            log.warn("Contact with ID {} not found", id);
            return "Contact not found";
        }
        addressBookRepository.deleteById(id);
        log.info("Contact with ID {} deleted successfully", id);
        return "Contact deleted successfully";
    }

    @Override
    public List<AddressBookEntryDTO> getContactsByEmail(String email) {
        log.info("Fetching contacts with email: {}", email);
        List<AddressBookEntry> allContacts = addressBookRepository.findAll();
        List<AddressBookEntryDTO> result = new ArrayList<>();
        for (AddressBookEntry contact : allContacts) {
            if (contact.getEmail().equalsIgnoreCase(email)) {
                result.add(modelMapper.map(contact, AddressBookEntryDTO.class));
            }
        }
        return result;
    }
}
