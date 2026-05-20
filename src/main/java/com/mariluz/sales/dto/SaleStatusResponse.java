package com.mariluz.sales.dto;

import com.mariluz.sales.model.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class SaleStatusResponse {

    private Status status;
}
