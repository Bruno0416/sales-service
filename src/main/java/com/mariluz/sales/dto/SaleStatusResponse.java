package com.mariluz.sales.dto;

import com.mariluz.sales.model.Status;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SaleStatusResponse {

    private Status status;
}
