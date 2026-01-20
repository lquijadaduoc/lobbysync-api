package cl.lobbysync.backend.service;

import cl.lobbysync.backend.dto.CreateUserRequest;
import cl.lobbysync.backend.dto.UserCreationResponse;
import cl.lobbysync.backend.dto.UserSyncResponse;
import cl.lobbysync.backend.model.sql.User;
import cl.lobbysync.backend.model.sql.Unit;
import cl.lobbysync.backend.repository.UserRepository;
import cl.lobbysync.backend.repository.UnitRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UnitRepository unitRepository;

    @Autowired(required = false)
    private FirebaseAuth firebaseAuth;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public UserSyncResponse syncUserFromFirebase(String firebaseUid) throws FirebaseAuthException {
        UserRecord userRecord = firebaseAuth.getUser(firebaseUid);
        
        // Primero buscar por firebase_uid
        Optional<User> existingUser = userRepository.findByFirebaseUid(firebaseUid);
        
        // Si no se encuentra por UID, buscar por email y actualizar el firebase_uid
        if (!existingUser.isPresent()) {
            existingUser = userRepository.findByEmail(userRecord.getEmail());
            if (existingUser.isPresent()) {
                User user = existingUser.get();
                user.setFirebaseUid(firebaseUid);
                userRepository.save(user);
                log.info("Updated firebase_uid for user: {}", user.getEmail());
            }
        }
        
        if (existingUser.isPresent()) {
            User user = existingUser.get();
            return UserSyncResponse.builder()
                    .id(user.getId())
                    .email(user.getEmail())
                    .firebaseUid(user.getFirebaseUid())
                    .role(user.getRole())
                    .isActive(user.getIsActive())
                    .message("User already synchronized")
                    .isNew(false)
                    .build();
        }
        
        // Si no existe en absoluto, crear nuevo con rol por defecto
        User newUser = User.builder()
                .email(userRecord.getEmail())
                .firebaseUid(firebaseUid)
                .role("RESIDENT")
                .isActive(true)
                .build();
        
        User savedUser = userRepository.save(newUser);
        log.info("Created new user: {} with role: {}", savedUser.getEmail(), savedUser.getRole());
        
        return UserSyncResponse.builder()
                .id(savedUser.getId())
                .email(savedUser.getEmail())
                .firebaseUid(savedUser.getFirebaseUid())
                .role(savedUser.getRole())
                .isActive(savedUser.getIsActive())
                .message("User synchronized successfully")
                .isNew(true)
                .build();
    }

    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public User getUserByFirebaseUid(String firebaseUid) {
        return userRepository.findByFirebaseUid(firebaseUid)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    /**
     * Crea un usuario en Firebase Authentication y lo sincroniza con PostgreSQL
     */
    @Transactional
    public UserCreationResponse createUserWithFirebase(CreateUserRequest request) {
        try {
            log.info("Creating user in Firebase: {}", request.getEmail());
            
            // 1. Verificar si el usuario ya existe en PostgreSQL
            Optional<User> existingUser = userRepository.findByEmail(request.getEmail());
            if (existingUser.isPresent()) {
                log.warn("User already exists in database: {}", request.getEmail());
                return UserCreationResponse.builder()
                        .success(false)
                        .message("El usuario ya existe en la base de datos")
                        .email(request.getEmail())
                        .build();
            }
            
            // 2. Crear usuario en Firebase Authentication
            UserRecord.CreateRequest firebaseRequest = new UserRecord.CreateRequest()
                    .setEmail(request.getEmail())
                    .setPassword(request.getPassword())
                    .setEmailVerified(false)
                    .setDisabled(false);
            
            // Agregar displayName si hay nombre y apellido
            if (request.getFirstName() != null && request.getLastName() != null) {
                String displayName = request.getFirstName() + " " + request.getLastName();
                firebaseRequest.setDisplayName(displayName);
            }
            
            UserRecord firebaseUser = firebaseAuth.createUser(firebaseRequest);
            log.info("User created in Firebase with UID: {}", firebaseUser.getUid());
            
            // 3. Crear usuario en PostgreSQL
            User.UserBuilder userBuilder = User.builder()
                    .email(request.getEmail())
                    .firebaseUid(firebaseUser.getUid())
                    .role(request.getRole())
                    .firstName(request.getFirstName())
                    .lastName(request.getLastName())
                    .phone(request.getPhone())
                    .isActive(true);
            
            // Asignar unidad si es residente y se proporciona unitId
            if (request.getUnitId() != null && "RESIDENT".equals(request.getRole())) {
                Unit unit = unitRepository.findById(request.getUnitId())
                        .orElseThrow(() -> new RuntimeException("Unidad no encontrada con ID: " + request.getUnitId()));
                userBuilder.unit(unit);
            }
            
            User newUser = userBuilder.build();
            
            User savedUser = userRepository.save(newUser);
            log.info("User saved in PostgreSQL with ID: {}", savedUser.getId());
            
            return UserCreationResponse.builder()
                    .success(true)
                    .message("Usuario creado exitosamente en Firebase y PostgreSQL")
                    .userId(savedUser.getId())
                    .firebaseUid(firebaseUser.getUid())
                    .email(savedUser.getEmail())
                    .role(savedUser.getRole())
                    .build();
                    
        } catch (FirebaseAuthException e) {
            log.error("Firebase error creating user: {}", e.getMessage());
            String errorMessage = "Error en Firebase: ";
            
            if (e.getAuthErrorCode().name().equals("EMAIL_ALREADY_EXISTS")) {
                errorMessage += "El correo ya está registrado en Firebase";
            } else {
                errorMessage += e.getMessage();
            }
            
            return UserCreationResponse.builder()
                    .success(false)
                    .message(errorMessage)
                    .email(request.getEmail())
                    .build();
        } catch (Exception e) {
            log.error("Error creating user: {}", e.getMessage());
            return UserCreationResponse.builder()
                    .success(false)
                    .message("Error al crear usuario: " + e.getMessage())
                    .email(request.getEmail())
                    .build();
        }
    }

    /**
     * Actualiza un usuario existente
     */
    @Transactional
    public User updateUser(Long userId, cl.lobbysync.backend.dto.UpdateUserRequest request) {
        try {
            User user = getUserById(userId);
            
            // Actualizar campos si están presentes
            if (request.getFirstName() != null) {
                user.setFirstName(request.getFirstName());
            }
            if (request.getLastName() != null) {
                user.setLastName(request.getLastName());
            }
            if (request.getPhone() != null) {
                user.setPhone(request.getPhone());
            }
            if (request.getRole() != null) {
                user.setRole(request.getRole());
            }
            if (request.getUnitId() != null) {
                Unit unit = unitRepository.findById(request.getUnitId())
                        .orElseThrow(() -> new RuntimeException("Unidad no encontrada con ID: " + request.getUnitId()));
                user.setUnit(unit);
            }
            if (request.getIsActive() != null) {
                user.setIsActive(request.getIsActive());
            }
            
            // Actualizar displayName en Firebase si hay cambios de nombre
            if ((request.getFirstName() != null || request.getLastName() != null) && user.getFirebaseUid() != null) {
                try {
                    String displayName = (request.getFirstName() != null ? request.getFirstName() : user.getFirstName()) 
                                       + " " 
                                       + (request.getLastName() != null ? request.getLastName() : user.getLastName());
                    
                    UserRecord.UpdateRequest updateRequest = new UserRecord.UpdateRequest(user.getFirebaseUid())
                            .setDisplayName(displayName);
                    
                    firebaseAuth.updateUser(updateRequest);
                    log.info("Updated displayName in Firebase for user: {}", user.getEmail());
                } catch (FirebaseAuthException e) {
                    log.warn("Could not update Firebase displayName: {}", e.getMessage());
                }
            }
            
            User updatedUser = userRepository.save(user);
            log.info("User updated: {}", updatedUser.getEmail());
            
            return updatedUser;
        } catch (Exception e) {
            log.error("Error updating user: {}", e.getMessage());
            throw new RuntimeException("Error al actualizar usuario: " + e.getMessage());
        }
    }

    /**
     * Elimina un usuario de Firebase y PostgreSQL
     */
    @Transactional
    public void deleteUser(Long userId) {
        try {
            User user = getUserById(userId);
            
            // Eliminar de Firebase si tiene firebaseUid
            if (user.getFirebaseUid() != null) {
                try {
                    firebaseAuth.deleteUser(user.getFirebaseUid());
                    log.info("User deleted from Firebase: {}", user.getEmail());
                } catch (FirebaseAuthException e) {
                    log.warn("Could not delete user from Firebase (may not exist): {}", e.getMessage());
                }
            }
            
            // Eliminar de PostgreSQL
            userRepository.delete(user);
            log.info("User deleted from database: {}", user.getEmail());
            
        } catch (Exception e) {
            log.error("Error deleting user: {}", e.getMessage());
            throw new RuntimeException("Error al eliminar usuario: " + e.getMessage());
        }
    }

    /**
     * Cambia la contraseña de un usuario en Firebase
     */
    public void changePassword(Long userId, String newPassword) {
        try {
            User user = getUserById(userId);
            
            if (user.getFirebaseUid() == null) {
                throw new RuntimeException("Usuario no tiene Firebase UID asociado");
            }
            
            UserRecord.UpdateRequest updateRequest = new UserRecord.UpdateRequest(user.getFirebaseUid())
                    .setPassword(newPassword);
            
            firebaseAuth.updateUser(updateRequest);
            log.info("Password changed for user: {}", user.getEmail());
            
        } catch (FirebaseAuthException e) {
            log.error("Firebase error changing password: {}", e.getMessage());
            throw new RuntimeException("Error al cambiar contraseña en Firebase: " + e.getMessage());
        } catch (Exception e) {
            log.error("Error changing password: {}", e.getMessage());
            throw new RuntimeException("Error al cambiar contraseña: " + e.getMessage());
        }
    }
}
