package com.example.apiproject.repositories.client;

import com.example.apiproject.entities.client.PaymentCard;
import com.example.apiproject.repositories.projection.PaymentCardDetailsProjection;
import com.example.apiproject.repositories.projection.PaymentCardOwnerProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentCardRepository extends JpaRepository<PaymentCard, Long> {
    boolean existsByUserClientIdAndActiveTrue(Long clientId);
    List<PaymentCard> findAllByUserClientIdOrderByCreatedAtDesc(Long clientId);

    @Query("""
            select p.id as id,
                   p.userClient.id as clientId,
                   p.cardHolderName as cardHolderName,
                   p.brand as brand,
                   p.lastFour as lastFour,
                   p.active as active,
                   p.createdAt as createdAt
            from PaymentCard p
            where p.userClient.id = :clientId
            order by p.createdAt desc
            """)
    List<PaymentCardDetailsProjection> findDetailsByUserClientIdOrderByCreatedAtDesc(@Param("clientId") Long clientId);

    @Query("""
            select p.id as id,
                   p.userClient.id as clientId,
                   p.cardHolderName as cardHolderName,
                   p.brand as brand,
                   p.lastFour as lastFour,
                   p.active as active,
                   p.createdAt as createdAt
            from PaymentCard p
            where p.id = :cardId
            """)
    Optional<PaymentCardDetailsProjection> findDetailsById(@Param("cardId") Long cardId);

    @Query("select p.userClient.id as clientId from PaymentCard p where p.id = :cardId")
    Optional<PaymentCardOwnerProjection> findOwnerById(@Param("cardId") Long cardId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update PaymentCard p set p.active = :active where p.id = :cardId")
    int updateActiveById(@Param("cardId") Long cardId, @Param("active") boolean active);
}
