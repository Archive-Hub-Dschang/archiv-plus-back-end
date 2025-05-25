package com.lde.academicservice.repositories;

import com.lde.academicservice.models.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;


import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DocumentRepository extends MongoRepository<Document, String> {

    // Find documents by department
    List<Document> findByDepartmentId(String departmentId);

    // Find documents by subject
    List<Document> findBySubjectId(String subjectId);

    // Find documents by field
    List<Document> findByFieldId(String fieldId);

    // Find documents by author
    List<Document> findByAuthor(String author);

    // Find documents by level
    List<Document> findByLevelId(String levelId);

    // Search documents by filename (case insensitive)
    List<Document> findByFileNameContainingIgnoreCase(String filename);

    // Find documents by content type
    List<Document> findByContentType(String contentType);

    // Find documents with download count greater than or equal to specified value
    List<Document> findByDownloadCountGreaterThanEqual(int minDownloads);

    // Find documents uploaded between two dates
    List<Document> findByUploadDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    // Find documents uploaded after a specific date
    List<Document> findByUploadDateAfter(LocalDateTime date);

    // Find documents uploaded before a specific date
    List<Document> findByUploadDateBefore(LocalDateTime date);

    // Custom query to find documents by multiple criteria
    @Query("{ 'departmentId': ?0, 'subjectId': ?1 }")
    List<Document> findByDepartmentAndSubject(String departmentId, String subjectId);

    // Custom query to find documents by department and field
    @Query("{ 'departmentId': ?0, 'fieldId': ?1 }")
    List<Document> findByDepartmentAndField(String departmentId, String fieldId);

    // Custom query to find documents by subject and field
    @Query("{ 'subjectId': ?0, 'fieldId': ?1 }")
    List<Document> findBySubjectAndField(String subjectId, String fieldId);

    // Custom query to find top downloaded documents
    @Query(value = "{}", sort = "{ 'downloadCount': -1 }")
    List<Document> findTopByDownloadCount();

    // Custom query to find recent documents
    @Query(value = "{}", sort = "{ 'uploadDate': -1 }")
    List<Document> findRecentDocuments();

    // Find documents by file size range
    List<Document> findByFileSizeBetween(long minSize, long maxSize);

    // Find documents larger than specified size
    List<Document> findByFileSizeGreaterThan(long size);

    // Find documents smaller than specified size
    List<Document> findByFileSizeLessThan(long size);

    // Count documents by department
    long countByDepartmentId(String departmentId);

    // Count documents by subject
    long countBySubjectId(String subjectId);

    // Count documents by field
    long countByFieldId(String fieldId);

    // Count documents by author
    long countByAuthor(String author);

    // Custom aggregation query to get download statistics
    @Query(value = "{ 'departmentId': ?0 }", fields = "{ 'downloadCount': 1, 'fileName': 1 }")
    List<Document> findDownloadStatsByDepartment(String departmentId);

    // Find visible documents (not hidden)
    List<Document> findByIsHiddenFalse();

    // Find visible documents with pagination
    Page<Document> findByIsHiddenFalse(Pageable pageable);

    // Find hidden documents
    List<Document> findByIsHiddenTrue();

    // Find documents by visibility status
    List<Document> findByIsHidden(boolean isHidden);

    // Find visible documents by department
    List<Document> findByDepartmentIdAndIsHiddenFalse(String departmentId);

    // Find visible documents by subject
    List<Document> findBySubjectIdAndIsHiddenFalse(String subjectId);

    // Find visible documents by field
    List<Document> findByFieldIdAndIsHiddenFalse(String fieldId);

    // Count visible documents
    long countByIsHiddenFalse();

    // Count hidden documents
    long countByIsHiddenTrue();
}