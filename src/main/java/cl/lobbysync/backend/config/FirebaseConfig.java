package cl.lobbysync.backend.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.io.FileInputStream;
import java.io.IOException;

@Configuration
@Slf4j
public class FirebaseConfig {

    @Bean
    public FirebaseApp firebaseApp() {
        try {
            if (FirebaseApp.getApps().isEmpty()) {
                FileInputStream serviceAccount = new FileInputStream("serviceAccountKey.json");
                GoogleCredentials credentials = GoogleCredentials.fromStream(serviceAccount);
                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(credentials)
                        .build();
                return FirebaseApp.initializeApp(options);
            }
            return FirebaseApp.getInstance();
        } catch (IOException e) {
            log.warn("Firebase initialization failed: " + e.getMessage());
            return null;
        }
    }

    @Bean
    public FirebaseAuth firebaseAuth() {
        try {
            if (!FirebaseApp.getApps().isEmpty()) {
                return FirebaseAuth.getInstance();
            }
        } catch (Exception e) {
            log.warn("FirebaseAuth initialization failed: " + e.getMessage());
        }
        return null;
    }
}
