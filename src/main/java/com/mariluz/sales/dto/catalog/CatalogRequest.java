package com.mariluz.sales.dto.catalog;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
public class CatalogRequest {

    List<Integer> ids;
}
