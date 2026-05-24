package com.mariluz.sales.dto.user;

import lombok.Data;

@Data
public class DireccionResponse {

    private Integer id;

    private String region;

    private String comuna;

    private String calle;

    private String numero;
}
