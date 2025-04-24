package com.cts.bookingservice.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cts.bookingservice.dto.BookingRequest;
import com.cts.bookingservice.entity.Booking;
import com.cts.bookingservice.service.BookingService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    // Create a new booking
    @PostMapping("/book/{turfId}/{customerId}")
    public ResponseEntity<Booking> createBooking(@PathVariable Long  turfId,@PathVariable Long  customerId,@Valid @RequestBody BookingRequest request) {
        Booking booking = bookingService.createBooking(turfId,customerId,request);
        return ResponseEntity.ok(booking);
    }



    // Retrieve a booking by its ID
    @GetMapping("/get/{id}")
    public ResponseEntity<Booking> getBookingById(@PathVariable Long id) {
        Booking booking = bookingService.getBookingById(id);
        return ResponseEntity.ok(booking);
    }

    // Retrieve all bookings for a given user
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Booking>> getBookingsByUser(@PathVariable Long userId) {
        List<Booking> bookings = bookingService.getBookingsByUserId(userId);
        return ResponseEntity.ok(bookings);
    }


//    @GetMapping("/getAll")
//    public ResponseEntity<List<TurfDTO>> getAllTurf(){
//        return ResponseEntity.ok(bookingService.getAllTurfs());
//    }

    @GetMapping("/getAvailableSlots/{turfId}")
    public ResponseEntity<List<String>> availableSlots(@PathVariable Long turfId) {
        return ResponseEntity.ok(bookingService.availableSlots(turfId));
    }

// Process payment for a booking
    @PostMapping("/pay/{bookingId}")
    public ResponseEntity<Booking> processPayment(@PathVariable Long bookingId) {
        Booking booking = bookingService.processPayment(bookingId);
        return ResponseEntity.ok(booking);
    }

    // Process refund for a booking
    @PostMapping("/refund/{bookingId}")
    public ResponseEntity<Booking> processRefund(@PathVariable Long bookingId) {
        Booking booking = bookingService.processRefund(bookingId);
        return ResponseEntity.ok(booking);
    }

    @PostMapping("/cancel/{bookingId}")
    public ResponseEntity<Booking> cancelBooking(@PathVariable Long bookingId) {
        Booking canceledBooking = bookingService.cancelBooking(bookingId);
        return ResponseEntity.ok(canceledBooking);
    }



// Retrieve all bookings for all turfs owned by the owner
    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<List<Booking>> getBookingsByOwner(@PathVariable Long ownerId) {
        List<Long> turfIds = bookingService.getTurfIdsByOwner(ownerId);
        List<Booking> bookings = bookingService.getBookingsByTurfIds(turfIds);
        return ResponseEntity.ok(bookings);
    }

    // Delete all bookings for a given turf
    @DeleteMapping("/turf/{turfId}")
    public ResponseEntity<String> deleteBookingsByTurf(@PathVariable Long turfId) {
        bookingService.deleteBookingsByTurf(turfId);
        return ResponseEntity.ok("Bookings for turf ID " + turfId + " deleted successfully");
    }
}

//@PostMapping("/favourite/{customerId}/{turfId}")
//public ResponseEntity<String> addFavoriteTurf(@PathVariable Long customerId, @PathVariable Long turfId) {
//    return ResponseEntity.ok(bookingService.addFavouriteTurf(customerId, turfId));
//}
//
//@DeleteMapping("/favourite/{customerId}/{turfId}")
//public ResponseEntity<String> removeFavoriteTurf(@PathVariable Long customerId, @PathVariable Long turfId) {
//    return ResponseEntity.ok(bookingService.removeFavouriteTurf(customerId, turfId));
//}