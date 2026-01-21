package cl.lobbysync.backend.exception;

/**
 * Excepción para operaciones no autorizadas
 * Ejemplo: "No tienes permisos para eliminar este usuario"
 */
public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String message) {
        super(message);
    }
    
    public UnauthorizedException(String message, Throwable cause) {
        super(message, cause);
    }
}
