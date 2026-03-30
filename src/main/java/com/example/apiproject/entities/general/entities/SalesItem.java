package com.example.apiproject.entities.general.entities;

import com.example.apiproject.entities.client.UserClient;
import com.example.apiproject.enums.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "sale_items")
public class SalesItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name =  "sale_id")
    private Sale sales;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(name = "quantity")
    private Integer quantity;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private UserClient userClient;

    @Enumerated(EnumType.STRING)
    @Column(name = "state")
    private Status state;

    @Column(name = "date")
    private LocalDateTime date;
}