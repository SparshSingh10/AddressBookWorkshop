package com.example.AddressBookWorkshop.Repository;

import com.example.AddressBookWorkshop.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);  // Correct method to find user by email
}
