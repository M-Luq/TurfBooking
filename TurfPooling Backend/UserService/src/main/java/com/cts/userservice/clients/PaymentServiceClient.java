package com.cts.userservice.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "PAYMENTSERVICE")
public interface PaymentServiceClient {

    @DeleteMapping("/payments/delete-by-turf-id")
    void deletePaymentsByTurfId(@RequestParam("turfId") Long turfId);

    @DeleteMapping("/payments/delete-by-turf-owner-id")
    void deletePaymentsByTurfOwnerId(@RequestParam("turfOwnerId") Long turfOwnerId);
}