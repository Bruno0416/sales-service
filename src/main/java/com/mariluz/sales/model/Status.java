package com.mariluz.sales.model;

public enum Status {
    PENDING, // estado inicial
    COMPLETED, // estado al momento de confirmar la existencia del producto y al guardar el producto en la bd
    REJECTED, // estado en caso de que el producto no exista
    CANCELLED, // estado en caso de que la venta sea cancelada por el cliente
}
