package cl.lobbysync.backend.controller;

import cl.lobbysync.backend.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/files")
public class FileController {

    @Autowired
    private StorageService storageService;

    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        String filepath = storageService.uploadFile(file.getBytes(), file.getOriginalFilename());
        return ResponseEntity.ok(Map.of(
            "filepath", filepath,
            "originalFilename", file.getOriginalFilename(),
            "message", "File uploaded successfully"
        ));
    }

    @GetMapping("/download")
    public ResponseEntity<byte[]> downloadFile(@RequestParam String filepath) throws IOException {
        byte[] fileContent = storageService.downloadFile(filepath);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"file\"")
                .body(fileContent);
    }

    @DeleteMapping
    public ResponseEntity<Map<String, String>> deleteFile(@RequestParam String filepath) throws IOException {
        storageService.deleteFile(filepath);
        return ResponseEntity.ok(Map.of(
            "message", "File deleted successfully",
            "filepath", filepath
        ));
    }
}
