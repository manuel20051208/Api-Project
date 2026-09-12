package com.apiproject.entities.admin;

import com.apiproject.entities.general.Product;
import com.apiproject.enums.ColorTypes;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@DynamicUpdate
public class UserAdmin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "password")
    private String password;

    @Column(name = "phone")
    private Long phone;

    @Column(unique = true, nullable = false, name = "email")
    private String email;

    @Column(name = "business_name")
    private String businessName;

    @Column(name = "profile_photo")
    private String profilePhoto;

    @Column(name = "profile_photo_url")
    private String profilePhotoUrl;

    @OneToMany(mappedBy = "userAdmin", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Product> products = new ArrayList<>();

    @Column(name = "color_config")
    // Hibernate 6+ (que usa Spring Boot 3) le dice al driver que use el
    // tipo enum nativo de Postgres en vez de tratarlo como texto plano.
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    // @Enumerated(EnumType.STRING) le dice a Hibernate cómo
    // convertir tu enum de Java a un valor que la base de datos entienda
    @Enumerated(EnumType.STRING)
    private ColorTypes colorTypes;
}
