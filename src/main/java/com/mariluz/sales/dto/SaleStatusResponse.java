package com.mariluz.sales.dto;

import com.mariluz.sales.model.Status;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class SaleStatusResponse {

    private Status status;
}
