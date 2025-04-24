package com.cts.bookingservice.dto;
 
import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
 
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookingRequest {
    private LocalTime startTime;
    private LocalTime endTime;
}