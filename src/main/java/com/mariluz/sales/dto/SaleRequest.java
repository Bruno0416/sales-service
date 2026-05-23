package com.mariluz.sales.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.Data;

@Data
public class SaleRequest {

    @Valid // Para validar los objetos anidados
    @NotEmpty(message = "La lista de productos no puede estar vacia")
    private List<SaleItemRequest> products;
}
