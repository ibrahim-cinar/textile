package com.cinar.textile.dto.request;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SignUpRequest {

    @NotBlank(message = "Please provide a first name")
    private String firstName;
    @NotBlank(message = "Please provide a last name")
    private String lastName;
    @Email(message = "Please provide a valid email address")
    @NotBlank(message = "Please provide an email address")
    @Column(unique = true)
    private String email;
    @NotBlank(message = "Please provide a password")
    private String password;
    @NotBlank(message = "Please provide a phone number")
    private String phoneNumber;
}