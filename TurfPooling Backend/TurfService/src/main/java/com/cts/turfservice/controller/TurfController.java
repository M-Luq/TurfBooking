package com.cts.turfservice.controller;


import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cts.turfservice.dto.TurfDTO;
import com.cts.turfservice.entity.Turf;
import com.cts.turfservice.service.TurfServiceImpl;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
 
@RestController
@RequestMapping("/turfs")
@RequiredArgsConstructor
public class TurfController {
	
    private final TurfServiceImpl turfService;
 
    @PostMapping("/add/{ownerId}")
    public ResponseEntity<Turf> addTurf(@PathVariable Long ownerId,@Valid @RequestBody TurfDTO turfDTO) {
        return ResponseEntity.ok(turfService.addTurf(ownerId,turfDTO));
    }
 
    @PutMapping("/update/{turfId}")
    public ResponseEntity<Turf> updateTurf(@PathVariable Long turfId, @Valid @RequestBody TurfDTO turfDTO) {
        return ResponseEntity.ok(turfService.updateTurf(turfId, turfDTO));
    }
 
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteTurf(@PathVariable Long id) {
        turfService.deleteTurf(id);
        return ResponseEntity.noContent().build();
    }
 
    @GetMapping("/getAll")
    public ResponseEntity<List<Turf>> getAllTurfs() {
        return ResponseEntity.ok(turfService.getAllTurfs());
    }
    
    @GetMapping("/getTurf/{id}")
    public ResponseEntity<Turf> getTurfById(@PathVariable Long id){
    	return ResponseEntity.ok(turfService.getTurfById(id));
    }
    
    @GetMapping("/getTurf/owner/{ownerId}")
    public ResponseEntity <List<Turf>> getTurfsByOwnerId(@PathVariable Long ownerId){
    	return ResponseEntity.ok(turfService.getTurfsByOwnerId(ownerId));
    }
    

    
    
}
