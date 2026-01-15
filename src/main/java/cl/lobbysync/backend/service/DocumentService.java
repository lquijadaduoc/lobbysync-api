package cl.lobbysync.backend.service;

import cl.lobbysync.backend.model.mongo.DocumentEntity;
import cl.lobbysync.backend.repository.DocumentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class DocumentService {

    @Autowired(required = false)
    private DocumentRepository documentRepository;

    public List<DocumentEntity> getAllDocuments() {
        if (documentRepository == null) return new java.util.ArrayList<>();
        return documentRepository.findAll();
    }

    public List<DocumentEntity> getDocumentsByCategory(String category) {
        if (documentRepository == null) return new java.util.ArrayList<>();
        return documentRepository.findByCategory(category);
    }

    public List<DocumentEntity> getPublicDocuments() {
        if (documentRepository == null) return new java.util.ArrayList<>();
        return documentRepository.findByIsPublic(true);
    }

    public DocumentEntity getDocumentById(String id) {
        if (documentRepository == null) return null;
        return documentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Document not found"));
    }

    public DocumentEntity createDocument(DocumentEntity document) {
        document.setUploadedAt(LocalDateTime.now());
        document.setUpdatedAt(LocalDateTime.now());
        if (document.getDownloadCount() == null) {
            document.setDownloadCount(0);
        }
        if (document.getIsPublic() == null) {
            document.setIsPublic(true);
        }
        return documentRepository.save(document);
    }

    public DocumentEntity updateDocument(String id, DocumentEntity updates) {
        DocumentEntity existing = getDocumentById(id);
        
        if (updates.getTitle() != null) existing.setTitle(updates.getTitle());
        if (updates.getCategory() != null) existing.setCategory(updates.getCategory());
        if (updates.getDescription() != null) existing.setDescription(updates.getDescription());
        if (updates.getIsPublic() != null) existing.setIsPublic(updates.getIsPublic());
        
        existing.setUpdatedAt(LocalDateTime.now());
        return documentRepository.save(existing);
    }

    public void deleteDocument(String id) {
        documentRepository.deleteById(id);
    }

    public void incrementDownloadCount(String id) {
        DocumentEntity document = getDocumentById(id);
        document.setDownloadCount(document.getDownloadCount() + 1);
        documentRepository.save(document);
    }
}
