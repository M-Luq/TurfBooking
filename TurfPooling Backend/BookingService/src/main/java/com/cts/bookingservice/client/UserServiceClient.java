package com.cts.bookingservice.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "USERSERVICE") // Adjust the URL as needed
public interface UserServiceClient {

    @GetMapping("owners/turfIds")
    List<Long> getTurfIdsByOwner(@RequestParam Long ownerId);
    
    @PostMapping("/customers/addfavourite")
    String addFavouriteTurf(@RequestParam Long customerId,@RequestParam Long turfId);
    
    @DeleteMapping("/customers/deletefavourite")
    String removeFavouriteTurf(@RequestParam Long customerId,@RequestParam Long turfId);   
    
}