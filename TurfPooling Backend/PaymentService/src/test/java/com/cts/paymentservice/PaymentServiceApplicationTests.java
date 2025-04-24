package com.cts.paymentservice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.cts.paymentservice.entity.Payment;
import com.cts.paymentservice.exception.PaymentNotFoundException;
import com.cts.paymentservice.repository.PaymentRepository;
import com.cts.paymentservice.service.PaymentServiceImpl;

@ExtendWith(MockitoExtension.class)
class PaymentServiceApplicationTests {

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    private Payment payment;

    @BeforeEach
    void setUp() {
        payment = new Payment();
        payment.setId(1L);
        payment.setBookingId(1L);
        payment.setUserId(1L);
        payment.setTurfId(1L);
        payment.setAmount(100.0);
        payment.setPaymentDate(LocalDateTime.now());
        payment.setStatus("PAID");
    }

    @Test
    void testProcessPayment() {
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

        Payment processedPayment = paymentService.processPayment(1L, 1L, 1L, 100.0);

        assertNotNull(processedPayment);
        assertEquals(1L, processedPayment.getBookingId());
        assertEquals(1L, processedPayment.getUserId());
        assertEquals(1L, processedPayment.getTurfId());
        assertEquals(100.0, processedPayment.getAmount());
        assertEquals("PAID", processedPayment.getStatus());
    }

    @Test
    void testRefundPayment() throws PaymentNotFoundException {
        when(paymentRepository.findByBookingId(anyLong())).thenReturn(payment);
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

        Payment refundedPayment = paymentService.refundPayment(1L);

        assertNotNull(refundedPayment);
        assertEquals("REFUNDED", refundedPayment.getStatus());
    }

    @Test
    void testRefundPaymentPaymentNotFound() {
        when(paymentRepository.findByBookingId(anyLong())).thenReturn(null);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            paymentService.refundPayment(1L);
        });

        assertEquals("Payment not found for booking ID: 1", exception.getMessage());
    }

    @Test
    void testGetPaymentsByTurfId() {
        when(paymentRepository.findByTurfId(anyLong())).thenReturn(Arrays.asList(payment));

        List<Payment> payments = paymentService.getPaymentsByTurfId(1L, null);

        assertNotNull(payments);
        assertFalse(payments.isEmpty());
    }

    @Test
    void testGetPaymentsByTurfIdAndStatus() {
        when(paymentRepository.findByTurfIdAndStatus(anyLong(), anyString())).thenReturn(Arrays.asList(payment));

        List<Payment> payments = paymentService.getPaymentsByTurfId(1L, "PAID");

        assertNotNull(payments);
        assertFalse(payments.isEmpty());
    }

    @Test
    void testGetPaymentsByUserId() {
        when(paymentRepository.findByUserId(anyLong())).thenReturn(Arrays.asList(payment));

        List<Payment> payments = paymentService.getPaymentsByUserId(1L, null);

        assertNotNull(payments);
        assertFalse(payments.isEmpty());
    }

    @Test
    void testGetPaymentsByUserIdAndStatus() {
        when(paymentRepository.findByUserIdAndStatus(anyLong(), anyString())).thenReturn(Arrays.asList(payment));

        List<Payment> payments = paymentService.getPaymentsByUserId(1L, "PAID");

        assertNotNull(payments);
        assertFalse(payments.isEmpty());
    }

    @Test
    void testGetPaymentsByTurfIds() {
        when(paymentRepository.findByTurfIdIn(anyList())).thenReturn(Arrays.asList(payment));

        List<Payment> payments = paymentService.getPaymentsByTurfIds(Arrays.asList(1L), null);

        assertNotNull(payments);
        assertFalse(payments.isEmpty());
    }

    @Test
    void testGetPaymentsByTurfIdsAndStatus() {
        when(paymentRepository.findByTurfIdInAndStatus(anyList(), anyString())).thenReturn(Arrays.asList(payment));

        List<Payment> payments = paymentService.getPaymentsByTurfIds(Arrays.asList(1L), "PAID");

        assertNotNull(payments);
        assertFalse(payments.isEmpty());
    }
}
