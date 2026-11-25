package com.cleanride.cleanride.security;

public record AuthPrincipal(Integer id, String role) {

    public boolean isAdmin() {
        return "ADMIN".equals(role);
    }

    public boolean isCustomer() {
        return "CUSTOMER".equals(role);
    }
}
