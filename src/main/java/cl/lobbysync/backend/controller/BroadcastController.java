package cl.lobbysync.backend.controller;

import cl.lobbysync.backend.model.mongo.Broadcast;
import cl.lobbysync.backend.service.BroadcastService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/broadcasts")
@Tag(name = "Broadcasts", description = "Comunicacion masiva a residentes y conserjes")
@Slf4j
public class BroadcastController {

    @Autowired
    private BroadcastService broadcastService;

    @Operation(summary = "Listar broadcasts", description = "Obtiene todos los mensajes broadcast enviados")
    @GetMapping
    public ResponseEntity<List<Broadcast>> getBroadcasts() {
        return ResponseEntity.ok(broadcastService.getAllBroadcasts());
    }

    @Operation(summary = "Obtener broadcast", description = "Obtiene un broadcast especifico por ID")
    @GetMapping("/{id}")
    public ResponseEntity<Broadcast> getBroadcast(@PathVariable String id) {
        return ResponseEntity.ok(broadcastService.getBroadcastById(id));
    }

    @Operation(summary = "Crear broadcast", description = "Envia un nuevo mensaje broadcast")
    @PostMapping
    public ResponseEntity<Broadcast> createBroadcast(@RequestBody Broadcast broadcast) {
        log.info("Creating broadcast: {} to {}", broadcast.getTitle(), broadcast.getTargetAudience());
        Broadcast created = broadcastService.createBroadcast(broadcast);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(summary = "Eliminar broadcast", description = "Elimina un broadcast del historial")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBroadcast(@PathVariable String id) {
        broadcastService.deleteBroadcast(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Obtener estadisticas de broadcasts", description = "Obtiene estadisticas de entrega y lectura de mensajes")
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getBroadcastStats() {
        return ResponseEntity.ok(broadcastService.getBroadcastStats());
    }
}
