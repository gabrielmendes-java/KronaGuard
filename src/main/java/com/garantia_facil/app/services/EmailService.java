package com.garantia_facil.app.services;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private final JavaMailSender javaMailSender;

    public EmailService(JavaMailSender javaMailSender){
        this.javaMailSender = javaMailSender;
    }

    public void enviarEmail(String email, String assunto, String mensagem){
        System.out.println("INICIANDO ENVIO DO EMAIL");
        SimpleMailMessage mailMessage = new SimpleMailMessage();

        mailMessage.setTo(email);
        mailMessage.setSubject(assunto);
        mailMessage.setText(mensagem);

        javaMailSender.send(mailMessage);
        System.out.println("EMAIL ENVIADO COM SUCESSO");
    }
}
