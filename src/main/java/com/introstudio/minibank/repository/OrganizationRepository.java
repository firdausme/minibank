package com.introstudio.minibank.repository;

import com.introstudio.minibank.model.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrganizationRepository extends JpaRepository<Organization, UUID> {

    Optional<Organization> findByName(String name);
    Boolean existsByName(String name);
    Boolean existsByEmail(String email);

}
