package com.garantia_facil.app.repositories;

import com.garantia_facil.app.models.Garantia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GarantiaRepository extends JpaRepository<Garantia, Long> {
    List<Garantia> findByClienteNomeContainingIgnoreCaseOrCpfContaining(String nome, String cpf);
    Optional<Garantia> findByCodigoGarantia(String codigoGarantia);
}
