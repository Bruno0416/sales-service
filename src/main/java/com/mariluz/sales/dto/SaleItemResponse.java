package com.mariluz.sales.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class SaleItemResponse {

    private Integer id;

    private Integer productId;

    private Integer quantity;

    private Integer subTotal;
}
