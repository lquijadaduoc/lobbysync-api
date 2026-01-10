package cl.lobbysync.backend.controller;

import cl.lobbysync.backend.model.sql.LogbookEntry;
import cl.lobbysync.backend.service.LogbookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/logbook")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Logbook", description = "Endpoints para la bitácora del conserje")
@CrossOrigin(origins = "*")
public class LogbookController {

    private final LogbookService logbookService;

    @GetMapping
    @Operation(summary = "Listar todas las entradas de bitácora")
    public ResponseEntity<List<LogbookEntry>> getAllEntries(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer limit
    ) {
        log.info("GET /logbook - page: {}, limit: {}", page, limit);
        
        if (page != null && limit != null) {
            Page<LogbookEntry> pageResult = logbookService.getEntriesPaginated(page, limit);
            return ResponseEntity.ok(pageResult.getContent());
        }
        
        List<LogbookEntry> entries = logbookService.getAllEntries();
        
        // Apply limit if provided
        if (limit != null && limit > 0 && entries.size() > limit) {
            entries = entries.subList(0, limit);
        }
        
        return ResponseEntity.ok(entries);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener entrada de bitácora por ID")
    public ResponseEntity<LogbookEntry> getEntryById(@PathVariable Long id) {
        log.info("GET /logbook/{}", id);
        return logbookService.getEntryById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Crear nueva entrada de bitácora")
    public ResponseEntity<LogbookEntry> createEntry(@RequestBody LogbookEntry entry) {
        log.info("POST /logbook - user: {}, note: {}", entry.getUser(), entry.getNote());
        
        if (entry.getNote() == null || entry.getNote().trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        if (entry.getUser() == null || entry.getUser().trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        LogbookEntry created = logbookService.createEntry(entry);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar entrada de bitácora")
    public ResponseEntity<LogbookEntry> updateEntry(
            @PathVariable Long id,
            @RequestBody LogbookEntry entry
    ) {
        log.info("PUT /logbook/{}", id);
        try {
            LogbookEntry updated = logbookService.updateEntry(id, entry);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            log.error("Error updating logbook entry: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar entrada de bitácora")
    public ResponseEntity<Void> deleteEntry(@PathVariable Long id) {
        log.info("DELETE /logbook/{}", id);
        try {
            logbookService.deleteEntry(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            log.error("Error deleting logbook entry: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/date/{date}")
    @Operation(summary = "Obtener entradas de bitácora por fecha")
    public ResponseEntity<List<LogbookEntry>> getEntriesByDate(@PathVariable String date) {
        log.info("GET /logbook/date/{}", date);
        try {
            LocalDateTime startOfDay = LocalDateTime.parse(date + "T00:00:00");
            LocalDateTime endOfDay = LocalDateTime.parse(date + "T23:59:59");
            List<LogbookEntry> entries = logbookService.getEntriesByDateRange(startOfDay, endOfDay);
            return ResponseEntity.ok(entries);
        } catch (Exception e) {
            log.error("Error parsing date: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
}
