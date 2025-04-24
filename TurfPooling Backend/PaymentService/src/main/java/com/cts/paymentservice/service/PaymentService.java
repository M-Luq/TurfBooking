package com.cts.paymentservice.service;


import com.cts.paymentservice.entity.Payment;
import com.cts.paymentservice.exception.PaymentNotFoundException;

import java.util.List;

public interface PaymentService {
    Payment processPayment(Long bookingId, Long userId, Long turfId,Double amount);
    Payment refundPayment(Long bookingId) throws PaymentNotFoundException;
    List<Payment> getPaymentsByTurfId(Long turfId,String status);
    List<Payment> getPaymentsByUserId(Long userId,String status);
    List<Payment> getPaymentsByTurfIds(List<Long> turfIds,String status);
	void deleteByTurfId(Long turfId);
}
