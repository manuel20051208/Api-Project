package com.example.apiproject.entities.admin;

import com.example.apiproject.entities.general.Product;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "users")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserAdmin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, name = "username")
    private String userName;

    @Column(name = "full_name", length = 60)
    private String fullName;

    @Column(nullable = false, name = "password")
    private String password;

    @Column(name = "phone")
    private Long phone;

    @Column(name = "email", length = 60)
    private String email;

    @Column(name = "business_name", length = 100)
    private String businessName;

    @OneToMany(mappedBy = "userAdmin", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Product> products = new ArrayList<>();
}
