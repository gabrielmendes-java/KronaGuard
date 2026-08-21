package com.garantia_facil.app.controllers;

import com.garantia_facil.app.models.Plano;
import com.garantia_facil.app.services.StripeService;
import com.garantia_facil.app.services.UsuarioService;
import com.stripe.exception.StripeException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class StripeController {
    private final StripeService stripeService;
    private final UsuarioService usuarioService;
    public StripeController(StripeService stripeService, UsuarioService usuarioService){
        this.stripeService = stripeService;
        this.usuarioService = usuarioService;
    }

    @GetMapping("/planos")
    public String planos(Model model, Authentication authentication){
        model.addAttribute("possuiAssinatura", usuarioService.possuiAssinatura(authentication.getName()));
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

    @PostMapping("/assinatura/portal")
    public String portal(Authentication authentication) throws StripeException {
        String url = stripeService.criarPortalCliente(authentication.getName());
        return "redirect:"+url;
    }
}
