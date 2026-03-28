package com.example.apiproject.entities.admin;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;

import java.math.BigDecimal;
import java.math.BigInteger;

@Entity
@Table(name = "clients_summary")
@Immutable
@AllArgsConstructor
@NoArgsConstructor
public class ClientsSummaryView {
    @Id
    private Long id;

    @Column(name = "full_name")
    private String clientName;

    @Column(name = "email")
    private String clientEmail;

    @Column(name = "total_quantity")
    private BigInteger totalQuantity;

    @Column(name = "total_spent")
    private BigDecimal totalSpent;
}