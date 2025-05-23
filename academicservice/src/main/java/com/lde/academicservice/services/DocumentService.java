package com.lde.academicservice.services;

import com.lde.academicservice.models.Document;
import com.lde.academicservice.repositories.DocumentRepository;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.model.GridFSFile;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.mongodb.core.query.Query;


import java.io.IOException;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private final GridFsTemplate gridFsTemplate;
    private final GridFSBucket gridFSBucket;
    private final DocumentRepository documentRepository;

    public String uploadDocument(MultipartFile file, String departmentId, String subjectId, String fieldId) throws IOException {
        DBObject metaData = new BasicDBObject();
        metaData.put("type", file.getContentType());

        ObjectId gridFsFileId = gridFsTemplate.store(file.getInputStream(), file.getOriginalFilename(), file.getContentType(), metaData);

        Document doc = new Document();
        doc.setFileName(file.getOriginalFilename());
        doc.setContentType(file.getContentType());
        doc.setFileSize(file.getSize());
        doc.setUploadDate(LocalDateTime.now());
        doc.setGridFsId(gridFsFileId.toString());
        doc.setDepartmentId(departmentId);
        doc.setSubjectId(subjectId);
        doc.setFieldId(fieldId);

        documentRepository.save(doc);
        return doc.getId();
    }

    public void downloadDocument(String docId, HttpServletResponse response) throws IOException {
        Document doc = documentRepository.findById(docId).orElseThrow();
        GridFSFile gridFSFile = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(doc.getGridFsId())));

        if (gridFSFile != null) {
            response.setContentType(doc.getContentType());
            response.setHeader("Content-Disposition", "attachment; filename=\"" + doc.getFileName() + "\"");
            gridFSBucket.downloadToStream(gridFSFile.getObjectId(), response.getOutputStream());
        }
    }
}
