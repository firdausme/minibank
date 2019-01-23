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
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;

@RestController
@RequestMapping("/v1/users")
public class UserController {

    private Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserRepository userRepository;

    @GetMapping("")
    public Page<User> getUsers (Pageable pageable){
        logger.info("Call service getUsers");
        return userRepository.findAll(pageable);
    }

    @GetMapping("/{userId}")
    public User findByUserId(@PathVariable Long userId){
        logger.info("Call service findByUserId");
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id : " + userId));
    }


    @PostMapping("")
    public User createUser (@Valid @RequestBody User user){
        return userRepository.save(user);
    }

//    @PutMapping("/{userId}")
//    public User updateUser (@PathVariable Long userId, @Valid @RequestBody User userReq){
//        logger.info("Call service updateUser");
//        return userRepository.findById(userId)
//                .map(user -> {
//                    user.setName(userReq.getName());
//                    return userRepository.save(user);
//                }).orElseThrow(() -> new ResourceNotFoundException("User not found with id : " + userId));
//    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<?> deleteUser (@PathVariable Long userId){
        logger.info("Call service deleteUser");
        return userRepository.findById(userId)
                .map(user -> {
                    userRepository.delete(user);
                    return ResponseEntity.ok().build();
                }).orElseThrow(() -> new ResourceNotFoundException("User not found with id : " + userId));
    }

}
