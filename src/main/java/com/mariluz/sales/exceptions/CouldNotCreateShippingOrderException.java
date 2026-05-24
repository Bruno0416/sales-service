package com.mariluz.sales.exceptions;

public class CouldNotCreateShippingOrderException extends RuntimeException {

    public CouldNotCreateShippingOrderException() {
        super("No se pudo crear el pedido de envío");
    }

    public CouldNotCreateShippingOrderException(String message) {
        super(message);
    }
}
