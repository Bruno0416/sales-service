package com.mariluz.sales.dto;

import com.mariluz.sales.model.Status;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class SaleResponse {

    private Integer id;

    private Integer total;

    private Status status;

    private LocalDateTime createdAt;

    private List<SaleItemResponse> products;
}
