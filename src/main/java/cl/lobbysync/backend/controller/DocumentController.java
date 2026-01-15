package cl.lobbysync.backend.controller;

import cl.lobbysync.backend.model.mongo.DocumentEntity;
import cl.lobbysync.backend.service.DocumentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/documents")
@Tag(name = "Documents", description = "Biblioteca de documentos del edificio")
@Slf4j
public class DocumentController {

    @Autowired
    private DocumentService documentService;

    @Operation(summary = "Listar documentos publicos", description = "Obtiene todos los documentos publicos disponibles")
    @GetMapping
    public ResponseEntity<List<DocumentEntity>> getDocuments() {
        return ResponseEntity.ok(documentService.getPublicDocuments());
    }

    @Operation(summary = "Listar documentos por categoria", description = "Obtiene documentos filtrados por categoria")
    @GetMapping("/category/{category}")
    public ResponseEntity<List<DocumentEntity>> getDocumentsByCategory(@PathVariable String category) {
        return ResponseEntity.ok(documentService.getDocumentsByCategory(category));
    }

    @Operation(summary = "Obtener documento", description = "Obtiene un documento especifico por ID")
    @GetMapping("/{id}")
    public ResponseEntity<DocumentEntity> getDocument(@PathVariable String id) {
        return ResponseEntity.ok(documentService.getDocumentById(id));
    }

    @Operation(summary = "Crear documento", description = "Crea un nuevo documento en la biblioteca")
    @PostMapping
    public ResponseEntity<DocumentEntity> createDocument(@RequestBody DocumentEntity document) {
        DocumentEntity created = documentService.createDocument(document);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(summary = "Actualizar documento", description = "Modifica los datos de un documento")
    @PutMapping("/{id}")
    public ResponseEntity<DocumentEntity> updateDocument(
            @PathVariable String id,
            @RequestBody DocumentEntity document) {
        
        DocumentEntity updated = documentService.updateDocument(id, document);
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "Eliminar documento", description = "Elimina un documento de la biblioteca")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDocument(@PathVariable String id) {
        documentService.deleteDocument(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Descargar documento", description = "Registra una descarga del documento")
    @PostMapping("/{id}/download")
    public ResponseEntity<Void> downloadDocument(@PathVariable String id) {
        documentService.incrementDownloadCount(id);
        return ResponseEntity.ok().build();
    }
}
