package com.garantia_facil.app.repositories;

import com.garantia_facil.app.models.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByToken(String token);
    Optional<PasswordResetToken> findByUsuarioId(Long id);
    void deleteByUsuarioId(Long usuarioId);
}
