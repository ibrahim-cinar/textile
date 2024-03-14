package com.cinar.textile.model;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
    ROLE_SUPER_ADMIN("SUPER_ADMIN"),
    ROLE_ADMIN("ADMIN");


    private String value;

    Role(String value) {
        this.value = value;
    }
    public String getValue() {
        return this.value;
    }

    @Override
    public String getAuthority() {
        return name();
    }
}
