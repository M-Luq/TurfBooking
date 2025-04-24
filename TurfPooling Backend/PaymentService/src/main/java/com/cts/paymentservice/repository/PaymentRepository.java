package com.cts.paymentservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cts.paymentservice.entity.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Payment findByBookingId(Long bookingId);
    List<Payment> findByTurfId(Long turfId);
    List<Payment> findByTurfIdAndStatus(Long turfId, String status);
    List<Payment> findByUserId(Long userId);
    List<Payment> findByUserIdAndStatus(Long userId, String status);
    List<Payment> findByTurfIdIn(List<Long> turfIds);
    List<Payment> findByTurfIdInAndStatus(List<Long> turfIds, String status);
    void deleteByTurfId(Long turfId);
}