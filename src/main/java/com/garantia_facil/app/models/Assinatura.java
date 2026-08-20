package com.garantia_facil.app.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "assinaturas")
@Getter
@Setter
public class Assinatura {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @JoinColumn(name = "usuario_id",nullable = false)
    @OneToOne
    private Usuario usuario;

    @NotNull
    @Column(nullable = false)
    private String stripeCustomerId;

    @NotNull
    @Column(nullable = false, unique = true)
    private String stripeSubscriptionId;

    @NotNull
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Plano plano;

    @NotNull
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private StatusAssinatura status;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime dataInicio;

    private LocalDateTime dataFim;
}
