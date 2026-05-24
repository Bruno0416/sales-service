package com.mariluz.sales.mapper;

import com.mariluz.sales.dto.SaleItemResponse;
import com.mariluz.sales.dto.SaleResponse;
import com.mariluz.sales.dto.SaleStatusResponse;
import com.mariluz.sales.model.Sale;
import com.mariluz.sales.model.SaleItem;
import com.mariluz.sales.model.Status;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SaleMapper {
    // SaleItem → SaleItemResponse, ignora: sale, unitPrice
    SaleItemResponse toItemResponse(SaleItem item);

    // Sale → SaleResponse, items → products (lista anidada resuelta por toItemResponse)
    @Mapping(source = "items", target = "products")
    SaleResponse toResponse(Sale sale);

    // List<Sale> → List<SaleResponse>, delega a toResponse por cada elemento
    List<SaleResponse> toResponseList(List<Sale> sales);

    // Status → SaleStatusResponse, wrap manual porque es enum a DTO de un solo campo
    default SaleStatusResponse toStatusResponse(Status status) {
        return SaleStatusResponse.builder().status(status).build();
    }
}
