package com.mariluz.sales.dto.shipping;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateShipmentRequest {

    private Integer saleId;

    private ShippingAddressRequest address;
}
