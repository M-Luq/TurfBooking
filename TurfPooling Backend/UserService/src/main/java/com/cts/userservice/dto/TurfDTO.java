package com.cts.userservice.dto;
import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TurfDTO {
    
	private long id;
	
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

