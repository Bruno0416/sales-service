package com.mariluz.sales.dto.catalog;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
public class UpdateStockRequest {

    private Integer id;

    private Integer quantity;
}
