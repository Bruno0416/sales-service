package com.mariluz.sales.client;

import com.mariluz.sales.dto.user.DireccionResponse;
import com.mariluz.sales.exceptions.CouldNotFindUserAddressException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class UserClient {

    private final RestClient restClient;

    public UserClient(@Value("${user.service.url}") String baseUrl) {
        this.restClient = RestClient.builder().baseUrl(baseUrl).build();
    }

    public DireccionResponse getDireccionById(String authHeader) {
        try {
            return restClient
                .get()
                .uri("/direccion")
                .header("Authorization", authHeader)
                .retrieve()
                .body(DireccionResponse.class);
        } catch (Exception e) {
            throw new CouldNotFindUserAddressException();
        }
    }
}
