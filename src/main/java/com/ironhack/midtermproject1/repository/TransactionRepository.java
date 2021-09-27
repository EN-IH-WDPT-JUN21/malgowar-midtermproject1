package com.ironhack.midtermproject1.repository;

import com.ironhack.midtermproject1.dao.Transaction;
import com.ironhack.midtermproject1.enums.ReturnType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    @Query(value = "SELECT * FROM transactions " +
            "WHERE id IN (" +
            "SELECT t1_id FROM(" +
                "SELECT t1_id,  t1_acc_id, t1_source, diff, count(*) AS count " +
                "FROM (" +
                    "SELECT t1.id as t1_id, t2.id as t2_id,t1.source_account_id AS t1_acc_id, t2.source_account_id AS t2_acc_id, " +
                    "t1.source_account_type as t1_source, t2.source_account_type as t2_source, t1.transaction_date as t1_date, t2.transaction_date as t2_date, " +
                    "ABS(TIMESTAMPDIFF(SECOND,t1.transaction_date,t2.transaction_date)) as diff " +
                    "FROM transactions t1 INNER JOIN transactions t2 ON t1.source_account_id = t2.source_account_id AND t1.source_account_type = t2.source_account_type " +
                    "WHERE t1.source_account_type <> 'CREDIT_CARD' and t1.id >= t2.id) data " +
                    "GROUP BY t1_id, t1_acc_id, t1_source, diff " +
                    "HAVING diff = 0 AND count > 2) data2) ", nativeQuery = true)
    List<Transaction> findFraudAccount();
    List<Transaction> findBySourceAccountIdAndSourceAccountType(Long id, ReturnType returnType);
    @Query(value = "SELECT * FROM transactions WHERE " +
            "(:sourceAccountId IS NULL OR source_account_id = :sourceAccountId) " +
            "AND (:sourceAccountType IS NULL OR source_account_type = :sourceAccountType) " +
            "AND (:targetAccountId IS NULL OR target_account_id = :targetAccountId) " +
            "AND (:targetAccountType IS NULL OR target_account_type = :targetAccountType) " +
            "AND (:sourceAccountPrimaryOwner IS NULL OR source_account_primary_owner_id = :sourceAccountPrimaryOwner) " +
            "AND (:targetAccountPrimaryOwner IS NULL OR target_account_primary_owner_id = :targetAccountPrimaryOwner) " +
            "AND (:transactionType IS NULL OR transaction_type = :transactionType) ", nativeQuery = true)
    List<Transaction> findByAnyParams(
            @Param("sourceAccountId") Long sourceAccountId,
            @Param("sourceAccountType") String sourceAccountType,
            @Param("targetAccountId") Long targetAccountId,
            @Param("targetAccountType") String targetAccountType,
            @Param("sourceAccountPrimaryOwner") Long sourceAccountPrimaryOwner,
            @Param("targetAccountPrimaryOwner") Long targetAccountPrimaryOwner,
            @Param("transactionType") String transactionType);
}