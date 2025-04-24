package com.cts.paymentservice.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cts.paymentservice.client.UserServiceClient;
import com.cts.paymentservice.entity.Payment;
import com.cts.paymentservice.exception.PaymentNotFoundException;
import com.cts.paymentservice.service.PaymentServiceImpl;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentServiceImpl paymentService;
    private final UserServiceClient ownerClient;

    @PostMapping("/pay")
    public ResponseEntity<Payment> processPayment(@RequestParam Long bookingId, @RequestParam Long userId,@RequestParam Long turfId, @RequestParam Double amount) {
        Payment payment = paymentService.processPayment(bookingId, userId,turfId, amount);
        return ResponseEntity.ok(payment);
    }

    @PostMapping("/refund")
    public ResponseEntity<Payment> refundPayment(@RequestParam Long bookingId) throws PaymentNotFoundException {
        Payment payment = paymentService.refundPayment(bookingId);
        return ResponseEntity.ok(payment);
    }
    
    @GetMapping("/turf/{turfId}")
    public ResponseEntity<List<Payment>> getPaymentsByTurfId(
        @PathVariable Long turfId,
        @RequestParam(required = false) String status) {
        
        List<Payment> payments = paymentService.getPaymentsByTurfId(turfId, status);
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Payment>> getPaymentsByUserId(
        @PathVariable Long userId,
        @RequestParam(required = false) String status) {
        
        List<Payment> payments = paymentService.getPaymentsByUserId(userId, status);
        return ResponseEntity.ok(payments);
    }
    

    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<List<Payment>> getPaymentsByOwnerId(
        @PathVariable Long ownerId,
        @RequestParam(required = false) String status) {

        // Call Owner Service to get the list of turf IDs
        List<Long> turfIds = ownerClient.getTurfIdsByOwnerId(ownerId);

        // Call Payment Service to get the list of payments for the retrieved turf IDs and status
        List<Payment> payments = paymentService.getPaymentsByTurfIds(turfIds, status);
        return ResponseEntity.ok(payments);
    }
    
    @DeleteMapping("/delete/{turfId}")
    public ResponseEntity<Void> deleteByTurfId(@PathVariable Long turfId){
    	paymentService.deleteByTurfId(turfId);
    	return ResponseEntity.noContent().build();
    }

    
}