package com.cts.turfservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cts.turfservice.entity.Turf;
 
@Repository
public interface TurfRepository extends JpaRepository<Turf, Long> {
    List<Turf> findByOwnerId(Long ownerId);
    boolean existsByNameAndLocation(String name, String location);
}
