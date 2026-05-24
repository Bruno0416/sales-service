package com.mariluz.sales.exceptions;

public class CouldNotCancelShippingOrderException extends RuntimeException {

    public CouldNotCancelShippingOrderException() {
        super("No se pudo cancelar el envío");
    }

    public CouldNotCancelShippingOrderException(String message) {
        super(message);
    }
}
