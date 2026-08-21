package com.garantia_facil.app.repositories;

import com.garantia_facil.app.models.Garantia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GarantiaRepository extends JpaRepository<Garantia, Long> {
    List<Garantia> findByUsuarioIdAndClienteNomeContainingIgnoreCaseOrUsuarioIdAndCpfContaining(Long usuarioId1, String nome, Long usuarioId2, String cpf);
    Optional<Garantia> findByUsuarioIdAndCodigoGarantia(Long usuarioId, String codigoGarantia);
    List<Garantia> findByUsuarioId(Long id);
}
