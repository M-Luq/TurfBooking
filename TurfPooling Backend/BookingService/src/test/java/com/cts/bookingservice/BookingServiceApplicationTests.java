package com.cts.bookingservice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.cts.bookingservice.client.PaymentClient;
import com.cts.bookingservice.client.TurfClient;
import com.cts.bookingservice.client.UserServiceClient;
import com.cts.bookingservice.dto.BookingRequest;
import com.cts.bookingservice.dto.TurfDTO;
import com.cts.bookingservice.entity.Booking;
import com.cts.bookingservice.entity.BookingStatus;
import com.cts.bookingservice.repository.BookingRepository;
import com.cts.bookingservice.service.BookingServiceImpl;

@ExtendWith(MockitoExtension.class)
class BookingServiceApplicationTests {
	    @Mock
	    private BookingRepository bookingRepository;

	    @Mock
	    private TurfClient turfClient;

	    @Mock
	    private PaymentClient paymentClient;

	    @Mock
	    private UserServiceClient userServiceClient;

	    @InjectMocks
	    private BookingServiceImpl bookingService;

	    private Booking booking;
	    private TurfDTO turfDTO;
	    private BookingRequest bookingRequest;

	    @BeforeEach
	     void setUp() {
	        booking = Booking.builder()
	                .id(1L)
	                .turfId(1L)
	                .turfName("Test Turf")
	                .userId(1L)
	                .bookingDateTime(LocalDateTime.now())
	                .startTime(LocalTime.of(10, 0))
	                .endTime(LocalTime.of(12, 0))
	                .slotInfo("2-hour")
	                .status(BookingStatus.PENDING)
	                .turfPrice(100.0)
	                .build();

	        turfDTO = new TurfDTO();
	        turfDTO.setId(1L);
	        turfDTO.setName("Test Turf");
	        turfDTO.setOpeningTime(LocalTime.of(8, 0));
	        turfDTO.setClosingTime(LocalTime.of(22, 0));
	        turfDTO.setPricePerHour(50.0);

	        bookingRequest = new BookingRequest();
	        bookingRequest.setStartTime(LocalTime.of(10, 0));
	        bookingRequest.setEndTime(LocalTime.of(12, 0));
	    }

	    @Test
	     void testCreateBooking() {
	        when(turfClient.getTurfById(anyLong())).thenReturn(turfDTO);
	        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

	        Booking createdBooking = bookingService.createBooking(1L, 1L, bookingRequest);

	        assertNotNull(createdBooking);
	        assertEquals(1L, createdBooking.getTurfId());
	        assertEquals("Test Turf", createdBooking.getTurfName());
	        assertEquals(1L, createdBooking.getUserId());
	    }

	    @Test
	     void testCreateBookingTurfNotFound() {
	        when(turfClient.getTurfById(anyLong())).thenReturn(null);

	        Exception exception = assertThrows(RuntimeException.class, () -> {
	            bookingService.createBooking(1L, 1L, bookingRequest);
	        });

	        assertEquals("Turf not found", exception.getMessage());
	    }

	    @Test
	     void testIsSlotAvailable() {
	        when(bookingRepository.findByTurfIdAndStatus(anyLong(), any(BookingStatus.class)))
	                .thenReturn(Arrays.asList(booking));

	        boolean isAvailable = bookingService.isSlotAvailable(1L, LocalTime.of(8, 0), LocalTime.of(10, 0));

	        assertTrue(isAvailable);
	    }

	    @Test
	     void testIsSlotNotAvailable() {
	        when(bookingRepository.findByTurfIdAndStatus(anyLong(), any(BookingStatus.class)))
	                .thenReturn(Arrays.asList(booking));

	        boolean isAvailable = bookingService.isSlotAvailable(1L, LocalTime.of(10, 0), LocalTime.of(12, 0));

	        assertFalse(isAvailable);
	    }

	    @Test
	     void testAvailableSlots() {
	        when(turfClient.getTurfById(anyLong())).thenReturn(turfDTO);
	        when(bookingRepository.findByTurfIdAndStatus(anyLong(), any(BookingStatus.class)))
	                .thenReturn(Arrays.asList());

	        List<String> slots = bookingService.availableSlots(1L);

	        assertFalse(slots.isEmpty());
	    }

//	    @Test
//	     void testProcessPayment() {
//	        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
//	        doNothing().when(paymentClient).processPayment(anyLong(), anyLong(), anyLong(), anyDouble());
//	        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
//
//	        Booking confirmedBooking = bookingService.processPayment(1L);
//
//	        assertNotNull(confirmedBooking);
//	        assertEquals(BookingStatus.CONFIRMED, confirmedBooking.getStatus());
//	    }
//
//	    @Test
//	     void testProcessRefund() {
//	        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
//	        doNothing().when(paymentClient).refundPayment(anyLong());
//	        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
//
//	        Booking cancelledBooking = bookingService.processRefund(1L);
//
//	        assertNotNull(cancelledBooking);
//	        assertEquals(BookingStatus.CANCELLED, cancelledBooking.getStatus());
//	    }

	    @Test
	     void testGetBookingById() {
	        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

	        Booking foundBooking = bookingService.getBookingById(1L);

	        assertNotNull(foundBooking);
	        assertEquals(1L, foundBooking.getId());
	    }

	    @Test
	     void testGetBookingsByUserId() {
	        when(bookingRepository.findByUserId(anyLong())).thenReturn(Arrays.asList(booking));

	        List<Booking> bookings = bookingService.getBookingsByUserId(1L);

	        assertNotNull(bookings);
	        assertFalse(bookings.isEmpty());
	    }

	    @Test
	     void testGetBookingsByTurfIds() {
	        when(bookingRepository.findByTurfIdIn(anyList())).thenReturn(Arrays.asList(booking));

	        List<Booking> bookings = bookingService.getBookingsByTurfIds(Arrays.asList(1L));

	        assertNotNull(bookings);
	        assertFalse(bookings.isEmpty());
	    }

	    @Test
	     void testGetTurfIdsByOwner() {
	        when(userServiceClient.getTurfIdsByOwner(anyLong())).thenReturn(Arrays.asList(1L));

	        List<Long> turfIds = bookingService.getTurfIdsByOwner(1L);

	        assertNotNull(turfIds);
	        assertFalse(turfIds.isEmpty());
	    }
	    
//	    @Test
//	     void testAddFavouriteTurf() {
//	        doNothing().when(userServiceClient).addFavouriteTurf(anyLong(), anyLong());
//	        String response = bookingService.addFavouriteTurf(1L, 1L);
//	        assertEquals("Turf added to favourites", response);
//	        verify(userServiceClient, times(1)).addFavouriteTurf(anyLong(), anyLong());
//	    }
//
//	    @Test
//	     void testRemoveFavouriteTurf() {
//	        doNothing().when(userServiceClient).removeFavouriteTurf(anyLong(), anyLong());
//	        String response = bookingService.removeFavouriteTurf(1L, 1L);
//	        assertEquals("Turf removed from favourites", response);
//	        verify(userServiceClient, times(1)).removeFavouriteTurf(anyLong(), anyLong());
//	    }
	    
	    
	}
