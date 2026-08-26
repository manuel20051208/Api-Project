package com.apiproject.entities.admin;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;

@Data
@DynamicUpdate
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "services_cupon")
public class ServiceCupon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // En el DBML la columna venia como sh_cupon_code; se normalizo a service_cupon_code
    @Column(name = "service_cupon_code", length = 15, nullable = false)
    private String serviceCuponCode;

    @Column(name = "cupon_date_limit", nullable = false)
    private LocalDateTime cuponDateLimit;

    @Column(nullable = false)
    private Double discount;

    private Integer quantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private UserAdmin userAdmin;
}
