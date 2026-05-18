package com.mariluz.sales.dto.catalog;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
public class ProductResponse {

    private Integer id;

    private String name;

    private Integer price;

    private Integer quantity;
}
