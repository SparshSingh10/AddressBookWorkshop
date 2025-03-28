package com.example.AddressBookWorkshop.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResponseDTO<T> {
    private String message;
    private boolean success;
    private T data;  // This can be either the token (on success) or null (on failure)
}
