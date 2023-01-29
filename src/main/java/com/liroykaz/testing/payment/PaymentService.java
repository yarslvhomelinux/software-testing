package com.liroykaz.testing.payment;

import com.liroykaz.testing.customer.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.stream.Stream;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final CustomerRepository customerRepository;
    private final CardPaymentCharger cardPaymentCharger;

    @Autowired
    public PaymentService(PaymentRepository paymentRepository,
                          CustomerRepository customerRepository,
                          CardPaymentCharger cardPaymentCharger) {
        this.paymentRepository = paymentRepository;
        this.customerRepository = customerRepository;
        this.cardPaymentCharger = cardPaymentCharger;
    }

    public void chargeCard(UUID customerId, PaymentRequest request) {
        //1. Does customer exist if no throw
        boolean isCustomerFound = customerRepository.findById(customerId).isPresent();
        if (!isCustomerFound) {
            throw new IllegalStateException(String.format("Customer with id [%s] not found", customerId));
        }

        //2. Do we support currency if no throw
        boolean isCurrencySupported = Stream.of(Currency.USD, Currency.GPB)
                .anyMatch(c -> request.getPayment().getCurrency().equals(c));

        if (!isCurrencySupported) {
            throw new IllegalStateException(String.format("Currency [%s] not supported", request.getPayment().getCurrency()));
        }

        //3. Charge card
        Payment payment = request.getPayment();
        CardPaymentCharge charge = cardPaymentCharger.chargeCard(
                payment.getAmount(),
                payment.getCurrency(),
                payment.getSource(),
                payment.getDescription());
        
        //4. If not debited throw
        if (!charge.isCardDebited()) {
            throw new IllegalStateException(String.format("Card not debited for customer [%s]", customerId));
        }

        //5. Insert payment
        payment.setCustomerId(customerId);
        paymentRepository.save(payment);

        //6. TODO: Send SMS
    }
}
