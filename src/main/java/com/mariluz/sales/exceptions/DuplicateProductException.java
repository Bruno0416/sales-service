package com.mariluz.sales.exceptions;

public class DuplicateProductException extends RuntimeException {

    public DuplicateProductException() {
        super("No se permiten productos duplicados en la misma orden.");
    }

    public DuplicateProductException(String message) {
        super(message);
    }
}
