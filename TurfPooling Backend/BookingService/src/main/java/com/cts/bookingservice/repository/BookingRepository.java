package com.cts.bookingservice.repository;
 
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cts.bookingservice.entity.Booking;
import com.cts.bookingservice.entity.BookingStatus;
 
@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    // Retrieve all bookings for a specific user
    List<Booking> findByUserId(Long userId);
    
    // Retrieve bookings by turf id if needed
    List<Booking> findByTurfId(Long turfId);
    
    List<Booking> findByTurfIdAndStatus(Long turfId, BookingStatus status);
    
    List<Booking> findByTurfIdIn(List<Long> turfIds);
    
    Booking findByTurfIdAndUserIdAndStartTimeAndEndTimeAndStatus(Long turfId, Long userId, LocalTime startTime, LocalTime endTime, BookingStatus status);
    
    
    
}
