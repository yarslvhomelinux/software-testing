package com.liroykaz.testing.payment.stripe;

import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.net.RequestOptions;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class StripeApi {

    public Charge create(Map<String, Object> params, RequestOptions requestOptions) throws StripeException {
        return Charge.create(params, requestOptions);
    }
}
