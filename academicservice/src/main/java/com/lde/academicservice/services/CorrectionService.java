package com.lde.academicservice.services;

import com.lde.academicservice.models.Correction;
import com.lde.academicservice.models.Document;
import com.lde.academicservice.repositories.CorrectionRepository;
import com.lde.academicservice.repositories.DocumentRepository;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.client.gridfs.GridFSBucket;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.mongodb.core.query.Query;

import java.io.IOException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CorrectionService {

    private final GridFsTemplate gridFsTemplate;
    private final GridFSBucket gridFSBucket;
    private final CorrectionRepository correctionRepository;
    private final DocumentRepository documentRepository;

    public String addCorrection(MultipartFile file, String docId, String name) throws IOException {
        // Vérifie si le sujet existe
        Optional<Document> doc = documentRepository.findByDoc_id(doc_id).stream().findFirst();
        if (doc.isEmpty()) {
            throw new IllegalArgumentException("Le document correspondant a la correction n'existe pas");
        }

        // Vérifie si une correction existe déjà pour ce sujet
        if (correctionRepository.existsByDoc_id(doc_id)) {
            throw new IllegalStateException("Une correction existe deja pour ce document");
        }

        // Stocke le fichier dans GridFS
        DBObject metaData = new BasicDBObject();
        metaData.put("type", file.getContentType());
        ObjectId gridFsFileId = gridFsTemplate.store(file.getInputStream(), file.getOriginalFilename(), file.getContentType(), metaData);

        // Crée et enregistre la correction
        Correction correction = new Correction();
        correction.setDoc_id(docId);
        correction.setFile_path(gridFsFileId.toString());
        correction.setName(name);

        Correction saved = correctionRepository.save(correction);
        return saved.getId();
    }

    public void downloadCorrection(String correctionId, HttpServletResponse response) throws IOException {
        Correction correction = correctionRepository.findById(correctionId).orElseThrow();
        ObjectId fileId = new ObjectId(correction.getFile_path());

        var gridFSFile = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(fileId)));
        if (gridFSFile != null) {
            response.setContentType(gridFSFile.getMetadata().getString("type"));
            response.setHeader("Content-Disposition", "attachment; filename=\"" + correction.getName() + "\"");
            gridFSBucket.downloadToStream(fileId, response.getOutputStream());
        }
    }
    public Optional<Correction> show(String id) {
        return correctionRepository.findById(id);
    }
}
