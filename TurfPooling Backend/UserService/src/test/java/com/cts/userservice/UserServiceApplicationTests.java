package com.cts.userservice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import com.cts.userservice.clients.AuthServiceClient;
import com.cts.userservice.clients.TurfServiceClient;
import com.cts.userservice.dto.CustomerDTO;
import com.cts.userservice.dto.TurfDTO;
import com.cts.userservice.dto.TurfOwnerUpdateDTO;
import com.cts.userservice.entity.Customer;
import com.cts.userservice.entity.TurfOwner;
import com.cts.userservice.exception.UserNotFoundException;
import com.cts.userservice.repository.CustomerRepository;
import com.cts.userservice.repository.TurfOwnerRepository;
import com.cts.userservice.service.CustomerServiceImpl;
import com.cts.userservice.service.TurfOwnerServiceImpl;

@ExtendWith(MockitoExtension.class)
class UserServiceApplicationTests {

    // CUSTOMER TEST CASES

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private AuthServiceClient authServiceClient;
    
    @Mock 
    private TurfServiceClient turfServiceClient;

    @InjectMocks
    private CustomerServiceImpl customerService;
    
    @Mock
    private TurfOwnerRepository turfOwnerRepository;

    @InjectMocks
    private TurfOwnerServiceImpl turfOwnerService;

    private TurfOwner turfOwner;

    private Customer customer;

    @BeforeEach
     void setUp() {
        customer = new Customer();
        customer.setCustomerId(1L);
        customer.setName("John Doe");
        customer.setEmail("john.doe@example.com");
        customer.setPhoneNumber("9876543210");
        customer.setAddress("123 Main St");
        customer.setRegisteredAt(LocalDateTime.now());
    }

    @Test
     void testSaveCustomer() {
        when(authServiceClient.getEmail(anyString())).thenReturn("john.doe@example.com");
        when(authServiceClient.getUserId(anyString())).thenReturn(1L);
        when(customerRepository.findByPhoneNumber(anyString())).thenReturn(Optional.empty());
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);

        Customer savedCustomer = customerService.saveCustomer("john_doe", customer);

