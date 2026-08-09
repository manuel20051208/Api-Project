package com.apiproject.entities.admin;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import net.jcip.annotations.Immutable;

import java.time.LocalDateTime;

@Entity
@Immutable
@Table(name = "report_view_dashboard")
public class ReportDashboard {

    @Id
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "client_name")
    private String clientName;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "total_calculated")
    private Double totalCalculated;

    @Column(name = "state")
    private String state;

    @Column(name = "date")
    private LocalDateTime date;

    @Column(name = "current_amount")
    private Double currentAmount;
}