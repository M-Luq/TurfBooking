package com.cts.userservice.entity;

import java.time.LocalDateTime;

import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@MappedSuperclass
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BaseUser {
	
	
    @NotBlank(message = "Name is mandatory")
    @Size(min = 3, max = 100, message = "Name must be between 3 and 100 characters")
    private String name;
    
    
    private String email;

    @NotBlank(message = "Phone number is mandatory")
    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Phone number is invalid")
    private String phoneNumber;

    @NotBlank(message = "Address is mandatory")
    @Size(min = 10, max = 255, message = "Address must be between 10 and 255 characters")
    private String address;

    private LocalDateTime registeredAt;

    private LocalDateTime updatedAt;
}