package com.mariluz.sales.controller;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.mariluz.sales.dto.SaleItemRequest;
import com.mariluz.sales.dto.SaleItemResponse;
import com.mariluz.sales.dto.SaleRequest;
import com.mariluz.sales.dto.SaleResponse;
import com.mariluz.sales.dto.SaleStatusResponse;
import com.mariluz.sales.exceptions.CouldNotCancelSaleException;
import com.mariluz.sales.exceptions.CouldNotCancelShippingOrderException;
import com.mariluz.sales.exceptions.CouldNotCreateShippingOrderException;
import com.mariluz.sales.exceptions.CouldNotRestoreStockException;
import com.mariluz.sales.exceptions.CouldNotUpdateStockException;
import com.mariluz.sales.exceptions.DuplicateProductException;
import com.mariluz.sales.exceptions.InsufficientStockException;
import com.mariluz.sales.exceptions.ProductsNotFoundException;
import com.mariluz.sales.exceptions.SaleNotFoundException;
import com.mariluz.sales.exceptions.UnauthorizedOperationException;
import com.mariluz.sales.model.Status;
import com.mariluz.sales.security.JwtUtil;
import com.mariluz.sales.service.SalesService;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.json.JsonMapper;

@WebMvcTest(SalesController.class)
@AutoConfigureMockMvc(addFilters = false) // desactiva filtro JWT y seguridad para ejecutar el test
public class SalesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JsonMapper objectMapper; // para mapear objetos/clases a json

    @MockitoBean
    private SalesService service;

    @MockitoBean
    private JwtUtil jwtUtil; // importante para que funcione el service

    // -------------- 1. CREATE SALE --------------

    // 201
    @Test
    public void testCreateSale() throws Exception {
        // 1. preparar request
        SaleItemRequest item = new SaleItemRequest();
        item.setId(1);
        item.setQuantity(2);

        SaleRequest request = new SaleRequest();
        request.setProducts(List.of(item));

        // 2. preparar respuesta
        SaleItemResponse itemResponse = SaleItemResponse.builder()
            .id(1)
            .productId(1)
            .quantity(2)
            .subTotal(200)
            .build();

        SaleResponse response = SaleResponse.builder()
            .id(1)
            .total(200)
            .status(Status.COMPLETED)
            .createdAt(LocalDateTime.now())
            .products(List.of(itemResponse))
            .build();

        // 3. configurar comportamiento del service
        when(service.createSale(request)).thenReturn(response);

        // 4. ejecutar request
        mockMvc
            .perform(
                post("/sales/create")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isCreated());
    }

    // 400
    @Test
    public void testCreateSaleEmptyProducts() throws Exception {
        // 1. preparar request con lista vacía
        SaleRequest request = new SaleRequest();
        request.setProducts(List.of());

        // 2. ejecutar request
        mockMvc
            .perform(
                post("/sales/create")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isBadRequest());
    }

    // 400
    @Test
    public void testCreateSaleInvalidProductFields() throws Exception {
        // 1. preparar request con campos inválidos
        SaleItemRequest item = new SaleItemRequest();
        item.setId(-1);
        item.setQuantity(-1);

        SaleRequest request = new SaleRequest();
        request.setProducts(List.of(item));

        // 2. ejecutar request
        mockMvc
            .perform(
                post("/sales/create")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isBadRequest());
    }

    // 400
    @Test
    public void testCreateSaleDuplicateProducts() throws Exception {
        // 1. preparar request
        SaleItemRequest item = new SaleItemRequest();
        item.setId(1);
        item.setQuantity(1);

        SaleRequest request = new SaleRequest();
        request.setProducts(List.of(item));

        // 2. configurar comportamiento del service
        when(service.createSale(request)).thenThrow(
            new DuplicateProductException("")
        );

        // 3. ejecutar request
        mockMvc
            .perform(
                post("/sales/create")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isBadRequest());
    }

    // 404
    @Test
    public void testCreateSaleProductsNotFound() throws Exception {
        // 1. preparar request
        SaleItemRequest item = new SaleItemRequest();
        item.setId(1);
        item.setQuantity(1);

        SaleRequest request = new SaleRequest();
        request.setProducts(List.of(item));

        // 2. configurar comportamiento del service
        when(service.createSale(request)).thenThrow(
            new ProductsNotFoundException("")
        );

        // 3. ejecutar request
        mockMvc
            .perform(
                post("/sales/create")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isNotFound());
    }

    // 409
    @Test
    public void testCreateSaleInsufficientStock() throws Exception {
        // 1. preparar request
        SaleItemRequest item = new SaleItemRequest();
        item.setId(1);
        item.setQuantity(1);

        SaleRequest request = new SaleRequest();
        request.setProducts(List.of(item));

        // 2. configurar comportamiento del service
        when(service.createSale(request)).thenThrow(
            new InsufficientStockException("")
        );

        // 3. ejecutar request
        mockMvc
            .perform(
                post("/sales/create")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isConflict());
    }

    // 500
    @Test
    public void testCreateSaleCouldNotUpdateStock() throws Exception {
        // 1. preparar request
        SaleItemRequest item = new SaleItemRequest();
        item.setId(1);
        item.setQuantity(1);

        SaleRequest request = new SaleRequest();
        request.setProducts(List.of(item));

        // 2. configurar comportamiento del service
        when(service.createSale(request)).thenThrow(
            new CouldNotUpdateStockException("")
        );

        // 3. ejecutar request
        mockMvc
            .perform(
                post("/sales/create")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isInternalServerError());
    }

    // 500
    @Test
    public void testCreateSaleCouldNotCreateShipping() throws Exception {
        // 1. preparar request
        SaleItemRequest item = new SaleItemRequest();
        item.setId(1);
        item.setQuantity(1);

        SaleRequest request = new SaleRequest();
        request.setProducts(List.of(item));

        // 2. configurar comportamiento del service
        when(service.createSale(request)).thenThrow(
            new CouldNotCreateShippingOrderException("")
        );

        // 3. ejecutar request
        mockMvc
            .perform(
                post("/sales/create")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isInternalServerError());
    }

    // -------------- 2. GET SALE BY ID --------------

    // 200
    @Test
    public void testGetSaleById() throws Exception {
        // 1. preparar respuesta
        SaleResponse response = SaleResponse.builder()
            .id(1)
            .total(100)
            .status(Status.COMPLETED)
            .createdAt(LocalDateTime.now())
            .products(List.of())
            .build();

        // 2. configurar comportamiento del service
        when(service.getSaleById(1)).thenReturn(response);

        // 3. ejecutar request
        mockMvc.perform(get("/sales/1")).andExpect(status().isOk());
    }

    // 400
    @Test
    public void testGetSaleByIdInvalidId() throws Exception {
        mockMvc.perform(get("/sales/-1")).andExpect(status().isBadRequest());
    }

    // 404
    @Test
    public void testGetSaleByIdNotFound() throws Exception {
        when(service.getSaleById(99)).thenThrow(new SaleNotFoundException(""));

        mockMvc.perform(get("/sales/99")).andExpect(status().isNotFound());
    }

    // -------------- 3. GET STATUS BY SALE ID --------------

    // 200
    @Test
    public void testGetStatusBySaleId() throws Exception {
        // 1. preparar respuesta
        SaleStatusResponse response = SaleStatusResponse.builder()
            .status(Status.COMPLETED)
            .build();

        // 2. configurar comportamiento del service
        when(service.getStatusBySaleId(1)).thenReturn(response);

        // 3. ejecutar request
        mockMvc.perform(get("/sales/status/1")).andExpect(status().isOk());
    }

    // 400
    @Test
    public void testGetStatusBySaleIdInvalidId() throws Exception {
        mockMvc
            .perform(get("/sales/status/-1"))
            .andExpect(status().isBadRequest());
    }

    // 404
    @Test
    public void testGetStatusBySaleIdNotFound() throws Exception {
        when(service.getStatusBySaleId(99)).thenThrow(
            new SaleNotFoundException("")
        );

        mockMvc
            .perform(get("/sales/status/99"))
            .andExpect(status().isNotFound());
    }

    // -------------- 4. GET ALL SALES --------------

    // 200
    @Test
    public void testGetAllSales() throws Exception {
        // 1. preparar respuesta
        SaleResponse response = SaleResponse.builder()
            .id(1)
            .total(100)
            .status(Status.COMPLETED)
            .createdAt(LocalDateTime.now())
            .products(List.of())
            .build();

        // 2. configurar comportamiento del service
        when(service.getAllSales()).thenReturn(List.of(response));

        // 3. ejecutar request
        mockMvc.perform(get("/sales/all")).andExpect(status().isOk());
    }

    // 403
    @Test
    public void testGetAllSalesUnauthorized() throws Exception {
        when(service.getAllSales()).thenThrow(
            new UnauthorizedOperationException("")
        );

        mockMvc.perform(get("/sales/all")).andExpect(status().isForbidden());
    }

    // -------------- 5. GET USER SALES --------------

    // 200
    @Test
    public void testGetSalesByUserId() throws Exception {
        // 1. preparar respuesta
        SaleResponse response = SaleResponse.builder()
            .id(1)
            .total(100)
            .status(Status.COMPLETED)
            .createdAt(LocalDateTime.now())
            .products(List.of())
            .build();

        // 2. configurar comportamiento del service
        when(service.getSalesByUserId()).thenReturn(List.of(response));

        // 3. ejecutar request
        mockMvc.perform(get("/sales/user-sales")).andExpect(status().isOk());
    }

    // -------------- 6. CANCEL SALE --------------

    // 204
    @Test
    public void testCancelSale() throws Exception {
        mockMvc
            .perform(put("/sales/cancel/1"))
            .andExpect(status().isNoContent());
    }

    // 400
    @Test
    public void testCancelSaleInvalidId() throws Exception {
        mockMvc
            .perform(put("/sales/cancel/-1"))
            .andExpect(status().isBadRequest());
    }

    // 404
    @Test
    public void testCancelSaleNotFound() throws Exception {
        doThrow(new SaleNotFoundException("")).when(service).cancelSale(99);

        mockMvc
            .perform(put("/sales/cancel/99"))
            .andExpect(status().isNotFound());
    }

    // 409
    @Test
    public void testCancelSaleConflict() throws Exception {
        doThrow(new CouldNotCancelSaleException(""))
            .when(service)
            .cancelSale(2);

        mockMvc
            .perform(put("/sales/cancel/2"))
            .andExpect(status().isConflict());
    }

    // 500
    @Test
    public void testCancelSaleCouldNotCancelShipping() throws Exception {
        doThrow(new CouldNotCancelShippingOrderException(""))
            .when(service)
            .cancelSale(3);

        mockMvc
            .perform(put("/sales/cancel/3"))
            .andExpect(status().isInternalServerError());
    }

    // 500
    @Test
    public void testCancelSaleCouldNotRestoreStock() throws Exception {
        doThrow(new CouldNotRestoreStockException(""))
            .when(service)
            .cancelSale(4);

        mockMvc
            .perform(put("/sales/cancel/4"))
            .andExpect(status().isInternalServerError());
    }
}
