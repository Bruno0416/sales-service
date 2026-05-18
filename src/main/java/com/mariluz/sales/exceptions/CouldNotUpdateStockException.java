package com.mariluz.sales.exceptions;

public class CouldNotUpdateStockException extends RuntimeException {

    public CouldNotUpdateStockException(String message) {
        super(message);
    }
}
