package com.example.apiproject.entities.admin;

import com.google.errorprone.annotations.Immutable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Immutable
@Table(name = "view_of_dashboard")
public class Dashboard {

    @Id
    @Column(name = "month_number")
    private Integer monthNumber;

    @Column(name = "sales_year")
    private Integer salesYear;

    @Column(name = "month_name")
    private String monthName;

    @Column(name = "monthly_total")
    private Double monthlyTotal;

    @Column(name = "number_of_products")
    private Long numberOfProducts;

    @Column(name = "count_clients")
    private Long countClients;
}
