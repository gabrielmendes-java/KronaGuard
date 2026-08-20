package com.garantia_facil.app.services;

import com.garantia_facil.app.configurations.SecurityConfiguration;
import com.garantia_facil.app.models.Role;
import com.garantia_facil.app.models.StatusAssinatura;
import com.garantia_facil.app.models.Usuario;
import com.garantia_facil.app.repositories.AssinaturaRepository;
import com.garantia_facil.app.repositories.UsuarioRepository;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import jdk.jshell.Snippet;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UsuarioService {
    UsuarioRepository usuarioRepository;
    PasswordEncoder passwordEncoder;
    AssinaturaRepository assinaturaRepository;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder, AssinaturaRepository assinaturaRepository){
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.assinaturaRepository = assinaturaRepository;
    }

    public Usuario buscarPorEmail(String email){
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));
        return usuario;
    }

    public void salvar(Usuario usuario){
        if(usuarioRepository.existsByEmail(usuario.getEmail())){
            throw new IllegalArgumentException("Este email já está cadastrado.");
        }

        usuario.setRole(Role.LOGADO);
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        usuarioRepository.save(usuario);
    }

    public boolean possuiAssinatura(String email){
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));

        return assinaturaRepository.existsByUsuarioIdAndStatus(usuario.getId(), StatusAssinatura.ATIVA);
    }
}
