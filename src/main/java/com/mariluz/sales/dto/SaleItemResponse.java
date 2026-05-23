package com.mariluz.sales.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class SaleItemResponse {

    private Integer id;

    private Integer productId;

    private Integer quantity;

    private Integer subTotal;
}
