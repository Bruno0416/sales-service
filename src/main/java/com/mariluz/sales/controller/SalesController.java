package com.mariluz.sales.controller;

import com.mariluz.sales.dto.*;
import com.mariluz.sales.service.SalesService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sales")
@RequiredArgsConstructor
public class SalesController {

    private final SalesService service;

    // 1. generar venta
    @PostMapping("/create")
    public ResponseEntity<SaleResponse> createSale(
        @Valid @RequestBody SaleRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
            service.createSale(request)
        );
    }

    // 2. buscar venta por saleId (usuario)
    @GetMapping("/{id}")
    public ResponseEntity<SaleResponse> getSaleById(
        @Valid @PathVariable Integer id
    ) {
        return ResponseEntity.ok(service.getSaleById(id));
    }

    // 3. ver estado venta
    @GetMapping("/status/{saleId}")
    public ResponseEntity<SaleStatusResponse> getStatusBySaleId(
        @Valid @PathVariable Integer saleId
    ) {
        return ResponseEntity.ok(service.getStatusBySaleId(saleId));
    }

    // 4. listar ventas (admin)
    @GetMapping("/all")
    public ResponseEntity<List<SaleResponse>> getAllSales() {
        return ResponseEntity.ok(service.getAllSales());
    }

    // 5. buscar mis compras (por usuario)
    // 6. cancelar venta(idVenta)
}
