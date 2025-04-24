package com.cts.bookingservice.dto;

import java.time.LocalTime;

import lombok.Data;

@Data
public class TurfDTO {
	private Long id;
	private String name;
	private String location;
	private String sportType;
	private Double pricePerHour;
	private Long ownerId;
	private LocalTime openingTime;
	private LocalTime closingTime;
	private String surfaceType;
}