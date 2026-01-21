package cl.lobbysync.backend.exception;

/**
 * Excepción para errores de integración con Firebase
 * Ejemplo: "No se pudo crear el usuario en Firebase", "Token inválido"
 */
public class FirebaseException extends RuntimeException {
    public FirebaseException(String message) {
        super(message);
    }
    
    public FirebaseException(String message, Throwable cause) {
        super(message, cause);
    }
}
