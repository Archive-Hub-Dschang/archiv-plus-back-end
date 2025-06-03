package com.lde.usermicroservice.clients;

import com.lde.usermicroservice.dto.ExamDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "academicservice")
public interface ExamClient {
    @GetMapping("/{id}")
    ExamDTO getExamById(@PathVariable String id);
}
