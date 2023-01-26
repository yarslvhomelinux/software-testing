package com.liroykaz.testing.customer;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface CustomerRepository extends CrudRepository<Customer, UUID> {

    @Query(value = "select id, name, phoneNumber from customer where phoneNumber = :phoneNumber", nativeQuery = true)
    Optional<Customer> selectCustomerByPhoneNumber(@Param("phoneNumber") String phoneNumber);
}
