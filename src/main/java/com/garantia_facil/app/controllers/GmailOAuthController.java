package com.garantia_facil.app.controllers;

import com.google.api.client.auth.oauth2.AuthorizationCodeRequestUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

@RestController
public class GmailOAuthController {

    private static final String REDIRECT_URI =
            "https://kronaguard-production.up.railway.app/oauth2/callback";

    private static final String SCOPE =
            "https://www.googleapis.com/auth/gmail.send";

    @Value("${google.client-id}")
    private String clientId;

    @Value("${google.client-secret}")
    private String clientSecret;


    @GetMapping("/oauth2/autorizar")
    public String autorizar() throws Exception {

        GoogleAuthorizationCodeFlow flow =
                new GoogleAuthorizationCodeFlow.Builder(
                        GoogleNetHttpTransport.newTrustedTransport(),
                        GsonFactory.getDefaultInstance(),
                        clientId,
                        clientSecret,
                        Collections.singleton(SCOPE)
                )
                        .setAccessType("offline")
                        .build();

        AuthorizationCodeRequestUrl authorizationUrl =
                flow.newAuthorizationUrl()
                        .setRedirectUri(REDIRECT_URI)
                        .setAccessType("offline")
                        .set("prompt", "consent");

        return """
                <html>
                <body>
                    <h2>Autorizar Gmail</h2>

                    <a href="%s">
                        Clique aqui para autorizar
                    </a>

                </body>
                </html>
                """.formatted(authorizationUrl.build());
    }


    @GetMapping("/oauth2/callback")
    public String callback(@RequestParam("code") String code) throws Exception {

        GoogleAuthorizationCodeFlow flow =
                new GoogleAuthorizationCodeFlow.Builder(
                        GoogleNetHttpTransport.newTrustedTransport(),
                        GsonFactory.getDefaultInstance(),
                        clientId,
                        clientSecret,
                        Collections.singleton(SCOPE)
                )
                        .setAccessType("offline")
                        .build();

        GoogleTokenResponse tokenResponse =
                flow.newTokenRequest(code)
                        .setRedirectUri(REDIRECT_URI)
                        .execute();

        String refreshToken = tokenResponse.getRefreshToken();

        System.out.println("=================================");
        System.out.println("REFRESH TOKEN:");
        System.out.println(refreshToken);
        System.out.println("=================================");

        return "Autorização concluída. Verifique os logs do Railway.";
    }
}