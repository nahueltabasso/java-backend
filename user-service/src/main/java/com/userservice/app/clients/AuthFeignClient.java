package com.userservice.app.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@FeignClient(name = "auth-service", url = "${auth.service.uri:8091}")
public interface AuthFeignClient {

    @GetMapping("/api/security/auth/get-user-details")
    Map<String, Object> getCurrentUserDetails(@RequestParam("token") String token);

    @DeleteMapping("/api/security/user/{id}")
    public void deleteUser(@RequestHeader("Authorization") String authorization, @PathVariable Long id);

}
