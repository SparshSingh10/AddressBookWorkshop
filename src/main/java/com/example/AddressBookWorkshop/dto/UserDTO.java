package com.example.AddressBookWorkshop.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {

    private Long id;
    private String email;
    private String name;  // Change from firstName and lastName to name
    private String role;
}
