package cl.lobbysync.backend.service;

import cl.lobbysync.backend.dto.UserSyncResponse;
import cl.lobbysync.backend.model.sql.User;
import cl.lobbysync.backend.repository.UserRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FirebaseAuth firebaseAuth;

    public UserSyncResponse syncUserFromFirebase(String firebaseUid) throws FirebaseAuthException {
        UserRecord userRecord = firebaseAuth.getUser(firebaseUid);
        
        Optional<User> existingUser = userRepository.findByFirebaseUid(firebaseUid);
        
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
        
        User newUser = User.builder()
                .email(userRecord.getEmail())
                .firebaseUid(firebaseUid)
                .role("CONSERJE")
                .isActive(true)
                .build();
        
        User savedUser = userRepository.save(newUser);
        
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
}
