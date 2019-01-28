package com.introstudio.minibank.controller;

import com.introstudio.minibank.exception.ResourceNotFoundException;
import com.introstudio.minibank.model.Organization;
import com.introstudio.minibank.repository.OrganizationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/organizations")
public class OrganizationController {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrganizationController.class);

    @Autowired
    private OrganizationRepository organizationRepository;

    @GetMapping("")
    @PreAuthorize("hasRole('ADMIN')")
    public Page<Organization> findAll(@PageableDefault(size = 2, sort = "createdAt") Pageable pageable) {

        LOGGER.info("Call API data");

        return organizationRepository.findAll(pageable);

    }

    @GetMapping("/{name}")
    @PreAuthorize("hasRole('ADMIN')")
    public Organization findByName(@PathVariable String name) {

        LOGGER.info("Call API findByName");

        return organizationRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Organization not found with a name : " + name));

    }

    @PostMapping("")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> create(@Valid @RequestBody Organization organization) {

        LOGGER.info("Call API create");

        if (organizationRepository.existsByName(organization.getName())) {
            return new ResponseEntity<String>("The organization name is already used!", HttpStatus.CONFLICT);
        }

        if (organizationRepository.existsByEmail(organization.getEmail())) {
            return new ResponseEntity<String>("Email is already used!", HttpStatus.CONFLICT);
        }

        return ResponseEntity.ok().body(organizationRepository.save(organization));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> update(@PathVariable UUID id, @Valid @RequestBody Organization organizationReq) {

        LOGGER.info("Call API update");

        if (organizationRepository.existsByName(organizationReq.getName())) {
            return new ResponseEntity<String>("The organization name is already used!", HttpStatus.CONFLICT);
        }

        if (organizationRepository.existsByEmail(organizationReq.getEmail())) {
            return new ResponseEntity<String>("Email is already used!", HttpStatus.CONFLICT);
        }

        return ResponseEntity.ok().body(
                organizationRepository.findById(id)
                        .map(organization -> {
                            organization.setName(organizationReq.getName());
                            organization.setEmail(organizationReq.getEmail());
                            organization.setAddress(organizationReq.getAddress());
                            organization.setWebsite(organizationReq.getWebsite());
                            organization.setPic(organizationReq.getPic());
                            return organizationRepository.save(organization);
                        }).orElseThrow(() -> new ResourceNotFoundException("Organization not found with id : " + id))
        );

    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> delete(@PathVariable UUID id) {

        LOGGER.info("Call API delete");

        return organizationRepository.findById(id)
                .map(organization -> {
                    organizationRepository.delete(organization);
                    return ResponseEntity.ok().build();
                }).orElseThrow(() -> new ResourceNotFoundException("Organization not found with id : " + id));


    }

}
