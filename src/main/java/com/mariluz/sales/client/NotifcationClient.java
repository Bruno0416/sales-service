/*
    NOTA: Ya que el servicio de resend no tiene
    configurado un dominio (correo oficial de empresa/tienda)
    el correo unicamente puede ser enviado al duenio de la api(brunomcalderonv@gmail.com)
    -> por lo que cualquier intento de enviar correo a otra direccion va a  dar error
*/

package com.mariluz.sales.client;

import com.mariluz.sales.dto.notification.NotificationRequest;
import com.mariluz.sales.exceptions.SendNotificationException;
import java.util.List;
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

    public void sendPurchaseEmail(
        String email,
        String title,
        List<String> products
    ) throws SendNotificationException {
        // Crear contenido para la solicitud
        NotificationRequest content = NotificationRequest.builder()
            .email(email)
            .title(title)
            .products(products)
            .build();

        restClient
            .post()
            .uri("/purchase")
            .body(content)
            .retrieve()
            .toBodilessEntity(); // Retorna la respuesta sin cuerpo (solo el status 200)
    }
}
