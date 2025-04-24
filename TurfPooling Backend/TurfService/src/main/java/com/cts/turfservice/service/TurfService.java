package com.cts.turfservice.service;

import java.util.List;

import com.cts.turfservice.dto.TurfDTO;
import com.cts.turfservice.entity.Turf;

public interface TurfService {

    Turf addTurf(Long id,TurfDTO turfDTO);

    Turf updateTurf(Long id, TurfDTO turfDetails);

    void deleteTurf(Long id);

    List<Turf> getAllTurfs();

    Turf getTurfById(Long id);

    List<Turf> getTurfsByOwnerId(Long ownerId);

//    List<Turf> searchTurfs(String location, String sportType, Double minPrice, Double maxPrice);

}