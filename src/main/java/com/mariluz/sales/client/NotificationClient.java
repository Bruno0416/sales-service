/*
    NOTA: Ya que el servicio de resend no tiene
    configurado un dominio (correo oficial de empresa/tienda)
    el correo unicamente puede ser enviado al duenio de la api(brunomcalderonv@gmail.com)
    -> por lo que cualquier intento de enviar correo a otra direccion va a dar error
*/

package com.mariluz.sales.client;

import com.mariluz.sales.dto.notification.NotificationRequest;
import com.mariluz.sales.exceptions.SendNotificationException;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Service
public class NotificationClient {

    private final RestClient restClient;

    // URL del servicio de notificaciones inyectada desde application.properties
    public NotificationClient(
        @Value("${notification.service.url}") String baseUrl
    ) {
        this.restClient = RestClient.builder()
            .baseUrl(baseUrl)
            .build();
    }

    public void sendPurchaseEmail(String email, String title, List<String> products)
        throws SendNotificationException {
        NotificationRequest content = NotificationRequest.builder()
            .email(email).title(title).products(products).build();

        try {
            restClient.post()
                .uri("/purchase")
                .body(content)
                .retrieve()
                .toBodilessEntity();
        } catch (RestClientException e) {
            // error al comunicarse con el servicio de notificaciones
            throw new SendNotificationException(
                "No se pudo enviar la notificacion al correo: " + email
            );
        }
    }
}
