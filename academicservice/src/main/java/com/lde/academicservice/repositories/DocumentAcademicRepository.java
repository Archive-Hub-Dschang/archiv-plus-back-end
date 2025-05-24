package com.lde.academicservice.repositories;

import com.lde.academicservice.models.DocumentAcademic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;


import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DocumentAcademicRepository extends MongoRepository<DocumentAcademic, String> {

    // Find documents by department
    List<DocumentAcademic> findByDepartmentId(String departmentId);

    // Find documents by subject
    List<DocumentAcademic> findBySubjectId(String subjectId);

    // Find documents by field
    List<DocumentAcademic> findByFieldId(String fieldId);

    // Find documents by author
    List<DocumentAcademic> findByAuthor(String author);

    // Find documents by level
    List<DocumentAcademic> findByLevelId(String levelId);

    // Search documents by filename (case insensitive)
    List<DocumentAcademic> findByFileNameContainingIgnoreCase(String filename);

    // Find documents by content type
    List<DocumentAcademic> findByContentType(String contentType);

    // Find documents with download count greater than or equal to specified value
    List<DocumentAcademic> findByDownloadCountGreaterThanEqual(int minDownloads);

    // Find documents uploaded between two dates
    List<DocumentAcademic> findByUploadDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    // Find documents uploaded after a specific date
    List<DocumentAcademic> findByUploadDateAfter(LocalDateTime date);

    // Find documents uploaded before a specific date
    List<DocumentAcademic> findByUploadDateBefore(LocalDateTime date);

    // Custom query to find documents by multiple criteria
    @Query("{ 'departmentId': ?0, 'subjectId': ?1 }")
    List<DocumentAcademic> findByDepartmentAndSubject(String departmentId, String subjectId);

    // Custom query to find documents by department and field
    @Query("{ 'departmentId': ?0, 'fieldId': ?1 }")
    List<DocumentAcademic> findByDepartmentAndField(String departmentId, String fieldId);

    // Custom query to find documents by subject and field
    @Query("{ 'subjectId': ?0, 'fieldId': ?1 }")
    List<DocumentAcademic> findBySubjectAndField(String subjectId, String fieldId);

    // Custom query to find top downloaded documents
    @Query(value = "{}", sort = "{ 'downloadCount': -1 }")
    List<DocumentAcademic> findTopByDownloadCount();

    // Custom query to find recent documents
    @Query(value = "{}", sort = "{ 'uploadDate': -1 }")
    List<DocumentAcademic> findRecentDocuments();

    // Find documents by file size range
    List<DocumentAcademic> findByFileSizeBetween(long minSize, long maxSize);

    // Find documents larger than specified size
    List<DocumentAcademic> findByFileSizeGreaterThan(long size);

    // Find documents smaller than specified size
    List<DocumentAcademic> findByFileSizeLessThan(long size);

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
    List<DocumentAcademic> findDownloadStatsByDepartment(String departmentId);

    // Find visible documents (not hidden)
    List<DocumentAcademic> findByIsHiddenFalse();

    // Find visible documents with pagination
    Page<DocumentAcademic> findByIsHiddenFalse(Pageable pageable);

    // Find hidden documents
    List<DocumentAcademic> findByIsHiddenTrue();

    // Find documents by visibility status
    List<DocumentAcademic> findByIsHidden(boolean isHidden);

    // Find visible documents by department
    List<DocumentAcademic> findByDepartmentIdAndIsHiddenFalse(String departmentId);

    // Find visible documents by subject
    List<DocumentAcademic> findBySubjectIdAndIsHiddenFalse(String subjectId);

    // Find visible documents by field
    List<DocumentAcademic> findByFieldIdAndIsHiddenFalse(String fieldId);

    // Count visible documents
    long countByIsHiddenFalse();

    // Count hidden documents
    long countByIsHiddenTrue();
}