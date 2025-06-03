package com.lde.paymentmicroservice.clients;

import com.lde.usermicroservice.dto.UserDTO;
import com.lde.usermicroservice.models.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "usermicroservice" , url = "http://localhost:8080" )
public interface UserClient {
        @GetMapping("/api/users/{userId}")
        UserDTO getUserById(@PathVariable("userId") Long userId);

}
