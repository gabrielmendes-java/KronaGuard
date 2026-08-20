package com.garantia_facil.app.services;

import com.garantia_facil.app.models.Usuario;
import com.garantia_facil.app.repositories.AssinaturaRepository;
import com.garantia_facil.app.repositories.UsuarioRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailService implements UserDetailsService {
    private final UsuarioRepository usuarioRepository;
    private final AssinaturaRepository assinaturaRepository;

    public CustomUserDetailService(UsuarioRepository usuarioRepository, AssinaturaRepository assinaturaRepository){
        this.usuarioRepository = usuarioRepository;
        this.assinaturaRepository = assinaturaRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(()-> new UsernameNotFoundException("Usuário não encontrado."));

        User.UserBuilder builder = User.builder()
                .username(usuario.getEmail())
                .password(usuario.getSenha())
                .roles(usuario.getRole().name());

        boolean assinaturaAtiva = assinaturaRepository.existsByUsuarioIdAndStatus(usuario.getId(), usuario.getAssinatura().getStatus());

        if (assinaturaAtiva){
            builder.authorities("ASSINATURA_ATIVA");
        }

        return builder.build();
    }
}