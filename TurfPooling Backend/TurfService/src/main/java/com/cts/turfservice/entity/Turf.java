package com.cts.turfservice.entity;

import java.time.LocalTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
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
public class Turf {
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Turf name is required")
    @Size(min = 1, max = 100, message = "Turf name must be between 1 and 100 characters")
    private String name;

    @NotNull(message = "Location is required")
    @Size(min = 1, max = 200, message = "Location must be between 1 and 200 characters")
    private String location;

    @NotNull(message = "Sport type is required")
    private String sportType;

    @Min(value = 0, message = "Price per hour must be greater than or equal to 0")
    private double pricePerHour;

    private Long ownerId;
    
    @Size(max = 500, message = "Description must be less than 500 characters")
    private String description;

    private String facilities;

    @Min(value = 8, message = "Capacity must be greater than or equal to 1")
    private int capacity;

    @NotNull(message = "Opening time is required")
    private LocalTime openingTime;

    @NotNull(message = "Closing time is required")
    private LocalTime closingTime;
    private String surfaceType;
}
