package com.example.AddressBookWorkshop.service;

import com.example.AddressBookWorkshop.Entity.AddressBookEntry;
import com.example.AddressBookWorkshop.Repository.AddressBookRepository;
import com.example.AddressBookWorkshop.dto.AddressBookEntryDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddressBookServiceTest {

    @Mock
    private AddressBookRepository addressBookRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private AddressBookService addressBookService;

    private AddressBookEntry sampleContact;
    private AddressBookEntryDTO sampleDTO;

    @BeforeEach
    void setUp() {
        sampleContact = new AddressBookEntry();
        sampleContact.setId(1L);
        sampleContact.setName("John Doe");
        sampleContact.setEmail("john@example.com");
        sampleContact.setPhoneNumber("1234567890");
        sampleContact.setAddress("123 Main St");

        sampleDTO = new AddressBookEntryDTO();
        sampleDTO.setName("John Doe");
        sampleDTO.setEmail("john@example.com");
        sampleDTO.setPhoneNumber("1234567890");
        sampleDTO.setAddress("123 Main St");
    }

    @Test
    void testGetAllContacts() {
        when(addressBookRepository.findAll()).thenReturn(Arrays.asList(sampleContact));
        when(modelMapper.map(sampleContact, AddressBookEntryDTO.class)).thenReturn(sampleDTO);

        List<AddressBookEntryDTO> result = addressBookService.getAllContacts();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("John Doe", result.get(0).getName());
    }

    @Test
    void testGetContactById() {
        when(addressBookRepository.findById(1L)).thenReturn(Optional.of(sampleContact));
        when(modelMapper.map(sampleContact, AddressBookEntryDTO.class)).thenReturn(sampleDTO);

        AddressBookEntryDTO result = addressBookService.getContact(1L);

        assertNotNull(result);
        assertEquals("John Doe", result.getName());
    }

    @Test
    void testAddContact() {
        when(addressBookRepository.save(any(AddressBookEntry.class))).thenReturn(sampleContact);
        when(modelMapper.map(sampleDTO, AddressBookEntry.class)).thenReturn(sampleContact);
        when(modelMapper.map(sampleContact, AddressBookEntryDTO.class)).thenReturn(sampleDTO);

        AddressBookEntryDTO result = addressBookService.addContact(sampleDTO);

        assertNotNull(result);
        assertEquals("John Doe", result.getName());
    }

    @Test
    void testUpdateContact() {
        when(addressBookRepository.findById(1L)).thenReturn(Optional.of(sampleContact));
        when(addressBookRepository.save(any(AddressBookEntry.class))).thenReturn(sampleContact);

        // Using lenient() to avoid unnecessary stubbing error
        lenient().when(modelMapper.map(sampleDTO, AddressBookEntry.class)).thenReturn(sampleContact);
        lenient().when(modelMapper.map(sampleContact, AddressBookEntryDTO.class)).thenReturn(sampleDTO);

        AddressBookEntryDTO result = addressBookService.updateContact(1L, sampleDTO);

        assertNotNull(result);
        assertEquals("John Doe", result.getName());
    }

    @Test
    void testDeleteContact() {
        when(addressBookRepository.existsById(1L)).thenReturn(true);
        doNothing().when(addressBookRepository).deleteById(1L);

        String result = addressBookService.deleteContact(1L);

        assertEquals("Contact deleted successfully", result);
    }
}
