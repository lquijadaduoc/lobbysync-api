package cl.lobbysync.backend.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * DTO estandarizado para respuestas de error
 * Proporciona información detallada sobre qué salió mal y cómo corregirlo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
    
    /**
     * Timestamp del error
     */
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();
    
    /**
     * Código HTTP del error (400, 404, 500, etc.)
     */
    private int status;
    
    /**
     * Nombre del error (Bad Request, Not Found, etc.)
     */
    private String error;
    
    /**
     * Mensaje descriptivo del error para el usuario
     */
    private String message;
    
    /**
     * Path del endpoint que generó el error
     */
    private String path;
    
    /**
     * Detalles adicionales del error (opcional)
     * Ejemplo: "El campo 'email' es requerido"
     */
    private String details;
    
    /**
     * Lista de errores de validación (para múltiples errores)
     */
    private List<ValidationError> validationErrors;
    
    /**
     * Sugerencias de cómo corregir el error
     */
    private List<String> suggestions;
    
    /**
     * Campos faltantes o inválidos
     */
    private Map<String, String> fieldErrors;
    
    /**
     * Error de validación individual
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ValidationError {
        private String field;
        private String message;
        private Object rejectedValue;
    }
}
