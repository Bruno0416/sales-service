package com.mariluz.sales.exceptions;

public class InsufficientStockException extends RuntimeException {

    public InsufficientStockException() {
        super("Stock insuficiente para completar la compra.");
    }

    public InsufficientStockException(String message) {
        super(message);
    }
}
