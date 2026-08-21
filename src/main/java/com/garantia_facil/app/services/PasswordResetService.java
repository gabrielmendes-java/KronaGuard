package com.garantia_facil.app.services;

import com.garantia_facil.app.models.PasswordResetToken;
import com.garantia_facil.app.models.Usuario;
import com.garantia_facil.app.repositories.PasswordResetTokenRepository;
import com.garantia_facil.app.repositories.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PasswordResetService {
    private final UsuarioRepository usuarioRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public PasswordResetService (EmailService emailService,PasswordEncoder passwordEncoder,UsuarioRepository usuarioRepository, PasswordResetTokenRepository passwordResetTokenRepository){
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    @Transactional
    public void criarToken(String email){
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElse(null);

        if (usuario == null){
            return;
        }

        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = passwordResetTokenRepository.findByUsuarioId(usuario.getId())
                .orElse(new PasswordResetToken());

        resetToken.setToken(token);
        resetToken.setUsuario(usuario);
        resetToken.setExpiracao(LocalDateTime.now().plusMinutes(15));
        resetToken.setUtilizado(false);

        passwordResetTokenRepository.save(resetToken);

        String link = "https://kronaguard-production.up.railway.app/redefinir-senha?token=" + token;
        String assunto = "Redefinição de senha - Krona Guard";
        String texto = "Olá, " + usuario.getNome() + "\n\n"
                + "Recebemos uma solicitação para redefinir a senha da sua conta Krona Guard.\n\n"
                + "Para criar uma nova senha, acesse:\n"
                + link + "\n\n"
                + "Este link é válido por 15 minutos e pode ser utilizado apenas uma vez.\n\n"
                + "Se você não solicitou a redefinição da senha, ignore este e-mail.\n\n"
                + "Atenciosamente,\n"
                + "Krona Guard";

        emailService.enviarEmail(email, assunto, texto);
    }
    public PasswordResetToken buscarToken(String token){
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Token inválido"));

        if(resetToken.getExpiracao().isBefore(LocalDateTime.now())){
            throw new IllegalArgumentException("Token expirado");
        }

        return resetToken;
    }

    @Transactional
    public void redefinirSenha(String token, String novaSenha){
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Token inválido"));

        if(resetToken.getExpiracao().isBefore(LocalDateTime.now())){
            throw new IllegalArgumentException("Token expirado");
        }

        Usuario usuario = resetToken.getUsuario();

        usuario.setSenha(passwordEncoder.encode(novaSenha));

        usuarioRepository.save(usuario);

        passwordResetTokenRepository.delete(resetToken);
    }
}
