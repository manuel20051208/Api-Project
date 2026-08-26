package com.apiproject.entities.general;

import com.apiproject.entities.client.UserClient;
import com.apiproject.enums.Status;
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
@Table(name = "sh_sales_item")
public class ShSalesItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sh_sale_id")
    private ShSale shSale;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sh_product_id")
    private SecondHandProduct shProduct;

    @Column(name = "quantity")
    private Integer quantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id")
    private UserClient userClient;

    @Enumerated(EnumType.STRING)
    @Column(name = "state")
    private Status state;

    @Column(name = "date")
    private LocalDateTime date;
}
