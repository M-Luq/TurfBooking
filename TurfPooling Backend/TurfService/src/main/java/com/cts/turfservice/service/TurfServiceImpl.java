package com.cts.turfservice.service;

import java.time.Duration;
import java.time.LocalTime;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.cts.turfservice.client.BookingServiceClient;
import com.cts.turfservice.client.OwnerServiceClient;
import com.cts.turfservice.dto.TurfDTO;
import com.cts.turfservice.entity.Turf;
import com.cts.turfservice.exception.TurfAlreadyExistsException;
import com.cts.turfservice.exception.TurfNotFoundException;
import com.cts.turfservice.repository.TurfRepository;

import feign.FeignException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TurfServiceImpl implements TurfService {

    private final TurfRepository turfRepository;
    private final ModelMapper modelMapper;
    private final OwnerServiceClient ownerClient;
    private final BookingServiceClient bookingClient;    

    private static final String TURF_NOT_FOUND = "Turf Not Found";
    private static final Logger log = LoggerFactory.getLogger(TurfService.class);
    
    @Override
    public Turf addTurf(Long id, TurfDTO turfDTO) {
        log.info("Adding new turf for owner ID: {}", id);
        log.debug("TurfDTO received: {}", turfDTO);

        if (turfRepository.existsByNameAndLocation(turfDTO.getName(), turfDTO.getLocation())) {
            log.warn("Turf already exists at location: {}", turfDTO.getLocation());
            throw new TurfAlreadyExistsException("Turf already exists at this location");
        }

        LocalTime openingTime = turfDTO.getOpeningTime();
        LocalTime closingTime = turfDTO.getClosingTime();

        log.debug("Opening time: {}, Closing time: {}", openingTime, closingTime);

        if (openingTime == null || closingTime == null) {
            log.error("Opening or closing time is null.");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Opening and closing times must not be null");
        }

        if (closingTime.equals(openingTime)) {
            log.error("Closing time is the same as opening time.");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Closing time must not be the same as opening time");
        }

        if (closingTime.isBefore(openingTime)) {
            log.warn("Closing time is before opening time. Checking for midnight crossing.");
            if (closingTime.isAfter(LocalTime.MIDNIGHT) && openingTime.isAfter(LocalTime.MIDNIGHT)) {
                log.info("Valid midnight crossing detected.");
            } else {
                log.error("Closing time is before opening time and not a valid midnight crossing.");
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Closing time must be after opening time");
            }
        }

        // Check if exact 1-hour slots can be created within the time range
        if (!canCreateExactOneHourSlots(openingTime, closingTime)) {
            log.error("Cannot create exact 1-hour slots within the given opening and closing times.");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot create exact 1-hour slots within the given opening and closing times");
        }

        Turf newTurf = modelMapper.map(turfDTO, Turf.class);
        newTurf.setOwnerId(id);

        log.debug("Turf entity to be saved: {}", newTurf);

        Turf savedTurf = turfRepository.save(newTurf);

        log.info("New turf added with ID: {}", savedTurf.getId());

        try {
            ownerClient.updateOwnedTurfs(savedTurf.getOwnerId(), String.valueOf(savedTurf.getId()));
            log.info("Owned turfs updated for owner ID: {}", savedTurf.getOwnerId());
        } catch (FeignException e) {
            log.error("Failed to update owned turfs for owner ID: {}. Error: {}", savedTurf.getOwnerId(), e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to update owned turfs due to remote service error.");
        }

        return savedTurf;
    }
    
    @Override
    public Turf updateTurf(Long id, TurfDTO turfDetails) {
        log.info("Updating turf with ID: {}", id);
        log.debug("TurfDTO received for update: {}", turfDetails);

        Turf existingTurf = turfRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Turf with ID: {} not found.", id);
                    return new TurfNotFoundException(TURF_NOT_FOUND);
                });

        LocalTime openingTime = turfDetails.getOpeningTime();
        LocalTime closingTime = turfDetails.getClosingTime();

        log.debug("Opening time: {}, Closing time: {}", openingTime, closingTime);

        if (openingTime == null || closingTime == null) {
            log.error("Opening or closing time is null for turf ID: {}.", id);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Opening and closing times must not be null");
        }

        if (closingTime.equals(openingTime)) {
            log.error("Closing time is the same as opening time for turf ID: {}.", id);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Closing time must not be the same as opening time");
        }

        if (closingTime.isBefore(openingTime)) {
            log.warn("Closing time is before opening time for turf ID: {}. Checking for midnight crossing.", id);
            if (closingTime.isAfter(LocalTime.MIDNIGHT) && openingTime.isAfter(LocalTime.MIDNIGHT)) {
                log.info("Valid midnight crossing detected for turf ID: {}.", id);
            } else {
                log.error("Closing time is before opening time and not a valid midnight crossing for turf ID: {}.", id);
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Closing time must be after opening time");
            }
        }

        // Check if exact 1-hour slots can be created within the time range
        if (!canCreateExactOneHourSlots(openingTime, closingTime)) {
            log.error("Cannot create exact 1-hour slots within the given opening and closing times for turf ID: {}.", id);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot create exact 1-hour slots within the given opening and closing times");
        }

        modelMapper.map(turfDetails, existingTurf);

        log.debug("Updated turf entity to be saved: {}", existingTurf);

        Turf updatedTurf = turfRepository.save(existingTurf);

        log.info("Turf with ID: {} updated successfully.", updatedTurf.getId());

        return updatedTurf;
    }

    private boolean canCreateExactOneHourSlots(LocalTime openingTime, LocalTime closingTime) {
        if (closingTime.isBefore(openingTime)) {
            closingTime = closingTime.plusHours(24); // Handle midnight crossing
        }
        long totalMinutes = Duration.between(openingTime, closingTime).toMinutes();
        return totalMinutes % 60 == 0;
    }

    @Override
    public void deleteTurf(Long id) {
        Turf turf = turfRepository.findById(id)
                .orElseThrow(() -> new TurfNotFoundException(TURF_NOT_FOUND));

        // Delete associated bookings
        bookingClient.deleteBookingsByTurf(id);
        
        ownerClient.removeFavouriteTurfFromAllCustomers(id);

        turfRepository.deleteById(id);

        try {
            ownerClient.removeOwnedTurf(turf.getOwnerId(), String.valueOf(id));
        } catch (FeignException e) {
            log.error("Failed to remove owned turf for owner ID: {}. Error: {}", turf.getOwnerId(), e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to update owned turfs due to remote service error.");
        }
    }

    public List<Turf> getAllTurfs() {
        return turfRepository.findAll();
    }

    public Turf getTurfById(Long id) {
        return turfRepository.findById(id).orElseThrow(() -> new TurfNotFoundException(TURF_NOT_FOUND));
    }

    public List<Turf> getTurfsByOwnerId(Long ownerId) {
        return turfRepository.findByOwnerId(ownerId);
    }
}