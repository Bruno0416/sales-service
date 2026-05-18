package com.mariluz.sales.exceptions;

public class ProductsNotFoundException extends RuntimeException {

    public ProductsNotFoundException() {
        super(
            "Lo sentimos, no se encontraron productos disponibles en este momento."
        );
    }

    public ProductsNotFoundException(String message) {
        super(message);
    }
}
