package com.example.AddressBookWorkshop.Entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users") // Changed table name to avoid conflict with SQL reserved keywords
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false) // Ensure email is unique and not null
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String role;

    @Column(unique = true) // Ensure uniqueness of reset token
    private String resetToken; // Token for password reset
}
