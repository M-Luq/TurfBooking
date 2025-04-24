package com.cts.bookingservice.entity;

import java.time.LocalDateTime;
import java.time.LocalTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ID of the turf being booked
    @NotNull(message = "Turf ID is required")
    @Column(nullable = false)
    private Long turfId;

    @NotBlank(message = "Turf name is required")
    @Column(nullable = false)
    private String turfName;

    // ID of the user making the booking
    @NotNull(message = "User ID is required")
    @Column(nullable = false)
    private Long userId;

    // Date and time of the booking (or slot time)
    @NotNull(message = "Booking date and time is required")
//    @Future(message = "Booking date and time must be in the future")
    @Column(nullable = false)
    private LocalDateTime bookingDateTime;
    
    private LocalTime startTime;
    
    private LocalTime endTime;

    // Additional slot or duration information
    @Size(max = 200, message = "Slot info must be less than 200 characters")
    private String slotInfo;

    // Booking status: PENDING, CONFIRMED, CANCELLED
    @NotNull(message = "Booking status is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus status;

    @NotNull(message = "Turf price is required")
    @Positive(message = "Turf price must be positive")
    private Double turfPrice;
    

 
}