        assertNotNull(savedCustomer);
        assertEquals("john.doe@example.com", savedCustomer.getEmail());
        assertEquals(1L, savedCustomer.getCustomerId());
    }

    @Test
     void testSaveCustomerUserNotFoundException() {
        when(authServiceClient.getEmail(anyString())).thenReturn(null);

        assertThrows(ResponseStatusException.class, () -> {
            customerService.saveCustomer("john_doe", customer);
        });

        when(authServiceClient.getEmail(anyString())).thenReturn("john.doe@example.com");
        when(authServiceClient.getUserId(anyString())).thenReturn(null);

        assertThrows(ResponseStatusException.class, () -> {
            customerService.saveCustomer("john_doe", customer);
        });
    }

    @Test
    public void testUpdateCustomer() {
        when(customerRepository.findById(anyLong())).thenReturn(Optional.of(customer));
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);

        CustomerDTO updatedDetails = new CustomerDTO();
        updatedDetails.setEmail("jane.doe@example.com");
        updatedDetails.setPhoneNumber("1234567890");

        Customer updatedCustomer = customerService.updateCustomer(1L, updatedDetails);

        assertNotNull(updatedCustomer);
        assertEquals("jane.doe@example.com", updatedCustomer.getEmail());
        assertEquals("1234567890", updatedCustomer.getPhoneNumber());
    }

    @Test
     void testAddFavouriteTurf() {
        when(customerRepository.findById(anyLong())).thenReturn(Optional.of(customer));
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);

        String response = customerService.addFavouriteTurf(1L, 101L);

        assertEquals("Turf is added to favourites", response);
        assertTrue(Arrays.asList(customer.getFavouriteTurfs().split(",")).contains("101"));
    }

    @Test
     void testRemoveFavouriteTurf() {
        customer.setFavouriteTurfs("101,102,103");
        when(customerRepository.findById(anyLong())).thenReturn(Optional.of(customer));
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);

        String response = customerService.removeFavouriteTurf(1L, 102L);

        assertEquals("Turf was removed from favourites", response);
        assertFalse(Arrays.asList(customer.getFavouriteTurfs().split(",")).contains("102"));
    }

    @Test
     void testGetCustomerById() {
        when(customerRepository.findById(anyLong())).thenReturn(Optional.of(customer));

        Customer foundCustomer = customerService.getCustomerById(1L);

        assertNotNull(foundCustomer);
        assertEquals(1L, foundCustomer.getCustomerId());
    }

   
    
    
    // TURF OWNER TEST CASES



    @BeforeEach
     void setUpTurfOwner() {
        turfOwner = new TurfOwner();
        turfOwner.setOwnerId(1L);
        turfOwner.setName("Jane Doe");
        turfOwner.setEmail("jane.doe@example.com");
        turfOwner.setPhoneNumber("9876543210");
        turfOwner.setAddress("456 Main St");
        turfOwner.setBusinessContact("1234567890");
        turfOwner.setBusinessAddress("789 Business St");
        turfOwner.setRegisteredAt(LocalDateTime.now());
    }

    @Test
     void testSaveTurfOwner() {
        when(authServiceClient.getEmail(anyString())).thenReturn("jane.doe@example.com");
        when(authServiceClient.getUserId(anyString())).thenReturn(1L);
        when(turfOwnerRepository.save(any(TurfOwner.class))).thenReturn(turfOwner);

        TurfOwner savedTurfOwner = turfOwnerService.saveTurfOwner("jane_doe", turfOwner);

        assertNotNull(savedTurfOwner);
        assertEquals("jane.doe@example.com", savedTurfOwner.getEmail());
        assertEquals(1L, savedTurfOwner.getOwnerId());
    }

    @Test
     void testSaveTurfOwnerUserNotFoundException() {
        when(authServiceClient.getEmail(anyString())).thenReturn(null);

        assertThrows(ResponseStatusException.class, () -> {
            turfOwnerService.saveTurfOwner("jane_doe", turfOwner);
        });

        when(authServiceClient.getEmail(anyString())).thenReturn("jane.doe@example.com");
        when(authServiceClient.getUserId(anyString())).thenReturn(null);

        assertThrows(ResponseStatusException.class, () -> {
            turfOwnerService.saveTurfOwner("jane_doe", turfOwner);
        });
    }


    @Test
     void testUpdateTurfOwner() {
        when(turfOwnerRepository.findById(anyLong())).thenReturn(Optional.of(turfOwner));
        when(turfOwnerRepository.save(any(TurfOwner.class))).thenReturn(turfOwner);

        TurfOwnerUpdateDTO updatedDetails = new TurfOwnerUpdateDTO();
        updatedDetails.setEmail("john.doe@example.com");
        updatedDetails.setPhoneNumber("1234567890");

        TurfOwner updatedTurfOwner = turfOwnerService.updateTurfOwner(1L, updatedDetails);

        assertNotNull(updatedTurfOwner);
        assertEquals("john.doe@example.com", updatedTurfOwner.getEmail());
        assertEquals("1234567890", updatedTurfOwner.getPhoneNumber());
    }

    @Test
     void testGetTurfOwnerById() {
        when(turfOwnerRepository.findById(anyLong())).thenReturn(Optional.of(turfOwner));

        TurfOwner foundTurfOwner = turfOwnerService.getTurfOwnerById(1L);

        assertNotNull(foundTurfOwner);
        assertEquals(1L, foundTurfOwner.getOwnerId());
    }

    
    @Test
    void deleteTurfOwnerAndTurfs() {
        Long ownerId = 1L;
        TurfOwner turfOwner = new TurfOwner();
        List<TurfDTO> turfs = List.of(new TurfDTO(), new TurfDTO());

        when(turfOwnerRepository.findById(ownerId)).thenReturn(Optional.of(turfOwner));
        when(turfServiceClient.getTurfsByOwnerId(ownerId)).thenReturn(turfs);

        turfOwnerService.deleteTurfOwner(ownerId);

        verify(authServiceClient).deleteUser(ownerId);
        verify(turfServiceClient).getTurfsByOwnerId(ownerId);
        verify(turfServiceClient, times(turfs.size())).deleteTurf(anyLong());
        verify(turfOwnerRepository).delete(turfOwner);
    }
    }
