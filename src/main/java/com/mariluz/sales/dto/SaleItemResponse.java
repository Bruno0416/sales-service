package com.mariluz.sales.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
public class SaleItemResponse {

    private Integer id;

    private Integer quantity;

    private Integer subTotal;
}
