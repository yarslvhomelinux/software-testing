package com.liroykaz.testing.payment.stripe;

import com.liroykaz.testing.payment.CardPaymentCharge;
import com.liroykaz.testing.payment.Currency;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.net.RequestOptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

class StripeServiceTest {

    private StripeService underTest;

    @Mock
    private StripeApi stripeApi;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        underTest = new StripeService(stripeApi);
    }

    @Test
    void isShouldChargeCard() throws StripeException {
        //Given
        String cardSource = "0x0x0x";
        String description = "Zakat";
        BigDecimal amount = new BigDecimal("10.00");
        Currency currency = Currency.USD;

        Charge charge = new Charge();
        charge.setPaid(Boolean.TRUE);
        given(stripeApi.create(anyMap(), any())).willReturn(charge);

        //When
        CardPaymentCharge cardPaymentCharge = underTest.chargeCard(amount, currency, cardSource, description);

        //Then
        ArgumentCaptor<Map<String, Object>> mapArgumentCaptor = ArgumentCaptor.forClass(Map.class);
        ArgumentCaptor<RequestOptions> requestOptionsArgumentCaptor = ArgumentCaptor.forClass(RequestOptions.class);
        then(stripeApi).should().create(mapArgumentCaptor.capture(), requestOptionsArgumentCaptor.capture());
        Map<String, Object> mapCapture = mapArgumentCaptor.getValue();

        assertThat(mapCapture).hasSize(4);
        assertThat(mapCapture.get("amount")).isEqualTo(amount);
        assertThat(mapCapture.get("currency")).isEqualTo(currency);
        assertThat(mapCapture.get("source")).isEqualTo(cardSource);
        assertThat(mapCapture.get("description")).isEqualTo(description);

        RequestOptions requestOptionsCapture = requestOptionsArgumentCaptor.getValue();
        assertThat(requestOptionsCapture).isNotNull();

        assertThat(cardPaymentCharge.isCardDebited()).isTrue();
    }
}