package com.example.AddressBookWorkshop.Repository;

import com.example.AddressBookWorkshop.Entity.AddressBookEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
    public interface AddressBookRepository  extends JpaRepository<AddressBookEntry, Long> {

    }
