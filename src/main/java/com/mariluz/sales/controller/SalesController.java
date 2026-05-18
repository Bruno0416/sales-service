package com.mariluz.sales.controller;

import com.mariluz.sales.dto.*;
import com.mariluz.sales.service.SalesService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
    public SaleResponse createSale(@Valid @RequestBody SaleRequest request) {
        return service.createSale(request);
    }
}
