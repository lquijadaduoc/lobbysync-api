package cl.lobbysync.backend.controller;

import cl.lobbysync.backend.model.mongo.MaintenanceTicket;
import cl.lobbysync.backend.service.MaintenanceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tickets")
@Slf4j
@Tag(name = "Maintenance", description = "Gestion de tickets de mantenimiento")
public class MaintenanceController {

    @Autowired
    private MaintenanceService maintenanceService;

    @Operation(
            summary = "Listar tickets",
            description = "Lista tickets de mantenimiento; permite filtrar por estado o edificio."
    )
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

    @Operation(
            summary = "Obtener ticket",
            description = "Recupera un ticket de mantenimiento por su ID."
    )
    @GetMapping("/{id}")
    public ResponseEntity<MaintenanceTicket> getTicketById(@PathVariable String id) {
        return ResponseEntity.ok(maintenanceService.getTicketById(id));
    }

    @Operation(
            summary = "Crear ticket",
            description = "Crea un nuevo ticket de mantenimiento."
    )
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
    @Operation(
            summary = "Actualizar estado de ticket",
            description = "Actualiza el estado y resolucion de un ticket de mantenimiento."
    )
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

    @Operation(
            summary = "Asignar ticket",
            description = "Asigna un ticket de mantenimiento a un usuario."
    )
    @PutMapping("/{id}/assign")
    public ResponseEntity<MaintenanceTicket> assignTicket(
            @PathVariable String id,
            @RequestBody Map<String, Long> request) {
        
        Long assignedTo = request.get("assignedTo");
        log.info("Assigning ticket {} to user: {}", id, assignedTo);
        
        MaintenanceTicket updated = maintenanceService.assignTicket(id, assignedTo);
        return ResponseEntity.ok(updated);
    }

    @Operation(
            summary = "Eliminar ticket",
            description = "Elimina un ticket de mantenimiento por su ID."
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTicket(@PathVariable String id) {
        maintenanceService.deleteTicket(id);
        return ResponseEntity.noContent().build();
    }
}
