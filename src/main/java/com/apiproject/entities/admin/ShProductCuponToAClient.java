package com.apiproject.entities.admin;

import com.apiproject.entities.client.UserClient;
import com.apiproject.entities.general.SecondHandProduct;
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
@Table(name = "sh_product_cupo_to_a_client")
public class ShProductCuponToAClient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    @JsonIgnore
    private UserClient client;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sh_cupons_id", nullable = false)
    @JsonIgnore
    private SecondHandCupon cupon;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sh_product_id", nullable = false)
    @JsonIgnore
    private SecondHandProduct product;
}
