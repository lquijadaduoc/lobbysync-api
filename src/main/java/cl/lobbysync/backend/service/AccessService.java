package cl.lobbysync.backend.service;

import cl.lobbysync.backend.model.mongo.AccessLog;
import cl.lobbysync.backend.repository.mongo.AccessLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AccessService {

    @Autowired
    private AccessLogRepository accessLogRepository;

    public AccessLog createAccessLog(Long userId, String accessType, String location, String description) {
        AccessLog accessLog = AccessLog.builder()
                .userId(userId)
                .accessType(accessType)
                .location(location)
                .description(description)
                .timestamp(LocalDateTime.now())
                .isAuthorized(true)
                .build();
        return accessLogRepository.save(accessLog);
    }

    public List<AccessLog> getAccessLogsByUserId(Long userId) {
        return accessLogRepository.findByUserId(userId);
    }

    public List<AccessLog> getAccessLogsByUserIdAfter(Long userId, LocalDateTime timestamp) {
        return accessLogRepository.findByUserIdAndTimestampAfter(userId, timestamp);
    }

    public List<AccessLog> getAccessLogsByType(String accessType) {
        return accessLogRepository.findByAccessType(accessType);
    }

    public List<AccessLog> getAllAccessLogs() {
        return accessLogRepository.findAll();
    }
}
