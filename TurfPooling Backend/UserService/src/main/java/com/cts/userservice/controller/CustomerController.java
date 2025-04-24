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

import com.cts.userservice.dto.CustomerDTO;
import com.cts.userservice.entity.Customer;
import com.cts.userservice.service.CustomerServiceImpl;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerServiceImpl customerService;

    @PostMapping("/create/{username}")
    public ResponseEntity<Customer> createCustomer(@PathVariable String username,@Valid @RequestBody Customer customer) {
        Customer savedCustomer = customerService.saveCustomer(username,customer);
        return ResponseEntity.ok(savedCustomer);
    }
    
    @PutMapping("/update/{id}")
    public ResponseEntity<Customer> updateCustomer(@PathVariable Long id, @Valid @RequestBody CustomerDTO updatedDetails) {
        Customer updatedCustomer = customerService.updateCustomer(id, updatedDetails);
        return ResponseEntity.ok(updatedCustomer);
    }

    
    @GetMapping("/get/{id}")
    public ResponseEntity<Customer> getCustomerById(@PathVariable Long id) {
        Customer customer = customerService.getCustomerById(id);
        return ResponseEntity.ok(customer);
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<Customer>> getAllCustomers() {
        List<Customer> customers = customerService.getAllCustomers();
        return ResponseEntity.ok(customers);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping("/{customerId}/favorites/{turfId}")
    public ResponseEntity<String> addFavouriteTurf(
            @PathVariable Long customerId,
            @PathVariable Long turfId) {
        return ResponseEntity.ok(customerService.addFavouriteTurf(customerId, turfId));
    }
    
    @DeleteMapping("/{customerId}/favorites/{turfId}")
    public ResponseEntity<String> removeFavouriteTurf(
            @PathVariable Long customerId,
            @PathVariable Long turfId) {
        return ResponseEntity.ok(customerService.removeFavouriteTurf(customerId, turfId));
    }
    
    @GetMapping("/registered/{customerId}")
    public ResponseEntity<Boolean> isRegistered(@PathVariable Long customerId){
    	return ResponseEntity.ok(customerService.isRegistered(customerId));
    }
    
    @DeleteMapping("/removeAllFavouriteTurf/{turfId}")
    public ResponseEntity<Void> removeFavouriteTurfFromAllCustomers(@PathVariable Long turfId) {
        customerService.removeTurfFromAllCustomers(turfId);
        return ResponseEntity.noContent().build();
    }
}
