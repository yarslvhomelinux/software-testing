package com.liroykaz.testing.payment;

import com.liroykaz.testing.customer.Customer;
import com.liroykaz.testing.customer.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;

class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private CardPaymentCharger cardPaymentCharger;

    private PaymentService underTest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        underTest = new PaymentService(paymentRepository, customerRepository, cardPaymentCharger);
    }

    @Test
    void itShouldChargeCardSuccessfully() {
        //given
        UUID customerId = UUID.randomUUID();

        //... Customer exists
        given(customerRepository.findById(customerId)).willReturn(Optional.of(mock(Customer.class))); //we can use also constructor of Customer

        Currency currency = Currency.USD;
        //... Payment request
        PaymentRequest paymentRequest = new PaymentRequest(
                new Payment(null, customerId, new BigDecimal("100.00"), currency, "card123xx", "Donation"));

        //... Card is charged successfully
        given(cardPaymentCharger.chargeCard(paymentRequest.getPayment().getAmount(),
                paymentRequest.getPayment().getCurrency(),
                paymentRequest.getPayment().getSource(),
                paymentRequest.getPayment().getDescription())).willReturn(new CardPaymentCharge(true));

        //when
        underTest.chargeCard(customerId, paymentRequest);

        //then
        ArgumentCaptor<Payment> paymentRequestArgumentCaptor = ArgumentCaptor.forClass(Payment.class);

        then(paymentRepository).should().save(paymentRequestArgumentCaptor.capture());
        Payment captorValue = paymentRequestArgumentCaptor.getValue();
        assertThat(captorValue).isEqualToIgnoringGivenFields(paymentRequest.getPayment(), "customerId");
        assertThat(captorValue.getCustomerId()).isEqualTo(customerId);
    }

    @Test
    void itShouldThrowWhenCardIsNotCharge() {
        //given
        UUID customerId = UUID.randomUUID();

        //... Customer exists
        given(customerRepository.findById(customerId)).willReturn(Optional.of(mock(Customer.class))); //we can use also constructor of Customer

        Currency currency = Currency.USD;
        //... Payment request
        PaymentRequest paymentRequest = new PaymentRequest(
                new Payment(null, customerId, new BigDecimal("100.00"), currency, "card123xx", "Donation"));

        //... Card is charged successfully
        given(cardPaymentCharger.chargeCard(paymentRequest.getPayment().getAmount(),
                paymentRequest.getPayment().getCurrency(),
                paymentRequest.getPayment().getSource(),
                paymentRequest.getPayment().getDescription())).willReturn(new CardPaymentCharge(false));

        //then
        then(paymentRepository).should(never()).save(any(Payment.class));
        assertThatThrownBy(() -> underTest.chargeCard(customerId, paymentRequest))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining(String.format("Card not debited for customer [%s]", customerId));
    }

    @Test
    void itShouldThrowExceptionWhenCurrencyNotSupported() {
        //given
        UUID customerId = UUID.randomUUID();

        //... Customer exists
        given(customerRepository.findById(customerId)).willReturn(Optional.of(mock(Customer.class))); //we can use also constructor of Customer

        Currency currency = Currency.RUB;
        //... Payment request
        PaymentRequest paymentRequest = new PaymentRequest(
                new Payment(null, customerId, new BigDecimal("100.00"), currency, "card123xx", "Donation"));

        //... Card is charged successfully
        given(cardPaymentCharger.chargeCard(paymentRequest.getPayment().getAmount(),
                paymentRequest.getPayment().getCurrency(),
                paymentRequest.getPayment().getSource(),
                paymentRequest.getPayment().getDescription())).willReturn(new CardPaymentCharge(true));

        //then
        then(paymentRepository).should(never()).save(any(Payment.class));
        assertThatThrownBy(() -> underTest.chargeCard(customerId, paymentRequest))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining(String.format("Currency [%s] not supported", paymentRequest.getPayment().getCurrency()));

        then(cardPaymentCharger).shouldHaveNoInteractions();
    }

    @Test
    void itShouldNotChargeAndThrownWhenCustomerNotFound() {
        //given
        UUID customerId = UUID.randomUUID();

        //... Customer exists
        given(customerRepository.findById(customerId)).willReturn(Optional.empty());

        //when
        assertThatThrownBy(() -> underTest.chargeCard(customerId, new PaymentRequest(new Payment())))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining(String.format("Customer with id [%s] not found", customerId));

        //then
        then(paymentRepository).should(never()).save(any(Payment.class));
        then(cardPaymentCharger).shouldHaveNoInteractions();
    }
}