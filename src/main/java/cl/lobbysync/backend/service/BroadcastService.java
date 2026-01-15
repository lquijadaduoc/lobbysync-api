package cl.lobbysync.backend.service;

import cl.lobbysync.backend.model.mongo.Broadcast;
import cl.lobbysync.backend.repository.BroadcastRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class BroadcastService {

    @Autowired(required = false)
    private BroadcastRepository broadcastRepository;

    public List<Broadcast> getAllBroadcasts() {
        if (broadcastRepository == null) return new java.util.ArrayList<>();
        return broadcastRepository.findAll();
    }

    public List<Broadcast> getActiveBroadcasts() {
        if (broadcastRepository == null) return new java.util.ArrayList<>();
        return broadcastRepository.findByIsActive(true);
    }

    public Broadcast getBroadcastById(String id) {
        if (broadcastRepository == null) return null;
        return broadcastRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Broadcast not found"));
    }

    public Broadcast createBroadcast(Broadcast broadcast) {
        if (broadcastRepository == null) return null;
        broadcast.setSentAt(LocalDateTime.now());
        if (broadcast.getIsActive() == null) {
            broadcast.setIsActive(true);
        }
        if (broadcast.getDeliveredCount() == null) {
            broadcast.setDeliveredCount(0);
        }
        if (broadcast.getReadCount() == null) {
            broadcast.setReadCount(0);
        }
        
        // Simular conteo de destinatarios basado en audiencia
        int recipientCount = calculateRecipientCount(broadcast.getTargetAudience());
        broadcast.setRecipientCount(recipientCount);
        
        return broadcastRepository.save(broadcast);
    }

    public void deleteBroadcast(String id) {
        if (broadcastRepository != null) broadcastRepository.deleteById(id);
    }

    public Map<String, Object> getBroadcastStats() {
        if (broadcastRepository == null) return new HashMap<>();
        List<Broadcast> allBroadcasts = broadcastRepository.findAll();
        
        int totalSent = allBroadcasts.size();
        int totalDelivered = allBroadcasts.stream()
                .mapToInt(b -> b.getDeliveredCount() != null ? b.getDeliveredCount() : 0)
                .sum();
        int totalRead = allBroadcasts.stream()
                .mapToInt(b -> b.getReadCount() != null ? b.getReadCount() : 0)
                .sum();
        int totalRecipients = allBroadcasts.stream()
                .mapToInt(b -> b.getRecipientCount() != null ? b.getRecipientCount() : 0)
                .sum();
        
        double deliveryRate = totalRecipients > 0 ? (double) totalDelivered / totalRecipients * 100 : 0;
        double readRate = totalDelivered > 0 ? (double) totalRead / totalDelivered * 100 : 0;
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalSent", totalSent);
        stats.put("deliveryRate", Math.round(deliveryRate * 10) / 10.0);
        stats.put("readRate", Math.round(readRate * 10) / 10.0);
        
        return stats;
    }

    private int calculateRecipientCount(String targetAudience) {
        // Simulación - en producción se consultaría la base de datos
        switch (targetAudience) {
            case "RESIDENTS":
                return 150; // Número aproximado de residentes
            case "CONCIERGES":
                return 8;   // Número aproximado de conserjes
            case "ALL":
            default:
                return 158; // Todos
        }
    }
}
