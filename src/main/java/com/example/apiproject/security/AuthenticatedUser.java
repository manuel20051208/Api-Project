package com.example.apiproject.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Locale;

public record AuthenticatedUser(
        Long id,
        String username,
        String accountType,
        Collection<? extends GrantedAuthority> authorities
) implements UserDetails {

    public static AuthenticatedUser fromToken(Long id, String username, String accountType) {
        String normalizedAccountType = accountType.toUpperCase(Locale.ROOT);
        return new AuthenticatedUser(
                id,
                username,
                normalizedAccountType,
                List.of(new SimpleGrantedAuthority("ROLE_" + normalizedAccountType))
        );
    }

    public boolean isAdmin() {
        return "ADMIN".equals(accountType);
    }

    public boolean isClient() {
        return "CLIENT".equals(accountType);
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
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