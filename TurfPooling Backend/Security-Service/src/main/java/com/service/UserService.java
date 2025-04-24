package com.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.dto.PasswordChangeRequest;
import com.entity.UserInfo;
import com.repository.UserInfoRepository;

import jakarta.validation.Valid;

@Service
public class UserService {

    @Autowired
    private UserInfoRepository repository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public String addUser(UserInfo userInfo) {
        if (repository.findByName(userInfo.getName()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "This UserName is Already Registered.");
        }
        if (repository.findByEmail(userInfo.getEmail()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "This Email is Already Registered.");
        }

        userInfo.setPassword(passwordEncoder.encode(userInfo.getPassword()));
        repository.save(userInfo);
        return "Registration Successful";

    }

    public String deleteUser(Long id) {
        UserInfo userInfo = repository.findById(id).orElse(null);
        if (userInfo == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found.");
        }
        repository.delete(userInfo);
        return "User deleted successfully.";
    }

    public String changePassword(String username, String password) {
        UserInfo existingUser = repository.findByName(username).orElse(null);
        if (existingUser == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found.");
        }
        existingUser.setPassword(passwordEncoder.encode(password));
        repository.save(existingUser);
        return existingUser.getRoles().equals("TURFOWNER") ? "TURFOWNER updated successfully." : "CUSTOMER updated successfully.";
    }

    public String getRoles(String username) {
        return repository.findByName(username)
                .map(UserInfo::getRoles)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found."));
    }

    public String getEmail(String username) {
        return repository.findByName(username)
                .map(UserInfo::getEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found."));
    }

    public Long getUserIdByUsername(String username) {
        return repository.findByName(username)
                .map(UserInfo::getId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found."));
    }
    
    public String updateUserEmail(Long userId, String email) {
        UserInfo user = repository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found."));

        // Check if the email is already in use by another user
        if (repository.existsByEmail(email) && !user.getEmail().equals(email)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email is already in use.");
        }

        user.setEmail(email);
        repository.save(user);
        return "Email updated successfully";
    }
}