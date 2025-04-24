package com.cts.userservice.clients;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.cts.userservice.dto.TurfDTO;

@FeignClient(name = "TURFSERVICE") // Adjust the URL as needed
public interface TurfServiceClient {

    @GetMapping("/turfs/{id}")
    TurfDTO getTurfById(@PathVariable("id") Long turfId);

    @GetMapping("turfs/getTurf/owner/{ownerId}")
    List<TurfDTO> getTurfsByOwnerId(@PathVariable Long ownerId);
    
    @DeleteMapping("turfs/delete/{id}")
    void deleteTurf(@PathVariable Long id);
}