package com.introstudio.minibank.controller;

import com.introstudio.minibank.exception.ResourceNotFoundException;
import com.introstudio.minibank.message.response.JwtResponse;
import com.introstudio.minibank.message.request.LoginForm;
import com.introstudio.minibank.message.request.SignUpForm;
import com.introstudio.minibank.model.Constants;
import com.introstudio.minibank.model.Role;
import com.introstudio.minibank.model.RoleName;
import com.introstudio.minibank.model.User;
import com.introstudio.minibank.repository.RoleRepository;
import com.introstudio.minibank.repository.UserRepository;
import com.introstudio.minibank.security.jwt.JwtProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.Set;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    JwtProvider jwtProvider;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginForm loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = jwtProvider.generateJwtToken(authentication);
        return ResponseEntity.ok(JwtResponse.builder()
                .token(jwt)
                .type(Constants.TOKEN_TYPE)
                .build());

    }

    @PostMapping("/signup")
    public ResponseEntity<String> registerUser(@Valid @RequestBody SignUpForm signupRequest) {

        if (userRepository.existsByUsername(signupRequest.getUsername())) {
            return new ResponseEntity<String>("Fail -> Username is already taken!", HttpStatus.BAD_REQUEST);
        }

        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            return new ResponseEntity<String>("Fail -> Email is already taken!", HttpStatus.BAD_REQUEST);
        }

        // Creating user's account
        User user = User.builder()
                .name(signupRequest.getName())
                .username(signupRequest.getUsername())
                .email(signupRequest.getEmail())
                .password(passwordEncoder.encode(signupRequest.getPassword()))
                .build();

        Set<String> strRoles = signupRequest.getRole();
        Set<Role> roles = new HashSet<>();

        strRoles.forEach(role -> {
            switch (role) {
                case "admin":
                    Role adminRole = roleRepository.findByName(RoleName.ROLE_ADMIN)
                            .orElseThrow(() -> new ResourceNotFoundException("Fail! -> Cause: User Role not find"));

                    roles.add(adminRole);
                    break;
                default:
                    Role userRole = roleRepository.findByName(RoleName.ROLE_USER)
                            .orElseThrow(() -> new ResourceNotFoundException("Fail! -> Cause: User Role nont find"));

                    roles.add(userRole);

            }
        });

        user.setRoles(roles);
        userRepository.save(user);


        return ResponseEntity.ok().body("User Registered succesfully");

    }

}
