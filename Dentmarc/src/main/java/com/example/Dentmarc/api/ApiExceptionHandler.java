package com.example.Dentmarc.api;

import com.example.Dentmarc.api.ApiDtos.ErrorResponse;
import com.example.Dentmarc.api.ApiDtos.FieldError;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.List;

@RestControllerAdvice(basePackages = "com.example.Dentmarc.api")
public class ApiExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(ApiExceptionHandler.class);

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErrorResponse> handleApiException(ApiException exception, HttpServletRequest request) {
        return response(exception.getStatus(), exception.getMessage(), request, List.of());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException exception,
                                                          HttpServletRequest request) {
        List<FieldError> errors = exception.getBindingResult().getFieldErrors().stream()
                .map(error -> new FieldError(error.getField(), error.getDefaultMessage()))
                .toList();
        return response(HttpStatus.BAD_REQUEST, "La solicitud contiene datos inválidos.", request, errors);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException exception,
                                                                   HttpServletRequest request) {
        List<FieldError> errors = exception.getConstraintViolations().stream()
                .map(violation -> new FieldError(violation.getPropertyPath().toString(),
                        violation.getMessage()))
                .toList();
        return response(HttpStatus.BAD_REQUEST, "La solicitud contiene datos inválidos.", request, errors);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleUnreadable(HttpMessageNotReadableException exception,
                                                          HttpServletRequest request) {
        return response(HttpStatus.BAD_REQUEST, "El cuerpo JSON está mal formado o contiene valores inválidos.",
                request, List.of());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleConflict(DataIntegrityViolationException exception,
                                                        HttpServletRequest request) {
        return response(HttpStatus.CONFLICT, "La operación entra en conflicto con datos existentes.",
                request, List.of());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException exception,
                                                               HttpServletRequest request) {
        return response(HttpStatus.BAD_REQUEST, exception.getMessage(), request, List.of());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(Exception exception, HttpServletRequest request) {
        logger.error("Error interno al atender la solicitud REST {}", request.getRequestURI(), exception);
        return response(HttpStatus.INTERNAL_SERVER_ERROR, "Ocurrió un error interno al procesar la solicitud.",
                request, List.of());
    }

    private ResponseEntity<ErrorResponse> response(HttpStatus status, String message,
                                                   HttpServletRequest request, List<FieldError> fieldErrors) {
        return ResponseEntity.status(status).body(new ErrorResponse(Instant.now().toString(),
                status.value(), status.getReasonPhrase(), message, request.getRequestURI(), fieldErrors));
    }
}
