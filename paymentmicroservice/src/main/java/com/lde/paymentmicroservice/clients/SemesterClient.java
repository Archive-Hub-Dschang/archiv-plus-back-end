package com.lde.paymentmicroservice.clients;

import com.lde.paymentmicroservice.dto.SemesterDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ACADEMICSERVICE")
public interface SemesterClient {
    @GetMapping("/api/semesters/dto/{id}")
    SemesterDTO getSemesterById(@PathVariable String id);

}