package com.introstudio.minibank.controller;

import com.introstudio.minibank.exception.ResourceNotFoundException;
import com.introstudio.minibank.model.Role;
import com.introstudio.minibank.model.RoleName;
import com.introstudio.minibank.repository.RoleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/roles")
public class RoleController {

    private static final Logger LOGGER = LoggerFactory.getLogger(RoleController.class);

    @Autowired
    private RoleRepository roleRepository;

    @GetMapping("")
    public Page<Role> getRoles (Pageable pageable){
        LOGGER.info("Call service getRoles");
        return roleRepository.findAll(pageable);
    }

    @GetMapping("/{name}")
    public Role findByName(@PathVariable RoleName name){
        LOGGER.info("Call service findByName");
        return roleRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with name : " + name));
    }

    @PostMapping("")
    public Role createRole (@Valid @RequestBody Role role){
        LOGGER.info("Call service createRole");

        return roleRepository.save(role);
    }

}

