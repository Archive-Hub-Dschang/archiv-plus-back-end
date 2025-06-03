package com.lde.paymentmicroservice.clients;

import com.lde.academicservice.dto.SemesterDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "academicmicroservice")
public interface SemesterClient {
    @GetMapping("/api/semesters/{id}")
    SemesterDTO getSemesterById(@PathVariable String id);

}