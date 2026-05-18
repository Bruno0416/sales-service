package com.mariluz.sales.controller;

import com.mariluz.sales.dto.*;
import com.mariluz.sales.service.SalesService;
import jakarta.validation.Valid;
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

    @PostMapping("/create")
    public ResponseEntity<SaleResponse> createSale(
        @Valid @RequestBody SaleRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
            service.createSale(request)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<SaleResponse> getSaleById(
        @Valid @PathVariable Integer id
    ) {
        return ResponseEntity.ok(service.getSaleById(id));
    }
}
