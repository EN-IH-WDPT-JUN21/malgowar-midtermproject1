package com.ironhack.midtermproject1.repository;

import com.ironhack.midtermproject1.dao.CreditCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface CreditCardRepository extends JpaRepository<CreditCard, Long> {
    @Query(value = "SELECT * FROM credit_cards WHERE " +
            "(:minBalance IS NULL OR balance > :minBalance) AND (:maxBalance is null OR balance < :maxBalance) " +
            "AND (:minCreationDate IS NULL OR CAST(creation_date AS DATE) > STR_TO_DATE(:minCreationDate, '%Y-%m-%d')) " +
            "AND (:maxCreationDate IS NULL OR CAST(creation_date AS DATE) < STR_TO_DATE(:maxCreationDate, '%Y-%m-%d')) " +
            "AND (:minAdditionLastInterestDate IS NULL OR CAST(last_interest_date AS DATE) > STR_TO_DATE(:minAdditionLastInterestDate, '%Y-%m-%d')) " +
            "AND (:maxAdditionLastInterestDate IS NULL OR CAST(last_interest_date AS DATE) < STR_TO_DATE(:maxAdditionLastInterestDate, '%Y-%m-%d')) " +
            "AND (:minCreditLimit IS NULL OR credit_limit > :minCreditLimit) AND (:maxCreditLimit is null OR credit_limit < :maxCreditLimit)" +
            "AND (:minInterestRate IS NULL OR interest_rate > :minInterestRate) AND (:maxInterestRate is null OR interest_rate < :maxInterestRate) " +
            "AND (:primaryOwner IS NULL OR primary_owner_id = :primaryOwner) " +
            "AND (:secondaryOwner IS NULL OR secondary_owner_id = :secondaryOwner)", nativeQuery = true)
    List<CreditCard> findByAnyParams(
            @Param("maxBalance") BigDecimal maxBalance,
            @Param("minBalance") BigDecimal minBalance,
            @Param("maxCreditLimit") BigDecimal maxCreditLimit,
            @Param("minCreditLimit") BigDecimal minCreditLimit,
            @Param("maxCreationDate") String maxCreationDate,
            @Param("minCreationDate") String minCreationDate,
            @Param("maxAdditionLastInterestDate") String maxAdditionLastInterestDate,
            @Param("minAdditionLastInterestDate") String minAdditionLastInterestDate,
            @Param("maxInterestRate") BigDecimal maxInterestRate,
            @Param("minInterestRate") BigDecimal minInterestRate,
            @Param("primaryOwner") Long primaryOwner,
            @Param("secondaryOwner") Long secondaryOwner
    );
    @Query(value = "SELECT * FROM credit_cards WHERE (:id IS NULL OR id = :id) AND primary_owner_id = :primaryOwnerId", nativeQuery = true)
    List<CreditCard> findByIdAndPrimaryOwner(
            @Param("id") Long id,
            @Param("primaryOwnerId") Long primaryOwnerId);
}
