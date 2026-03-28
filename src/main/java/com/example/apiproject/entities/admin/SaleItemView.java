package com.example.apiproject.entities.admin;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;

import java.time.LocalDate;

@Entity
@Immutable
@Getter
@NoArgsConstructor
@Table(name = "view_of_sales")
public class SaleItemView {
    @Id
    private Long id;

    @Column(name = "full_name")
    private String clientName;

    @Column(name = "name")
    private String productName;

    private Integer quantity;

    @Column(name = "total_calculated")
    private Double totalCalculated;

    private String state;

    private LocalDate date;
}