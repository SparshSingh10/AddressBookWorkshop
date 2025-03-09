package com.example.AddressBookWorkshop.Entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "address_book")
@Data
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
}
