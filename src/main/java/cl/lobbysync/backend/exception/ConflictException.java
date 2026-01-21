package cl.lobbysync.backend.exception;

/**
 * Excepción para conflictos de datos
 * Ejemplo: "El usuario ya existe", "El edificio ya tiene ese nombre"
 */
public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }
    
    public ConflictException(String message, Throwable cause) {
        super(message, cause);
    }
}
