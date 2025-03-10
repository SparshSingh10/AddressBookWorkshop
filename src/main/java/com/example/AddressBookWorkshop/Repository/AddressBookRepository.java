package com.example.AddressBookWorkshop.Repository;

import com.example.AddressBookWorkshop.Entity.AddressBookEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
    public interface AddressBookRepository  extends JpaRepository<AddressBookEntry, Long> {
    // Custom query to find contacts by email
    List<AddressBookEntry> findByEmail(String email);
    }
