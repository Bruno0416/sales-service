package com.mariluz.sales.client;

import com.mariluz.sales.dto.catalog.CatalogRequest;
import com.mariluz.sales.dto.catalog.GetProductsResponse;
import com.mariluz.sales.dto.catalog.UpdateStockRequest;
import com.mariluz.sales.exceptions.CouldNotUpdateStockException;
import com.mariluz.sales.exceptions.ProductsNotFoundException;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class CatalogClient {

    private final RestClient restClient;

    public CatalogClient() {
        this.restClient = RestClient.builder()
            .baseUrl("http://localhost:8084/catalog")
            .build();
    }

    public GetProductsResponse findProducts(
        List<Integer> ids,
        String authHeader
    ) throws ProductsNotFoundException {
        // 1. crear el contenido de la solicitud
        CatalogRequest content = CatalogRequest.builder().ids(ids).build();

        // 2. enviar la solicitud y obtener la respuesta
        return restClient
            .post()
            .uri("/products/ids")
            .header("Authorization", authHeader)
            .body(content)
            .retrieve()
            .body(GetProductsResponse.class);
    }

    public void updateStock(Integer id, Integer quantity, String authHeader)
        throws CouldNotUpdateStockException {
        // 1. crear contenido de la solicitud
        UpdateStockRequest request = UpdateStockRequest.builder()
            .id(id)
            .quantity(quantity)
            .build();
        // 2. enviar, obtener respuesta
        restClient
            .put()
            .uri("/update-stock")
            .header("Authorization", authHeader)
            .body(request)
            .retrieve()
            .toBodilessEntity();
    }
}
