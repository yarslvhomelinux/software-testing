package com.liroykaz.testing.payment.stripe;

import com.liroykaz.testing.payment.CardPaymentCharge;
import com.liroykaz.testing.payment.CardPaymentCharger;
import com.liroykaz.testing.payment.Currency;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@ConditionalOnProperty(value = "stripe.enabled", havingValue = "false")
public class MockStripeService implements CardPaymentCharger {

    @Override
    public CardPaymentCharge chargeCard(BigDecimal amount,
                                        Currency currency,
                                        String cardSource,
                                        String description) {

        return new CardPaymentCharge(true);
    }
}
