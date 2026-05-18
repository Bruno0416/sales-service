package com.mariluz.sales.exceptions;

import com.mariluz.sales.dto.ErrorResponse;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Handler permisos usuario
    @ExceptionHandler(UnauthorizedOperationException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorizedOperation(
        UnauthorizedOperationException ex,
        HttpServletRequest request
    ) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
            ErrorResponse.builder()
                .timeStamp(LocalDateTime.now())
                .status(HttpStatus.FORBIDDEN.value())
                .message("Debe ser administrador para realizar esta operacion")
                .errors(Map.of("error", ex.getMessage()))
                .endpoint(request.getRequestURI())
                .build()
        );
    }

    // Global Handler | Validacion BindingResult ahora se corrobora aqui en el global handler
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(
        MethodArgumentNotValidException ex,
        HttpServletRequest request
    ) {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult()
            .getFieldErrors()
            .forEach(e -> errors.put(e.getField(), e.getDefaultMessage()));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            ErrorResponse.builder()
                .timeStamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .message("Error de validacion")
                .errors(errors)
                .endpoint(request.getRequestURI())
                .build()
        );
    }

    // Handler token expirado
    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ErrorResponse> handleExpiredTokenException(
        ExpiredJwtException ex,
        HttpServletRequest request
    ) {
        Map<String, String> error = Map.of(
            "error",
            "El token ha expirado. Por favor, inicie sesión nuevamente."
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            ErrorResponse.builder()
                .timeStamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .message("Token expirado")
                .errors(error)
                .endpoint(request.getRequestURI())
                .build()
        );
    }

    // Handler error generico de JWT
    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ErrorResponse> handleJsonParseError(
        JwtException ex,
        HttpServletRequest request
    ) {
        Map<String, String> error = Map.of(
            "error",
            "El token proporcionado es inválido o está corrupto."
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            ErrorResponse.builder()
                .timeStamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .message("Error de token")
                .errors(error)
                .endpoint(request.getRequestURI())
                .build()
        );
    }
}
