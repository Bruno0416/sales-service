package com.mariluz.sales.dto;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
public class SaleRequest {

    @NotEmpty(message = "La lista de productos no puede estar vacia")
    private List<SaleItemRequest> products;
}
