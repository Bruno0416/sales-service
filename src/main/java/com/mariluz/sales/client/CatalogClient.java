package com.mariluz.sales.client;

import com.mariluz.sales.dto.catalog.CatalogRequest;
import com.mariluz.sales.dto.catalog.GetProductsResponse;
import com.mariluz.sales.dto.catalog.UpdateStockRequest;
import com.mariluz.sales.exceptions.CouldNotUpdateStockException;
import com.mariluz.sales.exceptions.ProductsNotFoundException;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Service
public class CatalogClient {

    private final RestClient restClient;

    // URL del servicio de catalogo inyectada desde application.properties
    public CatalogClient(@Value("${catalog.service.url}") String baseUrl) {
        this.restClient = RestClient.builder().baseUrl(baseUrl).build();
    }

    public GetProductsResponse findProducts(
        List<Integer> ids,
        String authHeader
    ) throws ProductsNotFoundException {
        CatalogRequest content = CatalogRequest.builder().ids(ids).build();
        try {
            return restClient
                .post()
                .uri("/products/ids")
                .header("Authorization", authHeader)
                .body(content)
                .retrieve()
                .body(GetProductsResponse.class);
        } catch (RestClientException e) {
            // error al comunicarse con el servicio de catalogo
            throw new ProductsNotFoundException(
                "No se pudo obtener los productos del catalogo."
            );
        }
    }

    public void updateStock(Integer id, Integer quantity, String authHeader)
        throws CouldNotUpdateStockException {
        UpdateStockRequest request = UpdateStockRequest.builder()
            .id(id)
            .quantity(quantity)
            .build();
        try {
            restClient
                .put()
                .uri("/update-stock")
                .header("Authorization", authHeader)
                .body(request)
                .retrieve()
                .toBodilessEntity();
        } catch (RestClientException e) {
            // error al comunicarse con el servicio de catalogo
            throw new CouldNotUpdateStockException(
                "No se pudo actualizar el stock del producto: " + id
            );
        }
    }
}
