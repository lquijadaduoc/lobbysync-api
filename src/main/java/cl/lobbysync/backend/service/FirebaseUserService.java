package cl.lobbysync.backend.service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class FirebaseUserService {

    private final FirebaseAuth firebaseAuth;

    @Autowired(required = false)
    public FirebaseUserService(FirebaseAuth firebaseAuth) {
        this.firebaseAuth = firebaseAuth;
    }

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

        UserRecord userRecord = firebaseAuth.createUser(request);
        log.info("Usuario creado exitosamente en Firebase. UID: {}, Email: {}", 
                userRecord.getUid(), userRecord.getEmail());
        
        return userRecord.getUid();
    }

    /**
     * Verifica si un usuario existe en Firebase por su email
     */
    public boolean userExists(String email) {
        try {
            firebaseAuth.getUserByEmail(email);
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
        firebaseAuth.deleteUser(firebaseUid);
        log.info("Usuario eliminado exitosamente de Firebase: {}", firebaseUid);
    }

    /**
     * Obtiene un usuario de Firebase por su UID
     */
    public UserRecord getFirebaseUser(String firebaseUid) throws FirebaseAuthException {
        return firebaseAuth.getUser(firebaseUid);
    }

    /**
     * Obtiene un usuario de Firebase por su email
     */
    public UserRecord getFirebaseUserByEmail(String email) throws FirebaseAuthException {
        return firebaseAuth.getUserByEmail(email);
    }

    /**
     * Actualiza información de un usuario en Firebase
     */
    public void updateFirebaseUser(String firebaseUid, String email, String displayName, Boolean disabled, String phoneNumber) throws FirebaseAuthException {
        log.info("Actualizando usuario en Firebase: {}", firebaseUid);
        
        UserRecord.UpdateRequest updateRequest = new UserRecord.UpdateRequest(firebaseUid);
        
        if (email != null) {
            updateRequest.setEmail(email);
        }
        if (displayName != null) {
            updateRequest.setDisplayName(displayName);
        }
        if (disabled != null) {
            updateRequest.setDisabled(disabled);
        }
        if (phoneNumber != null) {
            updateRequest.setPhoneNumber(phoneNumber);
        }
        
        firebaseAuth.updateUser(updateRequest);
        log.info("Usuario actualizado exitosamente en Firebase: {}", firebaseUid);
    }
}
