package com.mariluz.sales.exceptions;

public class CouldNotCancelSaleException extends RuntimeException {

    public CouldNotCancelSaleException() {
        super(
            "No se pudo cancelar la venta, no tiene permisos para eliminar la venta de otro usuario."
        );
    }

    public CouldNotCancelSaleException(String message) {
        super(message);
    }
}
