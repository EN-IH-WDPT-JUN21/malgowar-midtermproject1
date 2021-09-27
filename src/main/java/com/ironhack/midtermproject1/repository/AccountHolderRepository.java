package com.ironhack.midtermproject1.repository;

import com.ironhack.midtermproject1.dao.AccountHolder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountHolderRepository extends JpaRepository<AccountHolder, Long> {
}
