package com.introstudio.minibank.controller;

import com.introstudio.minibank.exception.ResourceNotFoundException;
import com.introstudio.minibank.model.User;
import com.introstudio.minibank.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserRepository userRepository;

    @GetMapping("")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public Page<User> getUsers (Pageable pageable){
        LOGGER.info("Call service getUsers");
        return userRepository.findAll(pageable);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public User findByUserId(@PathVariable UUID id){
        LOGGER.info("Call service findByUserId");
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id : "+ id));
    }

    @PostMapping("")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public User createUser (@Valid @RequestBody User user){
        return userRepository.save(user);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public User updateUser (@PathVariable UUID id, @Valid @RequestParam String password){
        LOGGER.info("Call service updateUser");
        return userRepository.findById(id)
                .map(user -> {
                    user.setPassword(password.trim());
                    return userRepository.save(user);
                }).orElseThrow(() -> new ResourceNotFoundException("User not found with id : " + id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<?> deleteUser (@PathVariable UUID id){
        LOGGER.info("Call service deleteUser");
        return userRepository.findById(id)
                .map(user -> {
                    userRepository.delete(user);
                    return ResponseEntity.ok().build();
                }).orElseThrow(() -> new ResourceNotFoundException("User not found with id : " + id));
    }

}
