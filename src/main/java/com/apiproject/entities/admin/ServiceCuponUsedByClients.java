package com.apiproject.entities.admin;

import com.apiproject.entities.client.UserClient;
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
@Table(name = "services_cupons_used_by_clients")
public class ServiceCuponUsedByClients {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_user", nullable = false)
    @JsonIgnore
    private UserClient clientUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_cupon_id", nullable = false)
    @JsonIgnore
    private ServiceCupon serviceCupon;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    /** Contador acumulado de usos del cliente con ese cupon de servicio (1..N). */
    @Column(name = "usage_count")
    private Integer usageCount;
}