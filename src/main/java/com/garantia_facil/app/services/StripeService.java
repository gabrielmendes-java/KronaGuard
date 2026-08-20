package com.garantia_facil.app.services;

import com.garantia_facil.app.models.Plano;
import com.garantia_facil.app.models.Usuario;
import com.garantia_facil.app.repositories.UsuarioRepository;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class StripeService {
    @Value("${STRIPE_PRICE_ID}")
    private String priceId;

    UsuarioRepository usuarioRepository;

    public StripeService(UsuarioRepository usuarioRepository){
        this.usuarioRepository=usuarioRepository;
    }

    public String criarCheckout(String email, Plano plano) throws Exception {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado."));

        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
                .setSuccessUrl("https://kronaguard-production.up.railway.app/assinatura/sucesso")
                .setCancelUrl("https://kronaguard-production.up.railway.app/assinatura/cancelada")
                .putMetadata("usuarioId", usuario.getId().toString())
                .putMetadata("plano", plano.name())
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setPrice(priceId)
                                .setQuantity(1L)
                                .build()
                )
                .build();
        Session session = Session.create(params);

        return session.getUrl();
    }
}
