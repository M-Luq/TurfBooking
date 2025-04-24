package com.controller;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dto.ApiResponse;
import com.dto.AuthRequest;
import com.dto.PasswordChangeRequest;
import com.entity.UserInfo;
import com.repository.UserInfoRepository;
import com.service.JwtService;
import com.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService service;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserInfoRepository repo;

    @Autowired
    private AuthenticationManager authenticationManager;
    
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @GetMapping("/welcome")
    public ResponseEntity<ApiResponse<String>> welcome() {
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Welcome this endpoint is not secure", "Welcome"));
    }

    @PostMapping("/new")
    public ResponseEntity<ApiResponse<String>> addNewUser(@Valid @RequestBody UserInfo userInfo) {
        String result = service.addUser(userInfo);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(HttpStatus.CREATED.value(), result));
    }

    @PostMapping("/authenticate")
    public ResponseEntity<ApiResponse<String>> authenticateAndGetToken(@Valid @RequestBody AuthRequest authRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
        if (authentication.isAuthenticated()) {
            Optional<UserInfo> optionalUser = repo.findByName(authRequest.getUsername());
            if (optionalUser.isPresent()) {
                UserInfo obj = optionalUser.get();
                String token = jwtService.generateToken(authRequest.getUsername(), obj.getRoles(), obj.getId());
                return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Authentication successful", token));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(HttpStatus.NOT_FOUND.value(), "User not found"));
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse<>(HttpStatus.UNAUTHORIZED.value(), "Invalid user request"));
        }
    }

    @GetMapping("/getroles/{username}")
    public ResponseEntity<ApiResponse<String>> getRoles(@PathVariable String username) {
        String roles = service.getRoles(username);
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Roles retrieved", roles));
    }

    @GetMapping("/getEmail/{username}")
    public String getEmail(@PathVariable String username) {
        String email = service.getEmail(username);
        return email;
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse<String>> deleteUser(@PathVariable Long id) {
        String result = service.deleteUser(id);
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), result));
    }

    @PutMapping("/update")
    public ResponseEntity<ApiResponse<String>> changePassword(@Valid @RequestBody PasswordChangeRequest request) {
        logger.info("password from request: {}", request.getNewPassword());
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        String result = service.changePassword(username, request.getNewPassword());
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), result));
    }

    @GetMapping("/getUserId/{username}")
    public Long getUserId(@PathVariable String username) {
        Long userId = service.getUserIdByUsername(username);
        return userId;
    }

    @PutMapping("/updateEmail/{userId}")
    public ResponseEntity<ApiResponse<String>> updateUserEmail(@PathVariable Long userId, @RequestBody String email) {
        String result = service.updateUserEmail(userId, email);
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), result));
    }
}