package com.mariluz.sales.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class User {

    private String id;
    private String name;
    private String email;
    private String role;
}
