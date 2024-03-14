package com.cinar.textile.dto.request;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SignInRequest {
    @Email(message = "Please provide a valid email address")
    @NotBlank(message = "Please provide an email address")
    @Column(unique = true)
    private String email;
    @NotBlank(message = "Please provide a password")
    private String password;
}