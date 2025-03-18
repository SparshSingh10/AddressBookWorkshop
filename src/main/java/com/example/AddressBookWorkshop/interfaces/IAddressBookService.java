package com.example.AddressBookWorkshop.interfaces;

import com.example.AddressBookWorkshop.Entity.User;
import com.example.AddressBookWorkshop.dto.AddressBookEntryDTO;

import java.util.List;
import java.util.Optional;

public interface IAddressBookService {
    Optional<User> findByEmail(String email);  // Method already in interface

    List<AddressBookEntryDTO> getAllContacts();

    AddressBookEntryDTO addContact(AddressBookEntryDTO addressBookEntryDTO);

    AddressBookEntryDTO getContact(Long id);

    AddressBookEntryDTO updateContact(Long id, AddressBookEntryDTO addressBookEntryDTO);

    String deleteContact(Long id);

    // Add the method to fetch contacts by email
    List<AddressBookEntryDTO> getContactsByEmail(String email);
}
