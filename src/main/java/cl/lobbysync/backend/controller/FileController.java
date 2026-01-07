package cl.lobbysync.backend.controller;

import cl.lobbysync.backend.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/files")
@Tag(name = "Files", description = "Carga, descarga y eliminacion de archivos")
public class FileController {

    @Autowired
    private StorageService storageService;

    @Operation(
            summary = "Subir archivo",
            description = "Recibe un archivo multipart, lo almacena y retorna su ubicacion."
    )
    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        String filepath = storageService.uploadFile(file.getBytes(), file.getOriginalFilename());
        return ResponseEntity.ok(Map.of(
            "filepath", filepath,
            "originalFilename", file.getOriginalFilename(),
            "message", "File uploaded successfully"
        ));
    }

    @Operation(
            summary = "Descargar archivo",
            description = "Descarga un archivo almacenado a partir de su ruta."
    )
    @GetMapping("/download")
    public ResponseEntity<byte[]> downloadFile(@RequestParam String filepath) throws IOException {
        byte[] fileContent = storageService.downloadFile(filepath);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"file\"")
                .body(fileContent);
    }

    @Operation(
            summary = "Eliminar archivo",
            description = "Elimina un archivo por la ruta indicada."
    )
    @DeleteMapping
    public ResponseEntity<Map<String, String>> deleteFile(@RequestParam String filepath) throws IOException {
        storageService.deleteFile(filepath);
        return ResponseEntity.ok(Map.of(
            "message", "File deleted successfully",
            "filepath", filepath
        ));
    }
}
