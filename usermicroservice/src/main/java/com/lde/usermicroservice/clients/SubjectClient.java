package com.lde.usermicroservice.clients;

import com.lde.usermicroservice.dto.SubjectDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "academicservice")
public interface SubjectClient {
    @GetMapping("/{id}")
    SubjectDTO getSubjectById(@PathVariable String id);
}
