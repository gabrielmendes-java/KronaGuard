package com.garantia_facil.app.services;

import com.garantia_facil.app.models.*;
import com.garantia_facil.app.repositories.AssinaturaRepository;
import com.garantia_facil.app.repositories.UsuarioRepository;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class StripeService {
    @Value("${STRIPE_PRICE_ID}")
    private String priceId;

    UsuarioRepository usuarioRepository;
    AssinaturaRepository assinaturaRepository;

    public StripeService(UsuarioRepository usuarioRepository, AssinaturaRepository assinaturaRepository){
        this.usuarioRepository=usuarioRepository;
        this.assinaturaRepository=assinaturaRepository;
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

    public void criarAssinatura(Session session){
        String plano = session.getMetadata().get("plano");
        Long usuarioId = Long.parseLong(session.getMetadata().get("usuarioId"));
        String customerId = session.getCustomer();
        String subscriptionId = session.getSubscription();

        if (!assinaturaRepository.existsByStripeSubscriptionId(subscriptionId)){
            Usuario usuario = usuarioRepository.findById(usuarioId)
                    .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));

            Assinatura assinatura = new Assinatura();

            assinatura.setPlano(Plano.valueOf(plano));
            assinatura.setUsuario(usuario);
            assinatura.setStripeCustomerId(customerId);
            assinatura.setStripeSubscriptionId(subscriptionId);
            assinatura.setDataInicio(LocalDateTime.now());
            assinatura.setStatus(StatusAssinatura.ATIVA);
            usuario.setRole(Role.TECNICO);

            assinaturaRepository.save(assinatura);
        }
    }
}