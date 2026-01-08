package cl.lobbysync.backend.service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class FirebaseUserService {

    /**
     * Crea un usuario en Firebase Authentication
     * @param email Email del usuario
     * @param password Contraseña del usuario
     * @param displayName Nombre para mostrar
     * @return UID del usuario creado en Firebase
     */
    public String createFirebaseUser(String email, String password, String displayName) throws FirebaseAuthException {
        log.info("Intentando crear usuario en Firebase: {}", email);
        
        UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                .setEmail(email)
                .setEmailVerified(false)
                .setPassword(password)
                .setDisplayName(displayName)
                .setDisabled(false);

        UserRecord userRecord = FirebaseAuth.getInstance().createUser(request);
        log.info("Usuario creado exitosamente en Firebase. UID: {}, Email: {}", 
                userRecord.getUid(), userRecord.getEmail());
        
        return userRecord.getUid();
    }

    /**
     * Verifica si un usuario existe en Firebase por su email
     */
    public boolean userExists(String email) {
        try {
            FirebaseAuth.getInstance().getUserByEmail(email);
            return true;
        } catch (FirebaseAuthException e) {
            if (e.getAuthErrorCode().name().equals("USER_NOT_FOUND")) {
                return false;
            }
            log.error("Error verificando usuario: {}", e.getMessage());
            throw new RuntimeException("Error verificando usuario en Firebase", e);
        }
    }

    /**
     * Elimina un usuario de Firebase Authentication
     */
    public void deleteFirebaseUser(String firebaseUid) throws FirebaseAuthException {
        log.info("Eliminando usuario de Firebase: {}", firebaseUid);
        FirebaseAuth.getInstance().deleteUser(firebaseUid);
        log.info("Usuario eliminado exitosamente de Firebase: {}", firebaseUid);
    }

    /**
     * Obtiene un usuario de Firebase por su UID
     */
    public UserRecord getFirebaseUser(String firebaseUid) throws FirebaseAuthException {
        return FirebaseAuth.getInstance().getUser(firebaseUid);
    }

    /**
     * Obtiene un usuario de Firebase por su email
     */
    public UserRecord getFirebaseUserByEmail(String email) throws FirebaseAuthException {
        return FirebaseAuth.getInstance().getUserByEmail(email);
    }
}
