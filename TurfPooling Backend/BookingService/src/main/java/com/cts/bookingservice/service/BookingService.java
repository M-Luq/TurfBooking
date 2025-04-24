package com.cts.bookingservice.service;

import java.time.LocalTime;
import java.util.List;

import com.cts.bookingservice.dto.BookingRequest;
import com.cts.bookingservice.entity.Booking;

public interface BookingService {
    Booking createBooking(Long turfId, Long customerId, BookingRequest request);
    Booking getBookingById(Long id);
    List<Booking> getBookingsByUserId(Long userId);
    boolean isSlotAvailable(Long turfId, LocalTime startTime, LocalTime endTime);
//    List<TurfDTO> getAllTurfs() ;
    List<String> availableSlots(Long turfId);
    Booking processRefund(Long bookingId);
    Booking processPayment(Long bookingId);
    List<Booking> getBookingsByTurfIds(List<Long> turfIds);
    List<Long> getTurfIdsByOwner(Long ownerId);
//    String addFavouriteTurf(Long customerId,Long turfId);
//    String removeFavouriteTurf(Long customerId,Long turfId);
	Booking cancelBooking(Long bookingId);
	void deleteBookingsByTurf(Long turfId);
}