package com.liroykaz.testing.customer;

import com.liroykaz.testing.PhoneNumberValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class CustomerRegistrationService {

    private final CustomerRepository customerRepository;
    private final PhoneNumberValidator validator;

    @Autowired
    public CustomerRegistrationService(CustomerRepository customerRepository, PhoneNumberValidator validator) {
        this.customerRepository = customerRepository;
        this.validator = validator;
    }

    public void registerNewCustomer(CustomerRegistrationRequest request) {
        String phoneNumber = request.getCustomer().getPhoneNumber();
        //TODO: add validate of phoneNumber
        if (!validator.test(phoneNumber)) {
            throw new IllegalStateException("Phone number " + phoneNumber + " is not valid");
        }

        Optional<Customer> customerOptional = customerRepository.selectCustomerByPhoneNumber(phoneNumber);
        if (customerOptional.isPresent()) {
            Customer customer = customerOptional.get();
            if (customer.getName().equals(request.getCustomer().getName())) {
                return;
            }

            throw new IllegalStateException(String.format("Phone number [%s] is taken", phoneNumber));
        }

        if (request.getCustomer().getId() == null) {
            request.getCustomer().setId(UUID.randomUUID());
        }
        customerRepository.save(request.getCustomer());
    }
}
