package com.liroykaz.testing.customer;

import com.liroykaz.testing.PhoneNumberValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

class CustomerRegistrationServiceTest {

    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private PhoneNumberValidator validator;

    @Captor
    private ArgumentCaptor<Customer> customerArgumentCaptor;

    private CustomerRegistrationService underTest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        underTest = new CustomerRegistrationService(customerRepository, validator);
    }

    @Test
    void itShouldSaveNewCustomer() {
        //given a phone number with a customer
        String phoneNumber = "+447000000000";
        Customer customer = new Customer(UUID.randomUUID(), "Mariam", phoneNumber);

        //... a request
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(customer);

        //... no customer with phone number passed
        given(customerRepository.selectCustomerByPhoneNumber(customer.getPhoneNumber()))
                .willReturn(Optional.empty());

        //... valid phone number
        given(validator.test(phoneNumber)).willReturn(Boolean.TRUE);

        //when
        underTest.registerNewCustomer(request);

        //then
        then(customerRepository).should().save(customerArgumentCaptor.capture());
        Customer customerArgumentCaptorValue = customerArgumentCaptor.getValue();
        assertThat(customerArgumentCaptorValue).isEqualToComparingFieldByField(customer);
    }

    @Test
    void itShouldThrowExceptionForSaveCustomer() {
        //given
        String phoneNumber = "0000099";
        Customer requestedCustomer = new Customer(UUID.randomUUID(), "Mariam", phoneNumber);
        Customer responsedCustomer = new Customer(UUID.randomUUID(), "Johny", phoneNumber);

        //... a request
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(requestedCustomer);

        //... existed customer by phoneNumber
        given(customerRepository.selectCustomerByPhoneNumber(phoneNumber))
                .willReturn(Optional.of(responsedCustomer));

        //... valid phone number
        given(validator.test(phoneNumber)).willReturn(Boolean.TRUE);

        //when

        //then
        then(customerRepository).shouldHaveNoInteractions();
        assertThatThrownBy(() -> underTest.registerNewCustomer(request))
                .isInstanceOf(IllegalStateException.class);

        //finally
        then(customerRepository).should(never()).save(any(Customer.class));
    }

    @Test
    void itShouldThrowNewExceptionForNotValidPhoneNumber() {
        //given
        String phoneNumber = "0000099";
        Customer requestedCustomer = new Customer(UUID.randomUUID(), "Mariam", phoneNumber);
        Customer responsedCustomer = new Customer(UUID.randomUUID(), "Johny", phoneNumber);

        //... a request
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(requestedCustomer);

        //... existed customer by phoneNumber
        given(customerRepository.selectCustomerByPhoneNumber(phoneNumber))
                .willReturn(Optional.of(responsedCustomer));

        //... valid phone number
        given(validator.test(phoneNumber)).willReturn(Boolean.FALSE);

        //when

        //then
        then(customerRepository).shouldHaveNoInteractions();
        assertThatThrownBy(() -> underTest.registerNewCustomer(request))
                .isInstanceOf(IllegalStateException.class);

        //finally
        then(customerRepository).should(never()).save(any(Customer.class));
    }

    @Test
    void itShouldNotSaveCustomerWhenCustomerExists() {
        //given
        String phoneNumber = "+447000000000";
        Customer requestedCustomer = new Customer(UUID.randomUUID(), "Mariam", phoneNumber);

        //... a request
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(requestedCustomer);

        //... no customer with phone number passed
        given(customerRepository.selectCustomerByPhoneNumber(phoneNumber))
                .willReturn(Optional.of(requestedCustomer));

        //... valid phone number
        given(validator.test(phoneNumber)).willReturn(Boolean.TRUE);

        //when
        underTest.registerNewCustomer(request);

        //then
        then(customerRepository).should(never()).save(any());
        then(customerRepository).should().selectCustomerByPhoneNumber(phoneNumber);
        then(customerRepository).shouldHaveNoMoreInteractions();
    }

    @Test
    void itShouldSaveNewCustomerWhenIdIsNull() {
        //given a phone number with a customer
        String phoneNumber = "+447000000000";
        Customer customer = new Customer(UUID.randomUUID(), "Mariam", phoneNumber);

        //... a request
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(customer);

        //... no customer with phone number passed
        given(customerRepository.selectCustomerByPhoneNumber(customer.getPhoneNumber()))
                .willReturn(Optional.empty());

        //... valid phone number
        given(validator.test(phoneNumber)).willReturn(Boolean.TRUE);

        //when
        underTest.registerNewCustomer(request);

        //then
        then(customerRepository).should().save(customerArgumentCaptor.capture());
        Customer customerArgumentCaptorValue = customerArgumentCaptor.getValue();
        assertThat(customerArgumentCaptorValue).isEqualToIgnoringGivenFields(customer, "id");
        assertThat(customerArgumentCaptorValue.getId()).isNotNull();
    }
}