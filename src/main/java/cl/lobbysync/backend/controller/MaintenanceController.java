package cl.lobbysync.backend.controller;

import cl.lobbysync.backend.model.mongo.MaintenanceTicket;
import cl.lobbysync.backend.service.MaintenanceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tickets")
@Slf4j
public class MaintenanceController {

    @Autowired
    private MaintenanceService maintenanceService;

    @GetMapping
    public ResponseEntity<List<MaintenanceTicket>> getAllTickets(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long buildingId) {
        
        if (status != null) {
            return ResponseEntity.ok(maintenanceService.getTicketsByStatus(status));
        }
        
        if (buildingId != null) {
            return ResponseEntity.ok(maintenanceService.getTicketsByBuildingId(buildingId));
        }
        
        return ResponseEntity.ok(maintenanceService.getAllTickets());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MaintenanceTicket> getTicketById(@PathVariable String id) {
        return ResponseEntity.ok(maintenanceService.getTicketById(id));
    }

    @PostMapping
    public ResponseEntity<MaintenanceTicket> createTicket(@RequestBody MaintenanceTicket ticket) {
        log.info("Creating maintenance ticket: {}", ticket.getTitle());
        MaintenanceTicket created = maintenanceService.createTicket(ticket);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * PUT /api/tickets/{id}/status
     * Actualiza el estado del ticket (Ej: OPEN -> IN_PROGRESS -> RESOLVED -> CLOSED)
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<MaintenanceTicket> updateTicketStatus(
            @PathVariable String id,
            @RequestBody Map<String, String> request) {
        
        log.info("Updating ticket {} status to: {}", id, request.get("status"));
        String status = request.get("status");
        String resolution = request.get("resolution");
        
        MaintenanceTicket updated = maintenanceService.updateTicketStatus(id, status, resolution);
        return ResponseEntity.ok(updated);
    }

    @PutMapping("/{id}/assign")
    public ResponseEntity<MaintenanceTicket> assignTicket(
            @PathVariable String id,
            @RequestBody Map<String, Long> request) {
        
        Long assignedTo = request.get("assignedTo");
        log.info("Assigning ticket {} to user: {}", id, assignedTo);
        
        MaintenanceTicket updated = maintenanceService.assignTicket(id, assignedTo);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTicket(@PathVariable String id) {
        maintenanceService.deleteTicket(id);
        return ResponseEntity.noContent().build();
    }
}
