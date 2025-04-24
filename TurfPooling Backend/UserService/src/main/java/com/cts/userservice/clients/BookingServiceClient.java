package com.cts.userservice.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "BOOKINGSERVICE")
public interface BookingServiceClient {

    @DeleteMapping("/bookings/delete-by-turf-id")
    void deleteBookingsByTurfId(@RequestParam("turfId") Long turfId);

    @DeleteMapping("/bookings/delete-by-turf-owner-id")
    void deleteBookingsByTurfOwnerId(@RequestParam("turfOwnerId") Long turfOwnerId);
}