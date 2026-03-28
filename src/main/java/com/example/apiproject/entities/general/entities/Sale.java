    package com.example.apiproject.entities.general.entities;

    import com.example.apiproject.entities.admin.UserAdmin;
    import com.example.apiproject.entities.client.UserClient;
    import jakarta.persistence.*;
    import lombok.AllArgsConstructor;
    import lombok.Data;
    import lombok.NoArgsConstructor;

    import java.math.BigDecimal;
    import java.time.LocalDateTime;

    @Entity
    @Table(name = "sales")
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public class Sale {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
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