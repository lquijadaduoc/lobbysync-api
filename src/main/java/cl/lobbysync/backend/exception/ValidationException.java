package cl.lobbysync.backend.exception;

import lombok.Getter;

/**
 * Excepción para validaciones de negocio fallidas
 * Ejemplo: "El email ya está registrado", "La contraseña es muy débil"
 */
@Getter
public class ValidationException extends RuntimeException {
    private final String field;
    private final Object rejectedValue;
    
    public ValidationException(String message) {
        super(message);
        this.field = null;
        this.rejectedValue = null;
    }
    
    public ValidationException(String field, String message) {
        super(message);
        this.field = field;
        this.rejectedValue = null;
    }
    
    public ValidationException(String field, Object rejectedValue, String message) {
        super(message);
        this.field = field;
        this.rejectedValue = rejectedValue;
    }
}
