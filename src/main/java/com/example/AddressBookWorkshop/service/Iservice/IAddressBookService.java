package com.example.AddressBookWorkshop.service.Iservice;

import com.example.AddressBookWorkshop.dto.AddressBookEntryDTO;

import java.util.List;

public interface IAddressBookService {
    List<AddressBookEntryDTO> getAllContacts();

    AddressBookEntryDTO addContact(AddressBookEntryDTO addressBookEntryDTO);

    AddressBookEntryDTO getContact(Long id);

    AddressBookEntryDTO updateContact(Long id, AddressBookEntryDTO addressBookEntryDTO);

    String deleteContact(Long id);
}
