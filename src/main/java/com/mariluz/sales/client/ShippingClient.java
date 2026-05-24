package com.mariluz.sales.client;

import com.mariluz.sales.dto.shipping.CreateShipmentRequest;
import com.mariluz.sales.dto.user.DireccionResponse;
import com.mariluz.sales.exceptions.CouldNotCancelShippingOrderException;
import com.mariluz.sales.exceptions.CouldNotCreateShippingOrderException;
import com.mariluz.sales.mapper.ShippingMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class ShippingClient {

    private final RestClient restClient;

    private final UserClient userClient;

    private final ShippingMapper mapper;

    public ShippingClient(
        @Value("${shipping.service.url}") String baseUrl,
        UserClient userClient,
        ShippingMapper mapper
    ) {
        this.restClient = RestClient.builder().baseUrl(baseUrl).build();

        this.userClient = userClient;

        this.mapper = mapper;
    }

    public void createShippingOrder(Integer saleId, String authHeader) {
        try {
            // 1. llamar a UserClient para obtener la direccion del usuario
            DireccionResponse direccion = userClient.getDireccionById(
                authHeader
            );
            // 2. crear el dto con la direccion y el mapper
            CreateShipmentRequest request = CreateShipmentRequest.builder()
                .saleId(saleId)
                .address(mapper.toShippingAddressRequest(direccion))
                .build();
            // 3. generar la orden de envio y capturar error
            restClient
                .post()
                .uri("/create")
                .header("Authorization", authHeader)
                .body(request)
                .retrieve()
                .toBodilessEntity(); // solo obtenemos el codigo 201
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            throw new CouldNotCreateShippingOrderException(e.getMessage());
        }
    }

    public void cancelShippingOrder(Integer saleId, String authHeader) {
        try {
            restClient
                .put()
                .uri("/cancel/{saleId}", saleId)
                .header("Authorization", authHeader)
                .retrieve()
                .toBodilessEntity(); // obtenemos unicamente el codigo 204
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            throw new CouldNotCancelShippingOrderException(e.getMessage());
        }
    }
}
