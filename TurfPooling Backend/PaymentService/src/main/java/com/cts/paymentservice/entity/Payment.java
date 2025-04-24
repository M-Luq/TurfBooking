package com.cts.paymentservice.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Payment {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull(message = "Booking ID is required")
	private Long bookingId;

	private Long turfId;

	private Long userId;

	@NotNull(message = "Amount is required")
	@Min(value = 0, message = "Amount must be zero or positive")
	private Double amount;

	private LocalDateTime paymentDate;

	private String status; // "PAID" or "REFUNDED"
}