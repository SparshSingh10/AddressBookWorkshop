package com.example.AddressBookWorkshop.service.ImplementI;

import com.example.AddressBookWorkshop.Entity.AddressBookEntry;
import com.example.AddressBookWorkshop.Repository.AddressBookRepository;
import com.example.AddressBookWorkshop.dto.AddressBookEntryDTO;
import com.example.AddressBookWorkshop.service.Iservice.IAddressBookService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AddressBookService implements IAddressBookService {

    @Autowired
    private AddressBookRepository addressBookRepository;
    @Autowired
    private ModelMapper modelMapper;

    @Override
    public List<AddressBookEntryDTO> getAllContacts() {
        List<AddressBookEntry> modeLis = addressBookRepository.findAll();
        List<AddressBookEntryDTO> dtoList = new ArrayList<>();

        for(AddressBookEntry modobj : modeLis){
            dtoList.add(modelMapper.map(modobj, AddressBookEntryDTO.class));
        }
        return dtoList;
    }

    @Override
    public AddressBookEntryDTO addContact(AddressBookEntryDTO addressBookEntryDTO) {
       AddressBookEntry obj=modelMapper.map(addressBookEntryDTO, AddressBookEntry.class);
       addressBookRepository.save(obj);
       return modelMapper.map(obj, AddressBookEntryDTO.class);
    }

    @Override
    public AddressBookEntryDTO getContact(Long id) {

        AddressBookEntry obj=addressBookRepository.findById(id).orElse(null);
        return (obj != null) ? modelMapper.map(obj, AddressBookEntryDTO.class) : null;
    }

    @Override
    public AddressBookEntryDTO updateContact(Long id, AddressBookEntryDTO addressBookEntryDTO) {
        AddressBookEntry obj=addressBookRepository.findById(id).orElse(null);
        if (obj != null) {
            obj.setName(addressBookEntryDTO.getName());
            obj.setEmail(addressBookEntryDTO.getEmail());
            obj.setPhoneNumber(addressBookEntryDTO.getPhoneNumber());
            obj.setAddress(addressBookEntryDTO.getAddress());
            addressBookRepository.save(obj);
            return modelMapper.map(obj, AddressBookEntryDTO.class);
        }
        return null;
    }

    @Override
    public String deleteContact(Long id) {
        if (addressBookRepository.existsById(id)) {
            addressBookRepository.deleteById(id);
            return "Contact deleted successfully";
        }
        return "Contact not found";
    }
}
