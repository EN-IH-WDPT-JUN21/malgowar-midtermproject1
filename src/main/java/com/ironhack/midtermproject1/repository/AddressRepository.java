package com.ironhack.midtermproject1.repository;


import com.ironhack.midtermproject1.dao.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
}
