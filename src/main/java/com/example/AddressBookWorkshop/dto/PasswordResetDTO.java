package com.example.AddressBookWorkshop.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordResetDTO {
    private String email;
    private String newPassword;
    private String resetToken;

}