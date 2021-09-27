package com.ironhack.midtermproject1.repository;

import com.ironhack.midtermproject1.dao.Saving;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SavingRepository extends JpaRepository<Saving, Long>{
    @Query(value = "SELECT * FROM saving_accounts WHERE (:id IS NULL OR id = :id) AND primary_owner_id = :primaryOwnerId", nativeQuery = true)
    List<Saving> findByIdAndPrimaryOwner(
            @Param("id") Long id,
            @Param("primaryOwnerId") Long primaryOwnerId);
}
