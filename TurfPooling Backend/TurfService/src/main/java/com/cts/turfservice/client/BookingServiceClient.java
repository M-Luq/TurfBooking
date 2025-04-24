package com.cts.turfservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "BOOKINGSERVICE") // Replace with your booking service name
public interface BookingServiceClient {

    @DeleteMapping("/bookings/turf/{turfId}")
    void deleteBookingsByTurf(@PathVariable("turfId") Long turfId);
}