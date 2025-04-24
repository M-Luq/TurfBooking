package com.cts.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TurfOwnerUpdateDTO {
    
	private String name;
	
	private String email;

	private String phoneNumber;

	private String address;
    
	private String businessAddress;
	private String businessContact;

}