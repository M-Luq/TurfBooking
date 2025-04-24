package com.cts.userservice.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.cts.userservice.clients.AuthServiceClient;
import com.cts.userservice.dto.CustomerDTO;
import com.cts.userservice.entity.Customer;
import com.cts.userservice.repository.CustomerRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import feign.FeignException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final AuthServiceClient client;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public Customer saveCustomer(String username, Customer customer) {
        if (username == null || customer == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username or customer details cannot be null");
        }

        try {
            String email = client.getEmail(username);
            if (email == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with this email in login. Try signup and login, then register.");
            }
            customer.setEmail(email);

            Long userId = client.getUserId(username);
            if (userId == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found. Try signup and login, then register.");
            }
            customer.setCustomerId(userId);

            Optional<Customer> existingCustomerByPhoneNumber = customerRepository.findByPhoneNumber(customer.getPhoneNumber());
            if (existingCustomerByPhoneNumber.isPresent()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Phone number already exists");
            }

            customer.setRegisteredAt(LocalDateTime.now());
            return customerRepository.save(customer);
        } catch (FeignException e) {
            String errorMessage = extractErrorMessage(e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error communicating with auth service: " + errorMessage);
        }
    }

    @Override
    @Transactional
    public Customer updateCustomer(Long id, CustomerDTO updatedDetails) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found with id " + id));

        if (updatedDetails == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Updated details cannot be null");
        }

        try {
            if (updatedDetails.getEmail() != null && !updatedDetails.getEmail().equals(customer.getEmail())) {
                client.updateUserEmail(customer.getCustomerId(), updatedDetails.getEmail());
                customer.setEmail(updatedDetails.getEmail());
            }

            if (updatedDetails.getPhoneNumber() != null && !updatedDetails.getPhoneNumber().equals(customer.getPhoneNumber())) {
                if (customerRepository.existsByPhoneNumber(updatedDetails.getPhoneNumber())) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Phone number already exists");
                }
                customer.setPhoneNumber(updatedDetails.getPhoneNumber());
            }

            if (updatedDetails.getAddress() != null) {
                customer.setAddress(updatedDetails.getAddress());
            }
            if (updatedDetails.getName() != null) {
                customer.setName(updatedDetails.getName());
            }

            customer.setUpdatedAt(LocalDateTime.now());
            return customerRepository.save(customer);
        } catch (FeignException e) {
            String errorMessage = extractErrorMessage(e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error communicating with auth service: " + errorMessage);
        }
    }

    @Override
    @Transactional
    public String addFavouriteTurf(Long customerId, Long turfId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found"));

        customer.setFavouriteTurfs(updateFavoriteTurfs(customer.getFavouriteTurfs(), turfId, true));
        customerRepository.save(customer);
        return "Turf is added to favourites";
    }

    @Override
    @Transactional
    public String removeFavouriteTurf(Long customerId, Long turfId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found"));

        customer.setFavouriteTurfs(updateFavoriteTurfs(customer.getFavouriteTurfs(), turfId, false));
        customerRepository.save(customer);
        return "Turf was removed from favourites";
    }
    
    

    private String updateFavoriteTurfs(String favoriteTurfs, Long turfId, boolean add) {
        List<String> favoriteTurfList = (favoriteTurfs == null || favoriteTurfs.isEmpty()) ?
                new ArrayList<>() : new ArrayList<>(Arrays.asList(favoriteTurfs.split(",")));

        if (add && !favoriteTurfList.contains(String.valueOf(turfId))) {
            favoriteTurfList.add(String.valueOf(turfId));
        } else if (!add && favoriteTurfList.contains(String.valueOf(turfId))) {
            favoriteTurfList.remove(String.valueOf(turfId));
        }

        return String.join(",", favoriteTurfList);
    }

    @Override
    public Customer getCustomerById(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found with id: " + id));
    }

    @Override
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    @Override
    @Transactional
    public void deleteCustomer(Long customerId) {
        customerRepository.findById(customerId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found"));
        try {
            client.deleteUser(customerId);
            customerRepository.deleteById(customerId);
        } catch (FeignException e) {
            String errorMessage = extractErrorMessage(e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error communicating with auth service: " + errorMessage);
        }
    }

    @Override
    public boolean isRegistered(Long customerId) {
        return customerRepository.existsByCustomerIdAndRegisteredAtIsNotNull(customerId);
    }
    
    
    @Override
    public void removeTurfFromAllCustomers(Long turfId) {
        List<Customer> customers = customerRepository.findAll();
        for (Customer customer : customers) {
            String favouriteTurfs = customer.getFavouriteTurfs();
            if (favouriteTurfs == null) {
                break;
            }
            if (favouriteTurfs.contains(String.valueOf(turfId))) {
                customer.setFavouriteTurfs(updateFavoriteTurfs(favouriteTurfs, turfId, false));
                customerRepository.save(customer);
            }
        }
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    private String extractErrorMessage(FeignException e) {
        try {
            String responseBody = e.contentUTF8();
            if (responseBody != null && !responseBody.isEmpty()) {
                Map<String, Object> errorMap = objectMapper.readValue(responseBody, Map.class);
                if (errorMap.containsKey("message")) {
                    return (String) errorMap.get("message");
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return e.getMessage();
    }
		
}