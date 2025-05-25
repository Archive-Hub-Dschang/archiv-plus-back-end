package com.lde.academicservice.services;

import com.lde.academicservice.models.Document;
import com.lde.academicservice.repositories.DocumentRepository;
import com.mongodb.client.gridfs.model.GridFSFile;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final GridFsTemplate gridFsTemplate;
    private final GridFsOperations gridFsOperations;

    // CREATE - Upload document
    public Document uploadDocument(MultipartFile file, String departmentId, String subjectId,
                                   String fieldId, String levelId, String author) throws IOException {
        // Store file in GridFS
        String gridFsId = gridFsTemplate.store(
                file.getInputStream(),
                file.getOriginalFilename(),
                file.getContentType()
        ).toString();

        // Create document metadata
        Document document = new Document();
        document.setFileName(file.getOriginalFilename());
        document.setContentType(file.getContentType());
        document.setFileSize(file.getSize());
        document.setGridFsId(gridFsId);
        document.setDepartmentId(departmentId);
        document.setSubjectId(subjectId);
        document.setLevelId(levelId);
        document.setFieldId(fieldId);
        document.setAuthor(StringUtils.hasText(author) ? author : "Anonymous");

        return documentRepository.save(document);
    }

    // READ - Get all documents with pagination (with option to include hidden)
    public Page<Document> getAllDocuments(Pageable pageable, boolean includeHidden) {
        if (includeHidden) {
            return documentRepository.findAll(pageable);
        } else {
            return documentRepository.findByIsHiddenFalse(pageable);
        }
    }

    // READ - Get document by ID
    public Optional<Document> getDocumentById(String id) {
        return documentRepository.findById(id);
    }

    // READ - Get documents by department
    public List<Document> getDocumentsByDepartment(String departmentId) {
        return documentRepository.findByDepartmentId(departmentId);
    }

    // READ - Get documents by subject
    public List<Document> getDocumentsBySubject(String subjectId) {
        return documentRepository.findBySubjectId(subjectId);
    }

    // READ - Get documents by field
    public List<Document> getDocumentsByField(String fieldId) {
        return documentRepository.findByFieldId(fieldId);
    }

    // READ - Get documents by author
    public List<Document> getDocumentsByAuthor(String author) {
        return documentRepository.findByAuthor(author);
    }
    // READ - Get documents by Level
    public List<Document> getDocumentsByLevel(String levelId) {
        return documentRepository.findByLevelId(levelId);
    }
    // READ - Search documents by filename
    public List<Document> searchDocumentsByFilename(String filename) {
        return documentRepository.findByFileNameContainingIgnoreCase(filename);
    }

    // READ - Get most downloaded documents
    public List<Document> getMostDownloadedDocuments(int limit) {
        Pageable pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "downloadCount"));
        return documentRepository.findAll(pageable).getContent();
    }

    // UPDATE - Update document metadata
    public Document updateDocument(String id, Document documentUpdate) {
        Optional<Document> existingDoc = documentRepository.findById(id);
        if (existingDoc.isPresent()) {
            Document document = existingDoc.get();

            // Update only non-null fields
            if (StringUtils.hasText(documentUpdate.getAuthor())) {
                document.setAuthor(documentUpdate.getAuthor());
            }
            if (StringUtils.hasText(documentUpdate.getDepartmentId())) {
                document.setDepartmentId(documentUpdate.getDepartmentId());
            }
            if (StringUtils.hasText(documentUpdate.getSubjectId())) {
                document.setSubjectId(documentUpdate.getSubjectId());
            }
            if (StringUtils.hasText(documentUpdate.getFieldId())) {
                document.setFieldId(documentUpdate.getFieldId());
            }
            if (StringUtils.hasText(documentUpdate.getLevelId())) {
                document.setLevelId(documentUpdate.getLevelId());
            }

            return documentRepository.save(document);
        }
        throw new RuntimeException("Document not found with id: " + id);
    }

    // DELETE - Delete document
    public void deleteDocument(String id) {
        Optional<Document> document = documentRepository.findById(id);
        if (document.isPresent()) {
            // Delete file from GridFS
            gridFsTemplate.delete(Query.query(Criteria.where("_id").is(document.get().getGridFsId())));

            // Delete document metadata
            documentRepository.deleteById(id);
        } else {
            throw new RuntimeException("Document not found with id: " + id);
        }
    }

    // DOWNLOAD - Download document with count tracking AFTER successful download
    public void downloadDocument(String id, HttpServletResponse response) throws IOException {
        Optional<Document> documentOpt = documentRepository.findById(id);
        if (documentOpt.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        Document document = documentOpt.get();

        // Check if document is hidden
        if (document.isHidden()) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        // Find file in GridFS
        GridFSFile gridFSFile = gridFsTemplate.findOne(
                Query.query(Criteria.where("_id").is(document.getGridFsId()))
        );

        if (gridFSFile == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        // Set response headers
        response.setContentType(document.getContentType());
        response.setHeader("Content-Disposition",
                "attachment; filename=\"" + document.getFileName() + "\"");
        response.setContentLengthLong(document.getFileSize());

        boolean downloadSuccessful = false;

        // Stream file content
        try (InputStream inputStream = gridFsOperations.getResource(gridFSFile).getInputStream()) {
            inputStream.transferTo(response.getOutputStream());
            response.flushBuffer();
            downloadSuccessful = true;
        } catch (IOException e) {
            // Log error but don't increment counter if download failed
            throw e;
        }

        // Increment download count ONLY AFTER successful download
        if (downloadSuccessful) {
            document.incrementDownloadCount();
            documentRepository.save(document);
        }
    }

    // Alternative method with more robust download tracking
    public void downloadDocumentWithTracking(String id, HttpServletResponse response) throws IOException {
        Document document = getDocumentById(id)
                .orElseThrow(() -> new RuntimeException("Document not found"));

        GridFSFile gridFSFile = gridFsTemplate.findOne(
                Query.query(Criteria.where("_id").is(document.getGridFsId()))
        );

        if (gridFSFile == null) {
            throw new RuntimeException("File not found in GridFS");
        }

        // Prepare response headers
        response.setContentType(document.getContentType());
        response.setHeader("Content-Disposition",
                "attachment; filename=\"" + document.getFileName() + "\"");
        response.setContentLengthLong(document.getFileSize());

        // Stream file and track download on success
        streamFileWithCallback(gridFSFile, response, () -> {
            // This callback is executed ONLY after successful download
            document.incrementDownloadCount();
            documentRepository.save(document);
        });
    }

    // Helper method to stream file with success callback
    private void streamFileWithCallback(GridFSFile gridFSFile, HttpServletResponse response, Runnable onSuccess) throws IOException {
        try (InputStream inputStream = gridFsOperations.getResource(gridFSFile).getInputStream()) {
            long totalBytes = 0;
            byte[] buffer = new byte[8192];
            int bytesRead;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                response.getOutputStream().write(buffer, 0, bytesRead);
                totalBytes += bytesRead;
            }

            response.getOutputStream().flush();

            // Execute callback only if entire file was streamed successfully
            if (totalBytes > 0) {
                onSuccess.run();
            }
        }
    }

    // UTILITY - Get download statistics
    public List<Document> getDocumentsByDownloadCount(int minDownloads) {
        return documentRepository.findByDownloadCountGreaterThanEqual(minDownloads);
    }

    // UTILITY - Get recent documents
    public List<Document> getRecentDocuments(int limit) {
        Pageable pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "uploadDate"));
        return documentRepository.findAll(pageable).getContent();
    }

    // UTILITY - Get documents uploaded in date range
    public List<Document> getDocumentsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return documentRepository.findByUploadDateBetween(startDate, endDate);
    }

    // UTILITY - Get total documents count
    public long getTotalDocumentsCount() {
        return documentRepository.count();
    }

    // UTILITY - Get total downloads count
    public long getTotalDownloadsCount() {
        return documentRepository.findAll().stream()
                .mapToLong(Document::getDownloadCount)
                .sum();
    }

    // HIDE/UNHIDE - Hide document
    public Document hideDocument(String id, String reason, String hiddenBy) {
        Optional<Document> documentOpt = documentRepository.findById(id);
        if (documentOpt.isPresent()) {
            Document document = documentOpt.get();
            document.hide(reason, hiddenBy);
            return documentRepository.save(document);
        }
        throw new RuntimeException("Document not found with id: " + id);
    }

    // HIDE/UNHIDE - Unhide document
    public Document unhideDocument(String id, String modifiedBy) {
        Optional<Document> documentOpt = documentRepository.findById(id);
        if (documentOpt.isPresent()) {
            Document document = documentOpt.get();
            document.unhide(modifiedBy);
            return documentRepository.save(document);
        }
        throw new RuntimeException("Document not found with id: " + id);
    }

    // READ - Get hidden documents
    public List<Document> getHiddenDocuments() {
        return documentRepository.findByIsHiddenTrue();
    }

    // READ - Get visible documents only
    public List<Document> getVisibleDocuments() {
        return documentRepository.findByIsHiddenFalse();
    }


}