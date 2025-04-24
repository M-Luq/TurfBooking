package com.cts.userservice.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.cts.userservice.entity.TurfOwner;

@Repository
public interface TurfOwnerRepository extends JpaRepository<TurfOwner, Long> {
	Optional<TurfOwner> findByEmail(String email);
    Optional<TurfOwner> findByPhoneNumber(String phoneNumber);
    boolean existsByPhoneNumber(String phoneNumber);
    boolean existsByOwnerIdAndRegisteredAtIsNotNull(Long ownerId);
    boolean existsByBusinessContact(String businessContact);
    
    @Query("SELECT o.ownedTurfs FROM TurfOwner o WHERE o.ownerId = :ownerId")
    String findOwnedTurfsByOwnerId(@Param("ownerId") Long ownerId);
}