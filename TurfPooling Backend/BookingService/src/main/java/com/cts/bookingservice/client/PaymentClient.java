package com.cts.bookingservice.client;
 
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.cts.bookingservice.dto.PaymentResponse;
 
// Adjust the name and URL as needed.
@FeignClient(name = "PAYMENTSERVICE")
public interface PaymentClient {
 
	@PostMapping("payments/pay")
    PaymentResponse processPayment(@RequestParam Long bookingId, @RequestParam Long userId,@RequestParam Long turfId, @RequestParam Double amount);

    @PostMapping("payments/refund")
    PaymentResponse refundPayment(@RequestParam Long bookingId);
    
    
    @DeleteMapping("payments/delete/{turfId}")
    void deleteByTurfId(@PathVariable Long turfId);
}