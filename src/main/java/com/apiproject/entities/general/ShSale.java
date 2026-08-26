package com.apiproject.entities.general;

import com.apiproject.entities.admin.UserAdmin;
import com.apiproject.entities.client.UserClient;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@DynamicUpdate
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "sh_sales")
public class ShSale {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sh_sale_seq")
    @SequenceGenerator(name = "sh_sale_seq", sequenceName = "sh_sales_id_seq", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id")
    private UserClient userClient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserAdmin userAdmin;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "created_at")
    private LocalDateTime hora;
}
