package com.garantia_facil.app.repositories;

import com.garantia_facil.app.models.Assinatura;
import com.garantia_facil.app.models.StatusAssinatura;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AssinaturaRepository extends JpaRepository<Assinatura, Long> {
    boolean existsByStripeSubscriptionId(String subscriptionId);
    Optional<Assinatura> findByUsuarioId(Long id);
    boolean existsByUsuarioIdAndStatus(Long id, StatusAssinatura status);
}
