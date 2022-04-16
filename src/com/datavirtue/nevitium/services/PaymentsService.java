package com.datavirtue.nevitium.services;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentLink;
import com.stripe.param.PaymentLinkCreateParams;

/**
 *
 * @author SeanAnderson
 */
public class PaymentsService {

    public void createStripePaymentLinkForInvoice(String invoiceNumber, Double amount) throws StripeException {
        // Set your secret key. Remember to switch to your live secret key in production.
        // See your keys here: https://dashboard.stripe.com/apikeys
        Stripe.apiKey = "sk_test_8bbmdYazndAPuqfnjpK73DIG00iKN6BmX2";

        PaymentLinkCreateParams params = PaymentLinkCreateParams
                        .builder()
                        .addLineItem(
                                PaymentLinkCreateParams.LineItem
                                        .builder()
                                        .setPrice("{{price_1KoK5BCfX8oq4zzvsyacDQ30}}")
                                        .setQuantity()
                                        .build()
                        )
                        .build();

        PaymentLink paymentLink = PaymentLink.create(params);
    }

}
