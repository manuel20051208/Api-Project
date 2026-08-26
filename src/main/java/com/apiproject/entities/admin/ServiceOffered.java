package com.apiproject.entities.admin;

import com.apiproject.entities.admin.UserAdmin;
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
@Table(name = "services_offered")
public class ServiceOffered {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name_of_service", length = 40, nullable = false)
    private String nameOfService;

    @Column(name = "value_of_service", nullable = false)
    private Double valueOfService;

    @Column(name = "description_of_service", length = 255, nullable = false)
    private String descriptionOfService;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private UserAdmin userAdmin;
}
