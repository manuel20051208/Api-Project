package com.apiproject.entities.admin;

import com.apiproject.entities.client.UserClient;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

@Data
@DynamicUpdate
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "service_cupon_to_a_client")
public class ServiceCuponToAClient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    @JsonIgnore
    private UserClient client;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_cupon_id", nullable = false)
    @JsonIgnore
    private ServiceCupon serviceCupon;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id", nullable = false)
    @JsonIgnore
    private ServiceOffered service;
}
