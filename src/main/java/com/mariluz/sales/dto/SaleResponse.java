package com.mariluz.sales.dto;

import com.mariluz.sales.model.Status;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
public class SaleResponse {

    private Integer id;

    private Integer total;

    private Status status;

    private LocalDateTime createdAt;

    private List<SaleItemResponse> products;
}
