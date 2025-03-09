package com.example.AddressBookWorkshop.controller;

import com.example.AddressBookWorkshop.Entity.AddressBookEntry;
import com.example.AddressBookWorkshop.Repository.AddressBookRepository;
import com.example.AddressBookWorkshop.dto.ResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class AddressBookController {

    @Autowired
    private AddressBookRepository addressBookRepository;

    @GetMapping("addressbook")
    public ResponseDTO<List<AddressBookEntry>> getAllContacts() {
        List<AddressBookEntry> lis=addressBookRepository.findAll();
        return new ResponseDTO<>("Success",true,lis);
    }

    @PostMapping("addressbook")
    public ResponseDTO<AddressBookEntry> addContact(@RequestBody AddressBookEntry addressBookEntry) {
        AddressBookEntry obj=addressBookRepository.save(addressBookEntry);
        return new ResponseDTO<>("Success",true,obj);
    }

    @GetMapping("addressbook/{id}")
    public ResponseDTO<AddressBookEntry> getContact(@PathVariable Long id){
        AddressBookEntry obj=addressBookRepository.findById(id).orElse(null);
        return new ResponseDTO<>(obj!=null?"Success":"Failed",true,obj);
    }

    @PutMapping("addressbook/{id}")
    public ResponseDTO<AddressBookEntry> updateContact(@PathVariable Long id,@RequestBody AddressBookEntry addressBookEntry){
        AddressBookEntry obj=addressBookRepository.findById(id).orElse(null);
        if(obj!=null){
            obj.setName(addressBookEntry.getName());
            obj.setEmail(addressBookEntry.getEmail());
            obj.setPhoneNumber(addressBookEntry.getPhoneNumber());
            obj.setAddress(addressBookEntry.getAddress());
            addressBookRepository.save(obj);
            return new ResponseDTO<>("Success",true,obj);
        }
        else{
            return new ResponseDTO<>("Failed",false,null);
        }
    }

    @DeleteMapping("addressbook/{id}")
    public ResponseDTO<String> deleteContact(@PathVariable Long id){
        addressBookRepository.deleteById(id);
        return new ResponseDTO<>("Success",true,"Deleted");
    }
}
