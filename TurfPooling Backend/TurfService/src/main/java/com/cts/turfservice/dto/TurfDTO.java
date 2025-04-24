package com.cts.turfservice.dto;

import java.time.LocalTime;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TurfDTO {
	private String name;

	private String location;

	private String sportType;

	private double pricePerHour;

	private String description;

	private String facilities;

	private int capacity;

	private LocalTime openingTime;

	private LocalTime closingTime;

	private String surfaceType;
}
