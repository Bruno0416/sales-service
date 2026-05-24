package com.mariluz.sales.dto.shipping;

import lombok.Data;

@Data
public class ShippingAddressRequest {

    private String region;

    private String commune;

    private String street;

    private String number;

    private String reference;
}
