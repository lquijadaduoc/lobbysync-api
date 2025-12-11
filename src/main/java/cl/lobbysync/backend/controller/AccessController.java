package cl.lobbysync.backend.controller;

import cl.lobbysync.backend.dto.AccessEntryRequest;
import cl.lobbysync.backend.model.mongo.AccessLog;
import cl.lobbysync.backend.service.AccessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/access-logs")
public class AccessController {

    @Autowired
    private AccessService accessService;

    @PostMapping
    public ResponseEntity<AccessLog> createAccessLog(
            Authentication authentication,
            @RequestBody AccessEntryRequest request) {
        
        Long userId = getUserIdFromAuthentication(authentication);
        
        AccessLog accessLog = accessService.createAccessLog(
            userId,
            request.getAccessType(),
            request.getLocation(),
            request.getDescription()
        );
        
        return ResponseEntity.ok(accessLog);
    }

    @GetMapping
    public ResponseEntity<List<AccessLog>> getAllAccessLogs() {
        return ResponseEntity.ok(accessService.getAllAccessLogs());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AccessLog>> getUserAccessLogs(@PathVariable Long userId) {
        return ResponseEntity.ok(accessService.getAccessLogsByUserId(userId));
    }

    @GetMapping("/type/{accessType}")
    public ResponseEntity<List<AccessLog>> getAccessLogsByType(@PathVariable String accessType) {
        return ResponseEntity.ok(accessService.getAccessLogsByType(accessType));
    }

    private Long getUserIdFromAuthentication(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            String firebaseUid = (String) authentication.getPrincipal();
            return 1L;
        }
        return 1L;
    }
}
