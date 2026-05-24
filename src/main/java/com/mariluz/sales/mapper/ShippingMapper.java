package com.mariluz.sales.mapper;

import com.mariluz.sales.dto.shipping.ShippingAddressRequest;
import com.mariluz.sales.dto.user.DireccionResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ShippingMapper {
    // DireccionResponse → ShippingAddressRequest
    @Mapping(source = "comuna", target = "commune")
    @Mapping(source = "calle", target = "street")
    @Mapping(source = "numero", target = "number")
    @Mapping(target = "reference", ignore = true)
    ShippingAddressRequest toShippingAddressRequest(
        DireccionResponse direccion
    );

    // ShippingAddressRequest → DireccionResponse
    @Mapping(source = "commune", target = "comuna")
    @Mapping(source = "street", target = "calle")
    @Mapping(source = "number", target = "numero")
    @Mapping(target = "id", ignore = true)
    DireccionResponse toDireccionResponse(ShippingAddressRequest address);
}
