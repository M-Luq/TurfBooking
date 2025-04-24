package com.cts.bookingservice.service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.cts.bookingservice.client.PaymentClient;
import com.cts.bookingservice.client.TurfClient;
import com.cts.bookingservice.client.UserServiceClient;
import com.cts.bookingservice.dto.BookingRequest;
import com.cts.bookingservice.dto.TurfDTO;
import com.cts.bookingservice.entity.Booking;
import com.cts.bookingservice.entity.BookingStatus;
import com.cts.bookingservice.exception.ResourceNotFoundException;
import com.cts.bookingservice.repository.BookingRepository;

import feign.FeignException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final TurfClient turfClient;
    private final PaymentClient paymentClient;
    private final UserServiceClient userServiceClient;

    private static final String BOOKING_NOT_FOUND = "Booking Not Found";

    @Override
    public Booking createBooking(Long turfId, Long customerId, BookingRequest request) {
        TurfDTO turf = turfClient.getTurfById(turfId);
        if (turf == null) {
            throw new ResourceNotFoundException("Turf not found");
        }

        LocalTime startTime = request.getStartTime();
        LocalTime endTime = request.getEndTime();

        long durationInHours = ChronoUnit.HOURS.between(startTime, endTime);
        if (durationInHours <= 0) {
            throw new RuntimeException("Invalid booking duration");
        }

        Double turfPrice = turf.getPricePerHour() * durationInHours;

        // Check for existing pending booking for the same slot
        Booking existingBooking = bookingRepository.findByTurfIdAndUserIdAndStartTimeAndEndTimeAndStatus(
                turfId, customerId, startTime, endTime, BookingStatus.PENDING);

        if (existingBooking != null) {
            // Update the existing pending booking to confirmed
            existingBooking.setStatus(BookingStatus.CONFIRMED);
            existingBooking.setBookingDateTime(LocalDateTime.now());
            existingBooking.setTurfPrice(turfPrice);
            return bookingRepository.save(existingBooking);
        } else {
            // Create a new booking
            Booking booking = Booking.builder()
                    .turfId(turfId)
                    .turfName(turf.getName())
                    .userId(customerId)
                    .bookingDateTime(LocalDateTime.now())
                    .startTime(startTime)
                    .endTime(endTime)
                    .slotInfo(String.format("%d-hour", durationInHours))
                    .status(BookingStatus.PENDING)
                    .turfPrice(turfPrice)
                    .build();

            return bookingRepository.save(booking);
        }
    }

    @Override
    public List<String> availableSlots(Long turfId) {
        TurfDTO turf = turfClient.getTurfById(turfId);
        if (turf == null) {
            throw new ResourceNotFoundException("Turf not found");
        }

        List<String> availableSlots = new ArrayList<>();
        LocalTime currentTime = turf.getOpeningTime();
        LocalTime closingTime = turf.getClosingTime();

        boolean crossesMidnight = closingTime.isBefore(currentTime);

        while (true) {
            LocalTime endTime = currentTime.plusHours(1);

            // Add slot if available
            if (isSlotAvailable(turfId, currentTime, endTime)) {
                availableSlots.add(String.format("%s - %s", currentTime, endTime));
            }

            // Break **before** adding the last invalid slot
            if ((!crossesMidnight && (endTime.equals(closingTime) || endTime.isAfter(closingTime))) ||
                    (crossesMidnight && endTime.equals(closingTime.plusHours(24)))) {
                break;
            }

            currentTime = endTime;

            // Handle crossing midnight
            if (currentTime.equals(LocalTime.MIDNIGHT)) {
                crossesMidnight = false;
            }
        }

        return availableSlots;
    }

    @Override
    public boolean isSlotAvailable(Long turfId, LocalTime startTime, LocalTime endTime) {
        List<Booking> bookings = bookingRepository.findByTurfIdAndStatus(turfId, BookingStatus.CONFIRMED);

        for (Booking booking : bookings) {
            if ((startTime.isBefore(booking.getEndTime()) && endTime.isAfter(booking.getStartTime())) ||
                    startTime.equals(booking.getStartTime()) || endTime.equals(booking.getEndTime())) {
                return false;
            }
        }

        return true;
    }

    @Override
    public Booking processPayment(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException(BOOKING_NOT_FOUND));
        // Call payment microservice
        paymentClient.processPayment(booking.getId(), booking.getUserId(), booking.getTurfId(), booking.getTurfPrice());
        booking.setStatus(BookingStatus.CONFIRMED);
        return bookingRepository.save(booking);
    }

    @Override
    public Booking cancelBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException(BOOKING_NOT_FOUND));
        booking.setStatus(BookingStatus.CANCELLED);
        return bookingRepository.save(booking);
    }

    @Override
    public Booking processRefund(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException(BOOKING_NOT_FOUND));
        // Call payment microservice for refund
        paymentClient.refundPayment(booking.getId());
        booking.setStatus(BookingStatus.CANCELLED);
        return bookingRepository.save(booking);
    }

    @Override
    public Booking getBookingById(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(BOOKING_NOT_FOUND));
    }

    @Override
    public List<Booking> getBookingsByUserId(Long userId) {
        return bookingRepository.findByUserId(userId);
    }

    @Override
    public List<Booking> getBookingsByTurfIds(List<Long> turfIds) {
        return bookingRepository.findByTurfIdIn(turfIds);
    }

    @Override
    public List<Long> getTurfIdsByOwner(Long ownerId) {
        return userServiceClient.getTurfIdsByOwner(ownerId);
    }

    @Override
    public void deleteBookingsByTurf(Long turfId) {
        List<Booking> bookings = bookingRepository.findByTurfId(turfId);
        for (Booking booking : bookings) {
            bookingRepository.delete(booking);
        }
        paymentClient.deleteByTurfId(turfId);
    }

}