package com.mariluz.sales.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
public class SaleItemRequest {

    @NotNull(message = "El id del producto no puede ser estar vacio")
    @Positive(message = "El id del producto debe ser mayor a cero")
    private Integer id;

    @NotNull(message = "La cantidad no puede ser estar vacia")
    @Positive(message = "La cantidad debe ser mayor a cero")
    private Integer quantity;
}
