package com.cts.userservice.service;

import java.util.List;

import com.cts.userservice.dto.CustomerDTO;
import com.cts.userservice.entity.Customer;

public interface CustomerService {

	Customer saveCustomer(String userName,Customer customer);

	Customer getCustomerById(Long id);

	List<Customer> getAllCustomers();

	void deleteCustomer(Long id);

    Customer updateCustomer(Long id, CustomerDTO updatedDetails);
    
    String addFavouriteTurf(Long customerId, Long turfId);
    
	String removeFavouriteTurf(Long customerId, Long turfId);
	
	boolean isRegistered(Long customerId);

	void removeTurfFromAllCustomers(Long turfId);

}
