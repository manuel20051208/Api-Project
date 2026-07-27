package com.example.apiproject.repositories.projection;

public interface ClientLoginProjection {
    Long getId();
    String getUserName();
    String getPassword();
    String getFullName();
    String getEmail();
    Long getPhone();
}