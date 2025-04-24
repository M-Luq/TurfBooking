package com.cts.paymentservice.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cts.paymentservice.entity.Payment;
import com.cts.paymentservice.exception.PaymentNotFoundException;
import com.cts.paymentservice.repository.PaymentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;

    @Transactional
    public Payment processPayment(Long bookingId, Long userId,Long turfId, Double amount) {
        Payment payment = new Payment();
        payment.setBookingId(bookingId);
        payment.setUserId(userId);
        payment.setAmount(amount);
        payment.setTurfId(turfId);
        payment.setPaymentDate(LocalDateTime.now());
        payment.setStatus("PAID");
        return paymentRepository.save(payment);
    }

    @Transactional
    public Payment refundPayment(Long bookingId) throws PaymentNotFoundException {
        Payment payment = paymentRepository.findByBookingId(bookingId);
        if (payment == null) {
            throw new PaymentNotFoundException("Payment not found for booking ID: " + bookingId);
        }
        payment.setStatus("REFUNDED");
        return paymentRepository.save(payment);
    }

    public List<Payment> getPaymentsByTurfId(Long turfId, String status) {
        if (status == null || status.isEmpty()) {
            return paymentRepository.findByTurfId(turfId);
        } else {
            return paymentRepository.findByTurfIdAndStatus(turfId, status);
        }
    }

    public List<Payment> getPaymentsByUserId(Long userId, String status) {
        if (status == null || status.isEmpty()) {
            return paymentRepository.findByUserId(userId);
        } else {
            return paymentRepository.findByUserIdAndStatus(userId, status);
        }
    }
    
    @Override
    public List<Payment> getPaymentsByTurfIds(List<Long> turfIds, String status) {
        if (status == null || status.isEmpty()) {
            return paymentRepository.findByTurfIdIn(turfIds);
        } else {
            return paymentRepository.findByTurfIdInAndStatus(turfIds, status);
        }
    }
    
    @Override
    public void deleteByTurfId(Long turfId) {
    	List<Payment> payments = paymentRepository.findByTurfId(turfId);
    	for (Payment payment : payments) {
    		paymentRepository.delete(payment);	
		}
        
    }
}