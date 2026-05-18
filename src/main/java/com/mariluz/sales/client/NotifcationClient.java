package com.mariluz.sales.client;

import com.mariluz.sales.dto.notification.NotificationRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class NotifcationClient {

    private final RestClient restClient;

    public NotifcationClient() {
        this.restClient = RestClient.builder()
            .baseUrl("http://localhost:8082/notifications")
            .build();
    }

    public void sendPurchaseEmail(String email, String title, String body) {
        // Crear contenido para la solicitud
        NotificationRequest content = NotificationRequest.builder()
            .email(email)
            .title(title)
            .body(body)
            .build();

        try {
            restClient
                .post()
                .uri("/purchase")
                .body(content)
                .retrieve()
                .toBodilessEntity(); // Retorna la respuesta sin cuerpo (solo el status 200)
        } catch (Exception e) {
            System.out.println(e.getMessage()); // TODO: arrojar exeption
        }
    }
}
