package com.introstudio.minibank.controller;

import com.introstudio.minibank.exception.ResourceNotFoundException;
import com.introstudio.minibank.message.response.UploadFileResponse;
import com.introstudio.minibank.model.Organization;
import com.introstudio.minibank.repository.OrganizationRepository;
import com.introstudio.minibank.service.FileStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/organizations")
public class OrganizationController {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrganizationController.class);

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private FileStorageService fileStorageService;

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

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public UploadFileResponse upload(@RequestParam("file") MultipartFile file) {

        String fileName = fileStorageService.storeFile(file);

        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/downloadFile/")
                .path(fileName)
                .toUriString();

        return UploadFileResponse.builder()
                .fileName(fileName)
                .fileDownloadUri(fileDownloadUri)
                .fileType(file.getContentType())
                .size(file.getSize())
                .build();
    }

    @GetMapping(value = "/getImage/{fileName:.+}", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<InputStreamResource> getImage(@PathVariable String fileName, HttpServletRequest request) throws IOException {

        Resource imgFile = fileStorageService.loadFileAsResource(fileName);

        return ResponseEntity
                .ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(new InputStreamResource(imgFile.getInputStream()));
    }

    @GetMapping("/downloadFile/{fileName:.+}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName, HttpServletRequest request) {
        // Load file as Resource

        LOGGER.info("FILENAME : " + fileName);

        Resource resource = fileStorageService.loadFileAsResource(fileName);

        // Try to determine file's content type
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            LOGGER.info("Could not determine file type.");
        }

        // Fallback to the default content type if type could not be determined
        if(contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

}
