package com.ironhack.midtermproject1.repository;

import com.ironhack.midtermproject1.dao.StudentChecking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentCheckingRepository extends JpaRepository<StudentChecking, Long> {
    @Query(value = "SELECT * FROM student_checking_accounts WHERE (:id IS NULL OR id = :id) AND primary_owner_id = :primaryOwnerId", nativeQuery = true)
    List<StudentChecking> findByIdAndPrimaryOwner(
            @Param("id") Long id,
            @Param("primaryOwnerId") Long primaryOwnerId);
}
