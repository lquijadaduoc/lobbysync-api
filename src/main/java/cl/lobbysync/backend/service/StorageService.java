package cl.lobbysync.backend.service;

import org.springframework.stereotype.Service;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class StorageService {

    private static final String UPLOAD_DIR = "uploads/";

    public String uploadFile(byte[] fileContent, String originalFilename) throws IOException {
        File directory = new File(UPLOAD_DIR);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        String filename = UUID.randomUUID() + "_" + originalFilename;
        Path filepath = Paths.get(UPLOAD_DIR, filename);
        Files.write(filepath, fileContent);
        return filepath.toString();
    }

    public byte[] downloadFile(String filepath) throws IOException {
        return Files.readAllBytes(Paths.get(filepath));
    }

    public void deleteFile(String filepath) throws IOException {
        Files.deleteIfExists(Paths.get(filepath));
    }
}
