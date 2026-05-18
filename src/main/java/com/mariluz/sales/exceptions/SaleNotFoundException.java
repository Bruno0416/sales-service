package com.mariluz.sales.exceptions;

public class SaleNotFoundException extends RuntimeException {

    public SaleNotFoundException() {
        super("No se encontro la venta con el id especificado.");
    }

    public SaleNotFoundException(String message) {
        super(message);
    }
}
