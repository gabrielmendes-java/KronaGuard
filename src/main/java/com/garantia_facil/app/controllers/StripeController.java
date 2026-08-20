package com.garantia_facil.app.controllers;

import com.garantia_facil.app.models.Plano;
import com.garantia_facil.app.services.StripeService;
import com.garantia_facil.app.services.UsuarioService;
import com.stripe.exception.StripeException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class StripeController {
    private final StripeService stripeService;

    public StripeController(StripeService stripeService){
        this.stripeService = stripeService;
    }

    @GetMapping("/planos")
    public String planos(){
        return "planos";
    }

    @PostMapping("/assinatura/checkout")
    public String checkout(Authentication authentication) throws Exception {
        return "redirect:" + stripeService.criarCheckout(authentication.getName(), Plano.PRO);
    }

    @GetMapping("/assinatura/sucesso")
    public String sucesso() {
        return "assinatura-sucesso";
    }

    @GetMapping("/assinatura/cancelada")
    public String cancelada(){
        return "assinatura-cancelada";
    }
}
