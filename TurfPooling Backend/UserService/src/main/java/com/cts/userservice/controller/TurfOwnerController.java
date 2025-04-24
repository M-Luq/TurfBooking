
package com.cts.userservice.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cts.userservice.dto.TurfOwnerUpdateDTO;
import com.cts.userservice.entity.TurfOwner;
import com.cts.userservice.service.TurfOwnerServiceImpl;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/owners")
@RequiredArgsConstructor
public class TurfOwnerController {
    
	
	private final TurfOwnerServiceImpl turfOwnerService;
	

    @PostMapping("/create/{username}")
    public ResponseEntity<TurfOwner> createTurfOwner(@PathVariable String username,@Valid @RequestBody TurfOwner turfOwner) {
        TurfOwner savedTurfOwnerDTO = turfOwnerService.saveTurfOwner(username,turfOwner);
        return ResponseEntity.ok(savedTurfOwnerDTO);
    }
    
    @PutMapping("/update/{id}")
    public ResponseEntity<TurfOwner> updateTurfOwner(@PathVariable Long id, @Valid @RequestBody TurfOwnerUpdateDTO updatedDetails) {
        TurfOwner updatedOwner = turfOwnerService.updateTurfOwner(id, updatedDetails);
        return ResponseEntity.ok(updatedOwner);
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<TurfOwner> getTurfOwnerById(@PathVariable Long id) {
        TurfOwner turfOwner = turfOwnerService.getTurfOwnerById(id);
        return ResponseEntity.ok(turfOwner);
    }
    

    @GetMapping("/getAll")
    public ResponseEntity<List<TurfOwner>> getAllTurfOwners() {
        List<TurfOwner> turfOwners = turfOwnerService.getAllTurfOwners();
        return ResponseEntity.ok(turfOwners);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteTurfOwner(@PathVariable Long id) {
        turfOwnerService.deleteTurfOwner(id);
        return ResponseEntity.noContent().build();
    }
    
    @PutMapping("/updateOwnedTurfs")
    public ResponseEntity<Void> updateOwnedTurfs(@RequestParam Long ownerId, @RequestParam String turfId) {
        turfOwnerService.updateOwnedTurfs(ownerId, turfId);
        return ResponseEntity.ok().build();
    }
    
    @PutMapping("/removeOwnedTurf")
    public ResponseEntity<Void> removeOwnedTurf(@RequestParam Long ownerId, @RequestParam String turfId) {
        turfOwnerService.removeOwnedTurf(ownerId, turfId);
        return ResponseEntity.ok().build();
    }
    
//    @GetMapping("/{ownerId}/turfs")
//    public ResponseEntity<List<TurfDTO>> getOwnedTurfs(@PathVariable Long ownerId) {
//        List<TurfDTO> turfs = turfOwnerService.getOwnedTurfs(ownerId);
//        return ResponseEntity.ok(turfs);
//    }
    

    @GetMapping("/turfIds")
    public ResponseEntity<List<Long>> getTurfIdsByOwnerId(@RequestParam Long ownerId) {
        List<Long> turfIds = turfOwnerService.getTurfIdsByOwnerId(ownerId);
        return ResponseEntity.ok(turfIds);
    }
    
    @GetMapping("/registered/{ownerId}")
    public ResponseEntity<Boolean> isRegistered(@PathVariable Long ownerId){
    	return ResponseEntity.ok(turfOwnerService.isRegistered(ownerId));
    }
    
    
}