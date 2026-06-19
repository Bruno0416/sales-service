package com.mariluz.sales.controller;

import com.mariluz.sales.dto.ErrorResponse;
import com.mariluz.sales.dto.SaleRequest;
import com.mariluz.sales.dto.SaleResponse;
import com.mariluz.sales.dto.SaleStatusResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import java.util.List;
import org.springframework.http.ResponseEntity;

public interface SalesApi {
    // 1. generar venta
    @Operation(
        summary = "Generar venta",
        description = "Crea una nueva orden de venta validando el stock y registrando los productos solicitados."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "201",
            description = "Venta creada exitosamente."
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Error de validación en los datos de la solicitud.",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(
                    value = """
                    {
                        "endpoint": "/sales/create",
                        "errors": { "products": "no debe estar vacío" },
                        "message": "Error de validacion",
                        "status": 400,
                        "timeStamp": "2026-06-12T05:11:58"
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Token JWT inválido o expirado.",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(
                    value = """
                    {
                        "endpoint": "/sales/create",
                        "errors": { "error": "Se requiere token de autenticación" },
                        "message": "No autenticado",
                        "status": 401,
                        "timeStamp": "2026-06-12T05:11:58"
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Producto no encontrado.",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(
                    value = """
                    {
                        "endpoint": "/sales/create",
                        "errors": { "error": "Producto no encontrado: 1" },
                        "message": "Productos no encontrados",
                        "status": 404,
                        "timeStamp": "2026-06-12T05:11:58"
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Error interno del servidor.",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(
                    value = """
                    {
                        "endpoint": "/sales/create",
                        "errors": { "error": "No se pudo actualizar el stock del producto: 1" },
                        "message": "No se pudo actualizar el stock",
                        "status": 500,
                        "timeStamp": "2026-06-12T05:11:58"
                    }
                    """
                )
            )
        ),
    })
    ResponseEntity<SaleResponse> createSale(@Valid SaleRequest request);

    // 2. buscar venta por saleId (usuario)
    @Operation(
        summary = "Buscar venta por ID",
        description = "Obtiene los detalles completos de una venta específica perteneciente al usuario autenticado."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Venta encontrada."),
        @ApiResponse(
            responseCode = "400",
            description = "Error en la variable de ruta.",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(
                    value = """
                    {
                        "endpoint": "/sales/{id}",
                        "errors": { "id": "El id debe ser mayor a cero" },
                        "message": "Error de validacion",
                        "status": 400,
                        "timeStamp": "2026-06-12T05:11:58"
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Token JWT inválido o expirado.",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(
                    value = """
                    {
                        "endpoint": "/sales/{id}",
                        "errors": { "error": "Se requiere token de autenticación" },
                        "message": "No autenticado",
                        "status": 401,
                        "timeStamp": "2026-06-12T05:11:58"
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Venta no encontrada.",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(
                    value = """
                    {
                        "endpoint": "/sales/{id}",
                        "errors": { "error": "No se encontro la venta con el id especificado." },
                        "message": "Venta no encontrada",
                        "status": 404,
                        "timeStamp": "2026-06-12T05:11:58"
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Error interno del servidor.",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(
                    value = """
                    {
                        "endpoint": "/sales/{id}",
                        "errors": { "error": "Error inesperado" },
                        "message": "Error interno del servidor",
                        "status": 500,
                        "timeStamp": "2026-06-12T05:11:58"
                    }
                    """
                )
            )
        ),
    })
    ResponseEntity<SaleResponse> getSaleById(
        @Positive(message = "El id debe ser mayor a cero") Integer id
    );

    // 3. ver estado venta
    @Operation(
        summary = "Ver estado de venta",
        description = "Obtiene el estado actual de una venta específica del usuario autenticado."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Estado de venta encontrado."
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Error en la variable de ruta.",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(
                    value = """
                    {
                        "endpoint": "/sales/status/{saleId}",
                        "errors": { "saleId": "El id de la venta debe ser mayor a cero" },
                        "message": "Error de validacion",
                        "status": 400,
                        "timeStamp": "2026-06-12T05:11:58"
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Token JWT inválido o expirado.",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(
                    value = """
                    {
                        "endpoint": "/sales/status/{saleId}",
                        "errors": { "error": "Se requiere token de autenticación" },
                        "message": "No autenticado",
                        "status": 401,
                        "timeStamp": "2026-06-12T05:11:58"
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Venta no encontrada.",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(
                    value = """
                    {
                        "endpoint": "/sales/status/{saleId}",
                        "errors": { "error": "No se encontro la venta con el id especificado." },
                        "message": "Venta no encontrada",
                        "status": 404,
                        "timeStamp": "2026-06-12T05:11:58"
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Error interno del servidor.",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(
                    value = """
                    {
                        "endpoint": "/sales/status/{saleId}",
                        "errors": { "error": "Error inesperado" },
                        "message": "Error interno del servidor",
                        "status": 500,
                        "timeStamp": "2026-06-12T05:11:58"
                    }
                    """
                )
            )
        ),
    })
    ResponseEntity<SaleStatusResponse> getStatusBySaleId(
        @Positive(
            message = "El id de la venta debe ser mayor a cero"
        ) Integer saleId
    );

    // 4. listar ventas (admin)
    @Operation(
        summary = "Listar todas las ventas",
        description = "Retorna una lista con todas las ventas registradas en el sistema. Requiere permisos de administrador."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Lista de todas las ventas."
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Token JWT inválido o expirado.",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(
                    value = """
                    {
                        "endpoint": "/sales/all",
                        "errors": { "error": "Se requiere token de autenticación" },
                        "message": "No autenticado",
                        "status": 401,
                        "timeStamp": "2026-06-12T05:11:58"
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "403",
            description = "No tiene permisos para acceder a este recurso.",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(
                    value = """
                    {
                        "endpoint": "/sales/all",
                        "errors": { "error": "Solo un administrador puede acceder a todas las ventas" },
                        "message": "Debe ser administrador para realizar esta operacion",
                        "status": 403,
                        "timeStamp": "2026-06-12T05:11:58"
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Error interno del servidor.",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(
                    value = """
                    {
                        "endpoint": "/sales/all",
                        "errors": { "error": "Error inesperado" },
                        "message": "Error interno del servidor",
                        "status": 500,
                        "timeStamp": "2026-06-12T05:11:58"
                    }
                    """
                )
            )
        ),
    })
    ResponseEntity<List<SaleResponse>> getAllSales();

    // 5. buscar mis compras (por usuario)
    @Operation(
        summary = "Listar compras del usuario",
        description = "Retorna el historial completo de las compras realizadas por el usuario autenticado."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Lista de compras del usuario."
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Token JWT inválido o expirado.",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(
                    value = """
                    {
                        "endpoint": "/sales/user-sales",
                        "errors": { "error": "Se requiere token de autenticación" },
                        "message": "No autenticado",
                        "status": 401,
                        "timeStamp": "2026-06-12T05:11:58"
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Error interno del servidor.",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(
                    value = """
                    {
                        "endpoint": "/sales/user-sales",
                        "errors": { "error": "Error inesperado" },
                        "message": "Error interno del servidor",
                        "status": 500,
                        "timeStamp": "2026-06-12T05:11:58"
                    }
                    """
                )
            )
        ),
    })
    ResponseEntity<List<SaleResponse>> getSalesByUserId();

    // 6. cancelar venta(idVenta)
    @Operation(
        summary = "Cancelar venta",
        description = "Cancela una venta del usuario, aborta la orden de envío y restaura el stock de los productos."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "204",
            description = "Venta cancelada exitosamente."
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Error en la variable de ruta.",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(
                    value = """
                    {
                        "endpoint": "/sales/cancel/{saleId}",
                        "errors": { "saleId": "El id de la venta debe ser mayor a cero" },
                        "message": "Error de validacion",
                        "status": 400,
                        "timeStamp": "2026-06-12T05:11:58"
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Token JWT inválido o expirado.",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(
                    value = """
                    {
                        "endpoint": "/sales/cancel/{saleId}",
                        "errors": { "error": "Se requiere token de autenticación" },
                        "message": "No autenticado",
                        "status": 401,
                        "timeStamp": "2026-06-12T05:11:58"
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Venta no encontrada.",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(
                    value = """
                    {
                        "endpoint": "/sales/cancel/{saleId}",
                        "errors": { "error": "No se encontro la venta con el id especificado." },
                        "message": "Venta no encontrada",
                        "status": 404,
                        "timeStamp": "2026-06-12T05:11:58"
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Error interno del servidor.",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(
                    value = """
                    {
                        "endpoint": "/sales/cancel/{saleId}",
                        "errors": { "error": "Error al comunicarse con el servicio de envíos" },
                        "message": "Error al cancelar orden de envío",
                        "status": 500,
                        "timeStamp": "2026-06-12T05:11:58"
                    }
                    """
                )
            )
        ),
    })
    ResponseEntity<Void> cancelSale(
        @Positive(
            message = "El id de la venta debe ser mayor a cero"
        ) Integer saleId
    );
}
