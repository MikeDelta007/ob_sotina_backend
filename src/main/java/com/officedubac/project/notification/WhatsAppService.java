package com.officedubac.project.notification;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * Envoi de notifications WhatsApp via l'API Cloud de Meta (template pré-approuvé,
 * requis pour tout message initié par l'entreprise en dehors d'une conversation
 * ouverte par le destinataire).
 */
@Slf4j
@Service
public class WhatsAppService {

    @Value("${whatsapp.api.version}")
    private String apiVersion;

    @Value("${whatsapp.api.phone-number-id}")
    private String phoneNumberId;

    @Value("${whatsapp.api.access-token}")
    private String accessToken;

    @Value("${whatsapp.api.template-name}")
    private String templateName;

    @Value("${whatsapp.api.template-lang}")
    private String templateLang;

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    // Envoie le template de notification au numéro donné (format international
    // sans "+", ex. 221771234567). N'interrompt jamais l'appelant en cas d'échec :
    // une notification manquée ne doit pas bloquer la création/validation d'une EB.
    public void envoyerNotificationValidation(String numeroDestinataire) {
        if (numeroDestinataire == null || numeroDestinataire.isBlank()) {
            log.warn("⚠️ Notification WhatsApp ignorée : numéro de téléphone manquant");
            return;
        }

        String numero = numeroDestinataire.replaceAll("[^0-9]", "");
        String url = "https://graph.facebook.com/" + apiVersion + "/" + phoneNumberId + "/messages";
        String body = """
                {
                  "messaging_product": "whatsapp",
                  "to": "%s",
                  "type": "template",
                  "template": { "name": "%s", "language": { "code": "%s" } }
                }
                """.formatted(numero, templateName, templateLang);

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Authorization", "Bearer " + accessToken)
                    .header("Content-Type", "application/json")
                    .timeout(Duration.ofSeconds(10))
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                log.info("📲 Notification WhatsApp envoyée à {}", numero);
            } else {
                log.warn("⚠️ Échec envoi WhatsApp à {} — HTTP {} — {}", numero, response.statusCode(), response.body());
            }
        } catch (Exception e) {
            log.warn("⚠️ Erreur envoi WhatsApp à {} : {}", numero, e.getMessage());
        }
    }
}
