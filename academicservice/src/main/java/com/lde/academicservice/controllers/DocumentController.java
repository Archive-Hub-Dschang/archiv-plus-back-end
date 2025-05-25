package com.lde.academicservice.controllers;

import com.lde.academicservice.models.Document;
import com.lde.academicservice.services.DocumentService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;

    // CREATE - Upload document
    @PostMapping("/upload")
    public ResponseEntity<Document> upload(@RequestParam("file") MultipartFile file,
                                           @RequestParam String departmentId,
                                           @RequestParam String subjectId,
                                           @RequestParam String fieldId,
                                           @RequestParam String levelId,
                                           @RequestParam(required = false) String author) throws IOException {
        Document document = documentService.uploadDocument(file, departmentId, subjectId, fieldId,levelId,author);
        return ResponseEntity.ok(document);
    }

    // READ - Get all documents with pagination
    @GetMapping
    public ResponseEntity<Page<Document>> getAllDocuments(Pageable pageable) {
        Page<Document> documents = documentService.getAllDocuments(pageable,true);
        return ResponseEntity.ok(documents);
    }

    // READ - Get document by ID
    @GetMapping("/{id}")
    public ResponseEntity<Document> getDocumentById(@PathVariable String id) {
        Optional<Document> document = documentService.getDocumentById(id);
        return document.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // READ - Get documents by department
    @GetMapping("/department/{departmentId}")
    public ResponseEntity<List<Document>> getDocumentsByDepartment(@PathVariable String departmentId) {
        List<Document> documents = documentService.getDocumentsByDepartment(departmentId);
        return ResponseEntity.ok(documents);
    }
    // READ - Get documents by department
    @GetMapping("/level/{levelId}")
    public ResponseEntity<List<Document>> getDocumentsByLevel(@PathVariable String levelId) {
        List<Document> documents = documentService.getDocumentsByLevel(levelId);
        return ResponseEntity.ok(documents);
    }

    // READ - Get documents by subject
    @GetMapping("/subject/{subjectId}")
    public ResponseEntity<List<Document>> getDocumentsBySubject(@PathVariable String subjectId) {
        List<Document> documents = documentService.getDocumentsBySubject(subjectId);
        return ResponseEntity.ok(documents);
    }

    // READ - Get documents by field
    @GetMapping("/field/{fieldId}")
    public ResponseEntity<List<Document>> getDocumentsByField(@PathVariable String fieldId) {
        List<Document> documents = documentService.getDocumentsByField(fieldId);
        return ResponseEntity.ok(documents);
    }

    // READ - Get documents by author
    @GetMapping("/author/{author}")
    public ResponseEntity<List<Document>> getDocumentsByAuthor(@PathVariable String author) {
        List<Document> documents = documentService.getDocumentsByAuthor(author);
        return ResponseEntity.ok(documents);
    }

    // READ - Search documents by filename
    @GetMapping("/search")
    public ResponseEntity<List<Document>> searchDocuments(@RequestParam String filename) {
        List<Document> documents = documentService.searchDocumentsByFilename(filename);
        return ResponseEntity.ok(documents);
    }

    // READ - Get most downloaded documents
    @GetMapping("/popular")
    public ResponseEntity<List<Document>> getMostDownloadedDocuments(@RequestParam(defaultValue = "10") int limit) {
        List<Document> documents = documentService.getMostDownloadedDocuments(limit);
        return ResponseEntity.ok(documents);
    }

    // UPDATE - Update document metadata
    @PutMapping("/{id}")
    public ResponseEntity<Document> updateDocument(@PathVariable String id,
                                                   @RequestBody Document documentUpdate) {
        try {
            Document updatedDocument = documentService.updateDocument(id, documentUpdate);
            return ResponseEntity.ok(updatedDocument);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // DELETE - Delete document
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDocument(@PathVariable String id) {
        try {
            documentService.deleteDocument(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // DOWNLOAD - Download document with count tracking
    @GetMapping("/download/{id}")
    public void download(@PathVariable String id, HttpServletResponse response) throws IOException {
        documentService.downloadDocument(id, response);
    }

    // DOWNLOAD - Alternative robust download method
    @GetMapping("/download-tracked/{id}")
    public ResponseEntity<String> downloadWithTracking(@PathVariable String id, HttpServletResponse response) {
        try {
            documentService.downloadDocumentWithTracking(id, response);
            return ResponseEntity.ok("Download successful - count incremented");
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Download failed: " + e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    // HIDE/UNHIDE - Hide document
    @PutMapping("/{id}/hide")
    public ResponseEntity<Document> hideDocument(@PathVariable String id,
                                                 @RequestParam String reason,
                                                 @RequestParam String hiddenBy) {
        try {
            Document hiddenDocument = documentService.hideDocument(id, reason, hiddenBy);
            return ResponseEntity.ok(hiddenDocument);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }


    // HIDE/UNHIDE - Unhide document
    @PutMapping("/{id}/unhide")
    public ResponseEntity<Document> unhideDocument(@PathVariable String id,
                                                   @RequestParam String modifiedBy) {
        try {
            Document unhiddenDocument = documentService.unhideDocument(id, modifiedBy);
            return ResponseEntity.ok(unhiddenDocument);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    // READ - Get hidden documents
    @GetMapping("/hidden")
    public ResponseEntity<List<Document>> getHiddenDocuments() {
        List<Document> hiddenDocuments = documentService.getHiddenDocuments();
        return ResponseEntity.ok(hiddenDocuments);
    }

    // GET - Get download statistics
    @GetMapping("/{id}/stats")
    public ResponseEntity<DocumentStats> getDocumentStats(@PathVariable String id) {
        Optional<Document> document = documentService.getDocumentById(id);
        if (document.isPresent()) {
            DocumentStats stats = new DocumentStats(
                    document.get().getId(),
                    document.get().getFileName(),
                    document.get().getDownloadCount(),
                    document.get().getUploadDate()
            );
            return ResponseEntity.ok(stats);
        }
        return ResponseEntity.notFound().build();
    }

    // Inner class for download statistics
    public static class DocumentStats {
        private String id;
        private String fileName;
        private int downloadCount;
        private java.time.LocalDateTime uploadDate;

        public DocumentStats(String id, String fileName, int downloadCount, java.time.LocalDateTime uploadDate) {
            this.id = id;
            this.fileName = fileName;
            this.downloadCount = downloadCount;
            this.uploadDate = uploadDate;
        }

        // Getters
        public String getId() { return id; }
        public String getFileName() { return fileName; }
        public int getDownloadCount() { return downloadCount; }
        public java.time.LocalDateTime getUploadDate() { return uploadDate; }
    }
}