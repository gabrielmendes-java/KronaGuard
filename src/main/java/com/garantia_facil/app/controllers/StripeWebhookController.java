package com.garantia_facil.app.controllers;

import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.StripeObject;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.serializer.Deserializer;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/webhook")
public class StripeWebhookController {
    @Value("${SECRET_WEBHOOK}")
    private String secretWebhook;

    @PostMapping("/stripe")
    public ResponseEntity<String> receber(@RequestBody String payload, @RequestHeader("Stripe-Signature") String sigHeader){
        try{
            Event event = Webhook.constructEvent(payload, sigHeader, secretWebhook);

            if (event.getType().equals("checkout.session.completed")){
                EventDataObjectDeserializer deserializer = event.getDataObjectDeserializer();
                StripeObject stripeObject = deserializer.getObject()
                        .orElseThrow(() -> new IllegalStateException("Não foi possível extrair o objeto"));

                Session session = (Session) stripeObject;

                System.out.println("Sessão extraída");
                System.out.println("Id: " + session.getId());
            }
            return ResponseEntity.ok("WEBHOOK RECEBIDO");
        } catch (SignatureVerificationException e) {

            return ResponseEntity.badRequest().body("Assinatura inválida");
        }catch (Exception e){
            return ResponseEntity.badRequest().body("Erro ao processar webhook");
        }

    }
}
