package com.lde.academicservice.services;

import com.lde.academicservice.models.DocumentAcademic;
import com.lde.academicservice.repositories.DocumentAcademicRepository;
import com.mongodb.client.gridfs.model.GridFSFile;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DocumentServiceTest {

    @Mock
    private DocumentAcademicRepository documentRepository;

    @Mock
    private GridFsTemplate gridFsTemplate;

    @Mock
    private GridFsOperations gridFsOperations;

    @Mock
    private MultipartFile multipartFile;

    @Mock
    private HttpServletResponse httpServletResponse;

    @Mock
    private ServletOutputStream servletOutputStream;

    @InjectMocks
    private DocumentAcademicService documentService;

    private DocumentAcademic testDocument;
    private final String TEST_ID = "test-id-123";
    private final String TEST_GRID_FS_ID = "grid-fs-id-123";

    @BeforeEach
    void setUp() {
        testDocument = new DocumentAcademic();
        testDocument.setId(TEST_ID);
        testDocument.setFileName("test-file.pdf");
        testDocument.setContentType("application/pdf");
        testDocument.setFileSize(1024L);
        testDocument.setGridFsId(TEST_GRID_FS_ID);
        testDocument.setDepartmentId("dept-1");
        testDocument.setSubjectId("subject-1");
        testDocument.setFieldId("field-1");
        testDocument.setAuthor("Test Author");
        testDocument.setDownloadCount(5);
    }

    @Test
    void testUploadDocument_Success() throws IOException {
        // Given
        when(multipartFile.getOriginalFilename()).thenReturn("test-file.pdf");
        when(multipartFile.getContentType()).thenReturn("application/pdf");
        when(multipartFile.getSize()).thenReturn(1024L);
        when(multipartFile.getInputStream()).thenReturn(new ByteArrayInputStream("test content".getBytes()));
        ObjectId fakeId = new ObjectId("507f1f77bcf86cd799439011");
        when(gridFsTemplate.store(any(InputStream.class), anyString(), anyString())).thenReturn(fakeId);
        when(documentRepository.save(any(DocumentAcademic.class))).thenReturn(testDocument);

        // When
        DocumentAcademic result = documentService.uploadDocument(multipartFile, "dept-1", "subject-1", "field-1", "Test Author");

        // Then
        assertNotNull(result);
        assertEquals("test-file.pdf", result.getFileName());
        assertEquals("application/pdf", result.getContentType());
        assertEquals(1024L, result.getFileSize());
        verify(documentRepository).save(any(DocumentAcademic.class));
        verify(gridFsTemplate).store(any(), anyString(), anyString());
    }

    @Test
    void testGetAllDocuments_WithoutHidden() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        List<DocumentAcademic> documents = Arrays.asList(testDocument);
        Page<DocumentAcademic> page = new PageImpl<>(documents, pageable, 1);
        when(documentRepository.findByIsHiddenFalse(pageable)).thenReturn(page);

        // When
        Page<DocumentAcademic> result = documentService.getAllDocuments(pageable, false);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(documentRepository).findByIsHiddenFalse(pageable);
        verify(documentRepository, never()).findAll(pageable);
    }

    @Test
    void testGetAllDocuments_WithHidden() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        List<DocumentAcademic> documents = Arrays.asList(testDocument);
        Page<DocumentAcademic> page = new PageImpl<>(documents, pageable, 1);
        when(documentRepository.findAll(pageable)).thenReturn(page);

        // When
        Page<DocumentAcademic> result = documentService.getAllDocuments(pageable, true);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(documentRepository).findAll(pageable);
        verify(documentRepository, never()).findByIsHiddenFalse(pageable);
    }

    @Test
    void testGetDocumentById_Found() {
        // Given
        when(documentRepository.findById(TEST_ID)).thenReturn(Optional.of(testDocument));

        // When
        Optional<DocumentAcademic> result = documentService.getDocumentById(TEST_ID);

        // Then
        assertTrue(result.isPresent());
        assertEquals(testDocument, result.get());
        verify(documentRepository).findById(TEST_ID);
    }

    @Test
    void testGetDocumentById_NotFound() {
        // Given
        when(documentRepository.findById(TEST_ID)).thenReturn(Optional.empty());

        // When
        Optional<DocumentAcademic> result = documentService.getDocumentById(TEST_ID);

        // Then
        assertFalse(result.isPresent());
        verify(documentRepository).findById(TEST_ID);
    }

    @Test
    void testUpdateDocument_Success() {
        // Given
        DocumentAcademic updateData = new DocumentAcademic();
        updateData.setAuthor("Updated Author");
        updateData.setDepartmentId("dept-2");

        when(documentRepository.findById(TEST_ID)).thenReturn(Optional.of(testDocument));
        when(documentRepository.save(any(DocumentAcademic.class))).thenReturn(testDocument);

        // When
        DocumentAcademic result = documentService.updateDocument(TEST_ID, updateData);

        // Then
        assertNotNull(result);
        verify(documentRepository).findById(TEST_ID);
        verify(documentRepository).save(testDocument);
    }

    @Test
    void testUpdateDocument_NotFound() {
        // Given
        DocumentAcademic updateData = new DocumentAcademic();
        when(documentRepository.findById(TEST_ID)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> documentService.updateDocument(TEST_ID, updateData));
        assertEquals("Document not found with id: " + TEST_ID, exception.getMessage());
        verify(documentRepository).findById(TEST_ID);
        verify(documentRepository, never()).save(any());
    }

    @Test
    void testDeleteDocument_Success() {
        // Given
        when(documentRepository.findById(TEST_ID)).thenReturn(Optional.of(testDocument));

        // When
        documentService.deleteDocument(TEST_ID);

        // Then
        verify(documentRepository).findById(TEST_ID);
        verify(gridFsTemplate).delete(any(Query.class));
        verify(documentRepository).deleteById(TEST_ID);
    }

    @Test
    void testDeleteDocument_NotFound() {
        // Given
        when(documentRepository.findById(TEST_ID)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> documentService.deleteDocument(TEST_ID));
        assertEquals("Document not found with id: " + TEST_ID, exception.getMessage());
        verify(documentRepository).findById(TEST_ID);
        verify(gridFsTemplate, never()).delete(any(Query.class));
        verify(documentRepository, never()).deleteById(anyString());
    }

    @Test
    void testDownloadDocument_Success() throws IOException {
        // Given
        GridFSFile gridFSFile = mock(GridFSFile.class);
        GridFsResource resource = mock(GridFsResource.class);
        ByteArrayInputStream inputStream = new ByteArrayInputStream("test content".getBytes());

        when(documentRepository.findById(TEST_ID)).thenReturn(Optional.of(testDocument));
        when(gridFsTemplate.findOne(any(Query.class))).thenReturn(gridFSFile);
        when(gridFsOperations.getResource(gridFSFile)).thenReturn(resource);
        when(resource.getInputStream()).thenReturn(inputStream);
        when(httpServletResponse.getOutputStream()).thenReturn(servletOutputStream);
        when(documentRepository.save(any(DocumentAcademic.class))).thenReturn(testDocument);

        // When
        documentService.downloadDocument(TEST_ID, httpServletResponse);

        // Then
        verify(documentRepository).findById(TEST_ID);
        verify(gridFsTemplate).findOne(any(Query.class));
        verify(httpServletResponse).setContentType("application/pdf");
        verify(httpServletResponse).setHeader("Content-Disposition", "attachment; filename=\"test-file.pdf\"");
        verify(httpServletResponse).setContentLengthLong(1024L);
        verify(documentRepository).save(testDocument);
        assertEquals(6, testDocument.getDownloadCount()); // Incremented from 5 to 6
    }

    @Test
    void testDownloadDocument_DocumentNotFound() throws IOException {
        // Given
        when(documentRepository.findById(TEST_ID)).thenReturn(Optional.empty());

        // When
        documentService.downloadDocument(TEST_ID, httpServletResponse);

        // Then
        verify(documentRepository).findById(TEST_ID);
        verify(httpServletResponse).setStatus(HttpServletResponse.SC_NOT_FOUND);
        verify(documentRepository, never()).save(any());
    }

    @Test
    void testDownloadDocument_HiddenDocument() throws IOException {
        // Given
        testDocument.setHidden(true);
        when(documentRepository.findById(TEST_ID)).thenReturn(Optional.of(testDocument));

        // When
        documentService.downloadDocument(TEST_ID, httpServletResponse);

        // Then
        verify(documentRepository).findById(TEST_ID);
        verify(httpServletResponse).setStatus(HttpServletResponse.SC_FORBIDDEN);
        verify(documentRepository, never()).save(any());
    }

    @Test
    void testDownloadDocument_FileNotInGridFS() throws IOException {
        // Given
        when(documentRepository.findById(TEST_ID)).thenReturn(Optional.of(testDocument));
        when(gridFsTemplate.findOne(any(Query.class))).thenReturn(null);

        // When
        documentService.downloadDocument(TEST_ID, httpServletResponse);

        // Then
        verify(documentRepository).findById(TEST_ID);
        verify(gridFsTemplate).findOne(any(Query.class));
        verify(httpServletResponse).setStatus(HttpServletResponse.SC_NOT_FOUND);
        verify(documentRepository, never()).save(any());
    }

    @Test
    void testHideDocument_Success() {
        // Given
        when(documentRepository.findById(TEST_ID)).thenReturn(Optional.of(testDocument));
        when(documentRepository.save(any(DocumentAcademic.class))).thenReturn(testDocument);

        // When
        DocumentAcademic result = documentService.hideDocument(TEST_ID, "Inappropriate content", "admin");

        // Then
        assertNotNull(result);
        assertTrue(testDocument.isHidden());
        assertEquals("Inappropriate content", testDocument.getHiddenReason());
        assertEquals("admin", testDocument.getHiddenBy());
        assertNotNull(testDocument.getHiddenDate());
        verify(documentRepository).findById(TEST_ID);
        verify(documentRepository).save(testDocument);
    }

    @Test
    void testHideDocument_NotFound() {
        // Given
        when(documentRepository.findById(TEST_ID)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> documentService.hideDocument(TEST_ID, "reason", "admin"));
        assertEquals("Document not found with id: " + TEST_ID, exception.getMessage());
        verify(documentRepository).findById(TEST_ID);
        verify(documentRepository, never()).save(any());
    }

    @Test
    void testUnhideDocument_Success() {
        // Given
        testDocument.setHidden(true);
        testDocument.setHiddenReason("test reason");
        testDocument.setHiddenBy("admin");
        testDocument.setHiddenDate(LocalDateTime.now());

        when(documentRepository.findById(TEST_ID)).thenReturn(Optional.of(testDocument));
        when(documentRepository.save(any(DocumentAcademic.class))).thenReturn(testDocument);

        // When
        DocumentAcademic result = documentService.unhideDocument(TEST_ID, "admin");

        // Then
        assertNotNull(result);
        assertFalse(result.isHidden());
        assertNull(result.getHiddenReason());
        assertNull(result.getHiddenBy());
        assertNull(result.getHiddenDate());
        verify(documentRepository).findById(TEST_ID);
        verify(documentRepository).save(testDocument);
    }

    @Test
    void testGetHiddenDocuments() {
        // Given
        testDocument.setHidden(true);
        List<DocumentAcademic> hiddenDocs = Arrays.asList(testDocument);
        when(documentRepository.findByIsHiddenTrue()).thenReturn(hiddenDocs);

        // When
        List<DocumentAcademic> result = documentService.getHiddenDocuments();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.get(0).isHidden());
        verify(documentRepository).findByIsHiddenTrue();
    }

    @Test
    void testGetVisibleDocuments() {
        // Given
        List<DocumentAcademic> visibleDocs = Arrays.asList(testDocument);
        when(documentRepository.findByIsHiddenFalse()).thenReturn(visibleDocs);

        // When
        List<DocumentAcademic> result = documentService.getVisibleDocuments();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertFalse(result.get(0).isHidden());
        verify(documentRepository).findByIsHiddenFalse();
    }

    @Test
    void testGetDocumentsByDepartment() {
        // Given
        List<DocumentAcademic> documents = Arrays.asList(testDocument);
        when(documentRepository.findByDepartmentId("dept-1")).thenReturn(documents);

        // When
        List<DocumentAcademic> result = documentService.getDocumentsByDepartment("dept-1");

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("dept-1", result.get(0).getDepartmentId());
        verify(documentRepository).findByDepartmentId("dept-1");
    }

    @Test
    void testGetMostDownloadedDocuments() {
        // Given
        List<DocumentAcademic> documents = Arrays.asList(testDocument);
        Page<DocumentAcademic> page = new PageImpl<>(documents);
    }
}