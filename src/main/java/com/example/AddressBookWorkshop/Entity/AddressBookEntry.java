package com.example.AddressBookWorkshop.Entity;

import jakarta.persistence.*;
import lombok.*;
@Entity
@Table(name = "address_book")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressBookEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;
    private String phoneNumber;
    private String address;

    @ManyToOne(fetch = FetchType.LAZY)  // Lazy loading to optimize performance.
    @JoinColumn(name = "user_id", nullable = false)  // Links contacts to a specific user
    private User user;
}
