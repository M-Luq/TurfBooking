package com.cts.turfservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name="USERSERVICE")
public interface OwnerServiceClient {
	
	@PutMapping("/owners/updateOwnedTurfs")
    void updateOwnedTurfs(@RequestParam Long ownerId, @RequestParam String turfId);
	
	@PutMapping("/owners/removeOwnedTurf")
    void removeOwnedTurf(@RequestParam Long ownerId, @RequestParam String turfId);
	
	@DeleteMapping("/customers/removeAllFavouriteTurf/{turfId}")
    void removeFavouriteTurfFromAllCustomers(@PathVariable Long turfId);

}
