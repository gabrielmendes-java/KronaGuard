package com.garantia_facil.app.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.br.CPF;

import java.time.LocalDate;

@Entity
@Table(name = "garantias")
@Getter
@Setter
public class Garantia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(nullable = false)
    @NotBlank(message = "O nome do cliente é obrigatório.")
    private String clienteNome;

    @Column(nullable = false)
    @CPF(message = "CPF inválido")
    private String cpf;

    @Column(nullable = false)
    @NotBlank(message = "O modelo do aparelho é obrigatório")
    private String aparelho;

    @Column(nullable = false)
    @NotBlank(message = "O serviço realizado é obrigatório")
    private String servico;

    @Column(nullable = false)
    @NotNull(message = "A data de realização é obrigatória")
    private LocalDate dataRealizacao;

    @Column(nullable = false)
    @NotNull(message = "Os dias de garantia são obrigatórios")
    @Min(value = 1, message = "A garantia deve ser de pelo menos 1 dia")
    private Integer diasGarantia;

    @Column(nullable = false)
    private LocalDate dataValidade;

    @Column(unique = true, nullable = false)
    private String codigoGarantia;

    private boolean malUso = false;

    @Column(columnDefinition = "TEXT")
    private String observacaoTecnica;
}
