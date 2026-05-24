package com.mariluz.sales.exceptions;

public class CouldNotFindUserAddressException extends RuntimeException {

    public CouldNotFindUserAddressException() {
        super("No se pudo encontrar la dirección del usuario");
    }

    public CouldNotFindUserAddressException(String message) {
        super(message);
    }
}
