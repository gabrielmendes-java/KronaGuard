package com.garantia_facil.app.services;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;
import jakarta.mail.Session;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.Properties;

@Service
public class EmailService {

    @Value("${google.client-id}")
    private String clientId;

    @Value("${google.client-secret}")
    private String clientSecret;

    @Value("${google.refresh-token}")
    private String refreshToken;

    @Value("${google.email}")
    private String googleEmail;


    public void enviarEmail(String email, String assunto, String mensagem) {
        try {
            System.out.println("INICIANDO ENVIO DO EMAIL");

            GoogleCredential credential = new GoogleCredential.Builder()
                    .setTransport(GoogleNetHttpTransport.newTrustedTransport())
                    .setJsonFactory(GsonFactory.getDefaultInstance())
                    .setClientSecrets(clientId, clientSecret)
                    .build()
                    .setRefreshToken(refreshToken);

            credential.refreshToken();

            Gmail gmail = new Gmail.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(),
                    GsonFactory.getDefaultInstance(),
                    credential
            )
                    .setApplicationName("Krona Guard")
                    .build();


            Properties properties = new Properties();

            Session session = Session.getInstance(properties);

            MimeMessage mimeMessage = new MimeMessage(session);

            mimeMessage.setFrom(new InternetAddress(googleEmail));
            mimeMessage.setRecipients(
                    jakarta.mail.Message.RecipientType.TO,
                    InternetAddress.parse(email)
            );
            mimeMessage.setSubject(assunto, "UTF-8");
            mimeMessage.setText(mensagem, "UTF-8");


            ByteArrayOutputStream buffer = new ByteArrayOutputStream();

            mimeMessage.writeTo(buffer);

            String encodedEmail = Base64.getUrlEncoder()
                    .withoutPadding()
                    .encodeToString(buffer.toByteArray());


            Message message = new Message();
            message.setRaw(encodedEmail);


            gmail.users()
                    .messages()
                    .send("me", message)
                    .execute();

            System.out.println("EMAIL ENVIADO COM SUCESSO");

        } catch (Exception e) {

            System.out.println("ERRO AO ENVIAR EMAIL:");
            e.printStackTrace();

            throw new RuntimeException("Não foi possível enviar o email.", e);
        }
    }
}