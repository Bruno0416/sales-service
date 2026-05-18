package com.mariluz.sales.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
public class SaleItemRequest {

    @NotNull(message = "El id del producto no puede ser estar vacio")
    private Integer id;

    @NotNull(message = "La cantidad no puede ser estar vacia")
    private Integer quantity;
}
