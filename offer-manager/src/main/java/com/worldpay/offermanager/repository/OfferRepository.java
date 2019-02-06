package com.worldpay.offermanager.repository;

import com.worldpay.offermanager.model.Offer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OfferRepository extends JpaRepository<Offer, Long> {


    @Query("from Offer where upper(description) like upper(:description)")
    List<Offer> findOffersByDescription(@Param("description") String description);

    @Query("select count(o) from Offer o where o.expiryDate <= :expiryDate and STATUS = 'ACTIVE'")
    Long countExpiredOffers(@Param("expiryDate") LocalDateTime expiryDate);

    @org.springframework.transaction.annotation.Transactional(propagation=Propagation.REQUIRES_NEW)
    @Modifying
    @Query("update Offer set status = 'EXPIRED' where expiryDate < :expiryDate and STATUS = 'ACTIVE'")
    int updateExpiryStatus(@Param("expiryDate") LocalDateTime expiryDate);



}