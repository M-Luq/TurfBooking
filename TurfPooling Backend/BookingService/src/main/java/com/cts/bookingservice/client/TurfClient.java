package com.cts.bookingservice.client;
 
import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.cts.bookingservice.dto.TurfDTO;
 
// Adjust the name and URL as needed.
@FeignClient(name = "TURFSERVICE")
public interface TurfClient {
 
    @GetMapping("turfs/getTurf/{id}")
    TurfDTO getTurfById(@PathVariable("id") Long turfId);
    
    @GetMapping("turfs/getAll")
    List<TurfDTO> getAllTurfs();
}