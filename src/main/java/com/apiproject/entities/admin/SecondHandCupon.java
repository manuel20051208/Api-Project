package com.apiproject.entities.admin;

import java.math.BigDecimal;

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
@Table(name = "secondhand_cupons")
public class SecondHandCupon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sh_cupon_code", length = 15, nullable = false)
    private String shCuponCode;

    @Column(name = "cupon_date_limit", nullable = false)
    private LocalDateTime cuponDateLimit;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal discount;

    private Integer quantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private UserAdmin userAdmin;
}
