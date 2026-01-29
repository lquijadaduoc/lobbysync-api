package cl.lobbysync.backend.exception;

import cl.lobbysync.backend.dto.ErrorResponse;
import com.google.firebase.FirebaseException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manejador global de excepciones para toda la API
 * Proporciona respuestas de error consistentes y descriptivas
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 404 - Recurso no encontrado
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(
            ResourceNotFoundException ex, 
            HttpServletRequest request) {
        
        log.warn("Resource not found: {}", ex.getMessage());
        
        ErrorResponse error = ErrorResponse.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .error("Not Found")
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .details("El recurso solicitado no existe en el sistema")
                .suggestions(List.of(
                    "Verifica que el ID sea correcto",
                    "Asegúrate de que el recurso no haya sido eliminado"
                ))
                .build();
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    /**
     * 400 - Validación fallida (Bean Validation)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {
        
        log.warn("Validation failed: {}", ex.getMessage());
        
        List<ErrorResponse.ValidationError> validationErrors = new ArrayList<>();
        Map<String, String> fieldErrors = new HashMap<>();
        
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            Object rejectedValue = ((FieldError) error).getRejectedValue();
            
            validationErrors.add(ErrorResponse.ValidationError.builder()
                    .field(fieldName)
                    .message(errorMessage)
                    .rejectedValue(rejectedValue)
                    .build());
            
            fieldErrors.put(fieldName, errorMessage);
        });
        
        ErrorResponse error = ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Validation Error")
                .message("Uno o más campos tienen errores de validación")
                .path(request.getRequestURI())
                .details("Revisa los campos marcados e intenta nuevamente")
                .validationErrors(validationErrors)
                .fieldErrors(fieldErrors)
                .suggestions(List.of(
                    "Verifica que todos los campos requeridos estén completos",
                    "Asegúrate de que los valores cumplan con el formato esperado"
                ))
                .build();
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * 400 - Validación de negocio fallida
     */
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            ValidationException ex,
            HttpServletRequest request) {
        
        log.warn("Business validation failed: {}", ex.getMessage());
        
        ErrorResponse.ErrorResponseBuilder errorBuilder = ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Validation Error")
                .message(ex.getMessage())
                .path(request.getRequestURI());
        
        if (ex.getField() != null) {
            Map<String, String> fieldErrors = new HashMap<>();
            fieldErrors.put(ex.getField(), ex.getMessage());
            errorBuilder.fieldErrors(fieldErrors);
            
            if (ex.getRejectedValue() != null) {
                errorBuilder.validationErrors(List.of(
                    ErrorResponse.ValidationError.builder()
                        .field(ex.getField())
                        .message(ex.getMessage())
                        .rejectedValue(ex.getRejectedValue())
                        .build()
                ));
            }
        }
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorBuilder.build());
    }

    /**
     * 400 - Argumento ilegal
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(
            IllegalArgumentException ex,
            HttpServletRequest request) {
        
        log.warn("Illegal argument: {}", ex.getMessage());
        
        ErrorResponse error = ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Bad Request")
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .details("Los datos proporcionados no son válidos")
                .suggestions(List.of(
                    "Verifica que los parámetros enviados sean correctos",
                    "Consulta la documentación de la API para el formato esperado"
                ))
                .build();
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * 400 - Parámetro faltante
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParameter(
            MissingServletRequestParameterException ex,
            HttpServletRequest request) {
        
        log.warn("Missing parameter: {}", ex.getParameterName());
        
        Map<String, String> fieldErrors = new HashMap<>();
        fieldErrors.put(ex.getParameterName(), "Este parámetro es requerido");
        
        ErrorResponse error = ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Missing Parameter")
                .message(String.format("El parámetro '%s' es requerido", ex.getParameterName()))
                .path(request.getRequestURI())
                .details(String.format("Falta el parámetro '%s' de tipo %s", 
                    ex.getParameterName(), ex.getParameterType()))
                .fieldErrors(fieldErrors)
                .suggestions(List.of(
                    String.format("Agrega el parámetro '%s' a tu request", ex.getParameterName()),
                    "Ejemplo: ?" + ex.getParameterName() + "=valor"
                ))
                .build();
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * 400 - Tipo de argumento incorrecto
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex,
            HttpServletRequest request) {
        
        log.warn("Type mismatch for parameter {}: {}", ex.getName(), ex.getValue());
        
        String expectedType = ex.getRequiredType() != null ? 
            ex.getRequiredType().getSimpleName() : "unknown";
        
        Map<String, String> fieldErrors = new HashMap<>();
        fieldErrors.put(ex.getName(), 
            String.format("Debe ser de tipo %s, pero se recibió '%s'", expectedType, ex.getValue()));
        
        ErrorResponse error = ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Invalid Parameter Type")
                .message(String.format("El parámetro '%s' tiene un tipo inválido", ex.getName()))
                .path(request.getRequestURI())
                .details(String.format("Se esperaba %s pero se recibió '%s'", expectedType, ex.getValue()))
                .fieldErrors(fieldErrors)
                .suggestions(List.of(
                    String.format("Envía un valor de tipo %s para '%s'", expectedType, ex.getName()),
                    "Ejemplo: " + ex.getName() + "=" + (expectedType.equals("Long") ? "123" : "valor")
                ))
                .build();
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * 401 - No autorizado
     */
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorized(
            UnauthorizedException ex,
            HttpServletRequest request) {
        
        log.warn("Unauthorized access: {}", ex.getMessage());
        
        ErrorResponse error = ErrorResponse.builder()
                .status(HttpStatus.UNAUTHORIZED.value())
                .error("Unauthorized")
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .details("No tienes permisos para realizar esta acción")
                .suggestions(List.of(
                    "Verifica que estés autenticado correctamente",
                    "Asegúrate de tener los permisos necesarios",
                    "Contacta al administrador si crees que deberías tener acceso"
                ))
                .build();
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    /**
     * 409 - Conflicto (recurso duplicado)
     */
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorResponse> handleConflict(
            ConflictException ex,
            HttpServletRequest request) {
        
        log.warn("Conflict: {}", ex.getMessage());
        
        ErrorResponse error = ErrorResponse.builder()
                .status(HttpStatus.CONFLICT.value())
                .error("Conflict")
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .details("El recurso ya existe o hay un conflicto con datos existentes")
                .suggestions(List.of(
                    "Verifica que el recurso no esté duplicado",
                    "Intenta con valores diferentes",
                    "Actualiza el recurso existente en lugar de crear uno nuevo"
                ))
                .build();
        
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    /**
     * 500 - Error de Firebase
     */
    @ExceptionHandler({
        cl.lobbysync.backend.exception.FirebaseException.class,
        FirebaseException.class
    })
    public ResponseEntity<ErrorResponse> handleFirebaseException(
            Exception ex,
            HttpServletRequest request) {
        
        log.error("Firebase error: {}", ex.getMessage(), ex);
        
        ErrorResponse error = ErrorResponse.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error("Firebase Error")
                .message("Error al comunicarse con Firebase Authentication")
                .path(request.getRequestURI())
                .details(ex.getMessage())
                .suggestions(List.of(
                    "Verifica la configuración de Firebase",
                    "Intenta nuevamente en unos momentos",
                    "Contacta al administrador si el problema persiste"
                ))
                .build();
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    /**
     * 500 - Error general no manejado
     * NOTA: NoResourceFoundException no se maneja aquí para permitir que Spring Boot
     * muestre su página de error por defecto en rutas de infraestructura
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(
            Exception ex,
            HttpServletRequest request) {
        
        String path = request.getRequestURI();
        
        // No interceptar NoResourceFoundException para rutas de infraestructura
        // Esto permite que Spring Boot maneje correctamente Swagger y Actuator
        if (ex instanceof org.springframework.web.servlet.resource.NoResourceFoundException) {
            if (path.startsWith("/swagger-ui") || 
                path.startsWith("/v3/api-docs") || 
                path.startsWith("/actuator") ||
                path.contains("/webjars/")) {
                // Delegar a Spring Boot para que sirva recursos estáticos o endpoints de infraestructura
                throw (RuntimeException) ex;
            }
        }
        
        // También excluir archivos estáticos explícitamente
        if (path.endsWith(".html") ||
            path.endsWith(".css") ||
            path.endsWith(".js") ||
            path.endsWith(".ico") ||
            path.endsWith(".png") ||
            path.endsWith(".map")) {
            throw new RuntimeException(ex);
        }
        
        log.error("Unexpected error: {}", ex.getMessage(), ex);
        
        ErrorResponse error = ErrorResponse.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error("Internal Server Error")
                .message("Ocurrió un error inesperado en el servidor")
                .path(path)
                .details(ex.getMessage() != null ? ex.getMessage() : "Error desconocido")
                .suggestions(List.of(
                    "Intenta nuevamente en unos momentos",
                    "Verifica que los datos enviados sean correctos",
                    "Contacta al administrador si el problema persiste"
                ))
                .build();
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
