package com.liroykaz.testing.payment;

import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface PaymentRepository extends CrudRepository<Payment, Long> {

}
