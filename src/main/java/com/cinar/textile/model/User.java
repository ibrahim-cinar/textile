package com.cinar.textile.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor

@Table(name = "users")
public class User extends BaseEntity implements UserDetails {
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
    @OneToMany(mappedBy = "user",fetch = FetchType.EAGER)
    private List<Token> tokens;

    @ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
    @JoinTable(name = "authorities", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "authority", nullable = false)
    @Enumerated(EnumType.STRING)
    private List<Role> authorities;

    public User(String firstName, String lastName, String email, String password, String phoneNumber, List<Role> authorities) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.authorities = authorities;
    }



    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
