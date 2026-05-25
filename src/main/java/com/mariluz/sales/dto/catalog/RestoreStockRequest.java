package com.mariluz.sales.dto.catalog;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RestoreStockRequest {

    private Integer id;

    private Integer quantity;
}
