package com.garantia_facil.app.services;

import com.garantia_facil.app.configurations.SecurityConfiguration;
import com.garantia_facil.app.models.Role;
import com.garantia_facil.app.models.Usuario;
import com.garantia_facil.app.repositories.UsuarioRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UsuarioService {
    UsuarioRepository usuarioRepository;
    PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder){
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
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
}
