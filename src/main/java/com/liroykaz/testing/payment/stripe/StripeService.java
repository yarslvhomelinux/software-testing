package com.liroykaz.testing.payment.stripe;

import com.liroykaz.testing.payment.CardPaymentCharge;
import com.liroykaz.testing.payment.CardPaymentCharger;
import com.liroykaz.testing.payment.Currency;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.net.RequestOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
public class StripeService implements CardPaymentCharger {

    private StripeApi stripeApi;

    private final static RequestOptions requestOptions = RequestOptions.builder()
            .setApiKey("sk_test_4eC39HqLyjWDarjtT1zdp7dc")
            .build();

    @Autowired
    public StripeService(StripeApi stripeApi) {
        this.stripeApi = stripeApi;
    }

    @Override
    public CardPaymentCharge chargeCard(BigDecimal amount,
                                        Currency currency,
                                        String cardSource,
                                        String description) {

        Map<String, Object> params = new HashMap<>();
        params.put("amount", amount);
        params.put("currency", currency);
        params.put("source", cardSource);
        params.put("description", description);

        try {
            Charge charge = stripeApi.create(params, requestOptions);
            Boolean chargePaid = charge.getPaid();

            return new CardPaymentCharge(chargePaid);
        } catch (StripeException e) {
            throw new RuntimeException("Cannot make stripe charge", e);
        }
    }
}
