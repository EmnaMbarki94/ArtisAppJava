package tn.esprit.services;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;

public class StripeService {

    public StripeService() {
        Stripe.apiKey = "sk_test_51RIEHFQlg3WeFHrl6oPaBPwYLP2mHCp8YwOwDyzXWFenL5hfFNoMrPkaQTGbjMYFdNnJiA4Enpz84VnZRngbiHjx00mUQfduBh"; // Remplacez par votre clé secrète
    }

    public String createPaymentIntent(double amount, String currency) {
        try {
            Stripe.apiKey = "sk_test_51RIEHFQlg3WeFHrl6oPaBPwYLP2mHCp8YwOwDyzXWFenL5hfFNoMrPkaQTGbjMYFdNnJiA4Enpz84VnZRngbiHjx00mUQfduBh";

            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount((long) (amount * 100)) // Convertir en cents
                    .setCurrency(currency.toLowerCase())
                    .build();

            PaymentIntent intent = PaymentIntent.create(params);

            // Vérifiez que vous retournez bien le client_secret complet
            System.out.println("[DEBUG] ClientSecret: " + intent.getClientSecret());
            return intent.getClientSecret();

        } catch (StripeException e) {
            System.err.println("Erreur Stripe: " + e.getMessage());
            return null;
        }
    }
}