package com.officedubac.project.notification;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;
import java.util.Map;

// Envoi de SMS via l'API Yas (opérateur sénégalais). N'interrompt jamais l'appelant en cas
// d'échec : une notification manquée ne doit pas bloquer la création/validation d'une EB.
@Slf4j
@Service
public class YasSmsService {

    @Value("${api.yas.base-url}")
    private String baseUrl;

    @Value("${api.yas.username}")
    private String username;

    @Value("${api.yas.password}")
    private String password;

    @Value("${api.yas.from}")
    private String from;

    @Value("${api.yas.enabled:true}")
    private boolean enabled;

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    private final ObjectMapper objectMapper = new ObjectMapper();

    public void envoyerNotificationValidation(String numeroDestinataire) {
        envoyerSms(numeroDestinataire,
                "Office du Bac : une expression de besoin est en attente de votre validation sur Sotina.");
    }

    private void envoyerSms(String numero, String texte) {
        if (!enabled) {
            log.warn("SMS désactivé, message non envoyé à {}", numero);
            return;
        }
        if (numero == null || numero.isBlank()) {
            log.warn("⚠️ SMS ignoré : numéro de téléphone manquant");
            return;
        }

        String to = formaterNumero(numero);
        try {
            String body = objectMapper.writeValueAsString(Map.of("from", from, "to", to, "text", texte));
            String auth = Base64.getEncoder().encodeToString((username + ":" + password).getBytes(StandardCharsets.UTF_8));

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl))
                    .header("Authorization", "Basic " + auth)
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .timeout(Duration.ofSeconds(10))
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200 || response.statusCode() == 201) {
                log.info("📱 SMS envoyé à {}", to);
            } else {
                log.warn("⚠️ Échec envoi SMS à {} — HTTP {} — {}", to, response.statusCode(), response.body());
            }
        } catch (Exception e) {
            log.warn("⚠️ Erreur envoi SMS à {} : {}", to, e.getMessage());
        }
    }

    private String formaterNumero(String numero) {
        String chiffres = numero.replaceAll("[^0-9]", "");
        if (chiffres.startsWith("0")) return "221" + chiffres.substring(1);
        if (chiffres.length() == 9) return "221" + chiffres;
        return chiffres;
    }
}
