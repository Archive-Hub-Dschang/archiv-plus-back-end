package com.lde.usermicroservice.clients;

import com.lde.usermicroservice.dto.DocumentDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "academicservice")
public interface DocumentClient {
    @GetMapping("/{id}")
    DocumentDTO getDocumentById(@PathVariable String id);
}
