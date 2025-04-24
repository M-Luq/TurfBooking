package com.cts.paymentservice.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("USERSERVICE")
public interface UserServiceClient {
	
	@GetMapping("/owners/turfIds")
    List<Long> getTurfIdsByOwnerId(@RequestParam Long ownerId);

}
