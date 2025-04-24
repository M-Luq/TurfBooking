package com.cts.userservice.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.cts.userservice.clients.AuthServiceClient;
import com.cts.userservice.clients.TurfServiceClient;
import com.cts.userservice.dto.TurfDTO;
import com.cts.userservice.dto.TurfOwnerUpdateDTO;
import com.cts.userservice.entity.TurfOwner;
import com.cts.userservice.repository.TurfOwnerRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import feign.FeignException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TurfOwnerServiceImpl implements TurfOwnerService {

    private final TurfOwnerRepository turfOwnerRepository;
    private final AuthServiceClient authServiceClient;
    private final TurfServiceClient turfServiceClient;
    private final ObjectMapper objectMapper;

    private static final String OWNER_NOT_FOUND = "Turf Owner Not Found with this ID: ";

    @Override
    @Transactional
    public TurfOwner saveTurfOwner(String username, TurfOwner owner) {
        if (username == null || owner == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username and owner details cannot be null");
        }

        try {
            String email = authServiceClient.getEmail(username);
            if (email == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "User not found with this username in the authentication service. " +
                                "Please ensure the user is registered and logged in.");
            }
            owner.setEmail(email);

            Long userId = authServiceClient.getUserId(username);
            if (userId == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "User not found with this username in the authentication service. " +
                                "Please ensure the user is registered and logged in.");
            }
            owner.setOwnerId(userId);

            if (turfOwnerRepository.existsByPhoneNumber(owner.getPhoneNumber())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Phone number already exists");
            }

            owner.setRegisteredAt(LocalDateTime.now());
            return turfOwnerRepository.save(owner);
        } catch (FeignException e) {
            String errorMessage = extractErrorMessage(e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error communicating with auth service: " + errorMessage);
        }
    }

    @Override
    public boolean isRegistered(Long ownerId) {
        return turfOwnerRepository.existsByOwnerIdAndRegisteredAtIsNotNull(ownerId);
    }

    @Override
    @Transactional
    public TurfOwner updateTurfOwner(Long id, TurfOwnerUpdateDTO updatedDetails) {
        TurfOwner owner = turfOwnerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, OWNER_NOT_FOUND + id));

        if (updatedDetails == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Updated details cannot be null");
        }

        try {
            if (updatedDetails.getEmail() != null && !updatedDetails.getEmail().equals(owner.getEmail())) {
                authServiceClient.updateUserEmail(owner.getOwnerId(), updatedDetails.getEmail());
                owner.setEmail(updatedDetails.getEmail());
            }

            if (updatedDetails.getPhoneNumber() != null && !updatedDetails.getPhoneNumber().equals(owner.getPhoneNumber())) {
                if (turfOwnerRepository.existsByPhoneNumber(updatedDetails.getPhoneNumber())) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Phone number already exists");
                }
                owner.setPhoneNumber(updatedDetails.getPhoneNumber());
            }

            if (updatedDetails.getAddress() != null) {
                owner.setAddress(updatedDetails.getAddress());
            }

            if (updatedDetails.getName() != null) {
                owner.setName(updatedDetails.getName());
            }

            if (updatedDetails.getBusinessContact() != null && !updatedDetails.getBusinessContact().equals(owner.getBusinessContact())) {
                if (turfOwnerRepository.existsByBusinessContact(updatedDetails.getBusinessContact())) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Business contact already exists");
                }
                owner.setBusinessContact(updatedDetails.getBusinessContact());
            }

            if (updatedDetails.getBusinessAddress() != null) {
                owner.setBusinessAddress(updatedDetails.getBusinessAddress());
            }

            owner.setUpdatedAt(LocalDateTime.now());
            return turfOwnerRepository.save(owner);
        } catch (FeignException e) {
            String errorMessage = extractErrorMessage(e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error communicating with auth service: " + errorMessage);
        }
    }

    @Override
    public TurfOwner getTurfOwnerById(Long id) {
        return turfOwnerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, OWNER_NOT_FOUND + id));
    }

    @Override
    public List<TurfOwner> getAllTurfOwners() {
        return turfOwnerRepository.findAll();
    }

    @Override
    @Transactional
    public void deleteTurfOwner(Long id) {
        try {
            authServiceClient.deleteUser(id);
            TurfOwner turfOwner = getTurfOwnerById(id);

            List<TurfDTO> turfs = turfServiceClient.getTurfsByOwnerId(id);
            for (TurfDTO turfDTO : turfs) {
                turfServiceClient.deleteTurf(turfDTO.getId());
            }

            turfOwnerRepository.delete(turfOwner);
        } catch (FeignException e) {
            String errorMessage = extractErrorMessage(e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error communicating with remote service: " + errorMessage);
        }
    }

    @Override
    @Transactional
    public void updateOwnedTurfs(Long ownerId, String turfId) {
        TurfOwner owner = turfOwnerRepository.findById(ownerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, OWNER_NOT_FOUND + ownerId));
        String ownedTurfs = owner.getOwnedTurfs();
        if (ownedTurfs == null || ownedTurfs.isEmpty()) {
            ownedTurfs = turfId;
        } else {
            ownedTurfs += "," + turfId;
        }
        owner.setOwnedTurfs(ownedTurfs);
        turfOwnerRepository.save(owner);
    }

    @Override
    @Transactional
    public void removeOwnedTurf(Long ownerId, String turfId) {
        TurfOwner owner = turfOwnerRepository.findById(ownerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, OWNER_NOT_FOUND + ownerId));
        String ownedTurfs = owner.getOwnedTurfs();
        if (ownedTurfs != null && !ownedTurfs.isEmpty()) {
            ownedTurfs = Arrays.stream(ownedTurfs.split(","))
                    .filter(id -> !id.equals(turfId))
                    .collect(Collectors.joining(","));
            owner.setOwnedTurfs(ownedTurfs);
            turfOwnerRepository.save(owner);
        }
    }

    @Override
    public List<Long> getTurfIdsByOwnerId(Long ownerId) {
        String ownedTurfs = turfOwnerRepository.findOwnedTurfsByOwnerId(ownerId);
        if (ownedTurfs == null || ownedTurfs.isEmpty()) {
            return new ArrayList<>();
        }
        return Arrays.stream(ownedTurfs.split(","))
                .map(Long::parseLong)
                .collect(Collectors.toList());
    }

    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    private String extractErrorMessage(FeignException e) {
        try {
            String responseBody = e.contentUTF8();
            if (responseBody != null && !responseBody.isEmpty()) {
                Map<String, Object> errorMap = objectMapper.readValue(responseBody, Map.class);
                if (errorMap.containsKey("message")) {
                    return (String) errorMap.get("message");
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return e.getMessage();
    }
}