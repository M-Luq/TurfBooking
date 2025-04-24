package com.cts.userservice.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "SECURITY-SERVICE")
public interface AuthServiceClient {
    @GetMapping("/auth/getEmail/{username}")
    String getEmail(@PathVariable String username);
    
    @DeleteMapping("auth/delete/{customerId}")
    String deleteUser(@PathVariable Long customerId);
    
    @GetMapping("/auth/getUserId/{username}")
    Long getUserId(@PathVariable String username);
    
    @PutMapping("auth/updateEmail/{ownerId}")
	String updateUserEmail(@PathVariable Long ownerId,@RequestBody String email);
    
   
    
}
