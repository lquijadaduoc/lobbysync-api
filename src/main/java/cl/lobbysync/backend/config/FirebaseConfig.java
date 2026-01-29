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
                log.info("Initializing Firebase with serviceAccountKey.json");
                FileInputStream serviceAccount = new FileInputStream("serviceAccountKey.json");
                GoogleCredentials credentials = GoogleCredentials.fromStream(serviceAccount);
                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(credentials)
                        .build();
                FirebaseApp app = FirebaseApp.initializeApp(options);
                log.info("Firebase initialized successfully");
                return app;
            }
            log.info("Firebase already initialized");
            return FirebaseApp.getInstance();
        } catch (IOException e) {
            log.error("Firebase initialization failed - IOException: {}", e.getMessage(), e);
            return null;
        } catch (Exception e) {
            log.error("Firebase initialization failed - Exception: {}", e.getMessage(), e);
            return null;
        }
    }

    @Bean
    public FirebaseAuth firebaseAuth() {
        try {
            if (!FirebaseApp.getApps().isEmpty()) {
                FirebaseAuth auth = FirebaseAuth.getInstance();
                log.info("FirebaseAuth bean created successfully");
                return auth;
            } else {
                log.error("FirebaseAuth cannot be created - FirebaseApp not initialized");
            }
        } catch (Exception e) {
            log.error("FirebaseAuth initialization failed: {}", e.getMessage(), e);
        }
        return null;
    }
}
